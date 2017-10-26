/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package virtual.machine;

import types.TypesInfo;
import common.VarType;
import java.io.UnsupportedEncodingException;
import virtual.machine.memory.Memory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import program.builder.BinBuilderClassesMetaInfo;
import program.builder.BinObjBuilder;
import program.builder.BinaryReader;
import types.TypeString;
import virtual.machine.memory.MemoryHeap;
import virtual.machine.memory.MemoryManager;
import virtual.machine.memory.MemoryProgram;
import virtual.machine.memory.MemoryStack;
import virtual.machine.memory.VmSysRegister;

/**
 *
 * @author Andrey
 */
public class VM {
    protected Instructions instructionsService;
    protected DataBinConvertor binConvertorService;
  
    
    
    public static  int ADDR_SIZE = 4;
    public static  int INT_SIZE = 4;
    public static int COMMAND_SIZE =  1 + INT_SIZE; 
    
    protected MemoryManager memoryManager;
    protected int pos;
    protected int offset;
    protected Program program;
    
    protected int[] varAddr;
    protected TypesInfo typesInfo;
    protected VMAddrTables addrTables;
    
    public VM(){
        this.binConvertorService =  DataBinConvertor.getInstance();
        this.instructionsService = Instructions.getInstance();
        this.memoryManager = new MemoryManager(binConvertorService);
        this.typesInfo = TypesInfo.getInstance();
    }
    

    
    protected void allocateVariables() throws VMOutOfMemoryException{
        MemoryHeap memHeap = getMemHeap();
        int secStart = program.readHeader(VmExeHeader.VarTableStart);
        int secEnd = program.readHeader(VmExeHeader.ClassesMetaInfoStart);
        
        BinaryReader binReader = new BinaryReader(program.getData());
        binReader.setCurPos(secStart);
 
        while( binReader.getCurPos() < secEnd){
             int varInd = binReader.readIntAndNext();
             int varSize = binReader.readIntAndNext();        
             int ptrAddr = memHeap.memAlloc(varSize);
             memHeap.putValue(ptrAddr, varSize);
             addrTables.setAddrForIndex(VmExeHeader.VarTableSize, varInd, ptrAddr);
        }
    }
    
    protected void allocateData() throws VMOutOfMemoryException{
        int secStart = program.readHeader(VmExeHeader.ConstStart);
        int secEnd = program.readHeader(VmExeHeader.VarTableStart);
        
        MemoryHeap memHeap = getMemHeap();
        
        ArrayList<Byte> progData = program.getData();
        
        BinaryReader binReader = new BinaryReader(program.getData());
        binReader.setCurPos(secStart);
 
        while( binReader.getCurPos() < secEnd){ 
            int varInd =  binReader.readIntAndNext();
            int varSize =  binReader.readIntAndNext();           

            int ptrAddr = memHeap.memAlloc(varSize+ INT_SIZE);
            memHeap.putValue(ptrAddr, varSize);
            memHeap.putValue(ptrAddr+ INT_SIZE, progData, binReader.getCurPos() , binReader.getCurPos() +   varSize);
            addrTables.setAddrForIndex(VmExeHeader.ConstTableSize, varInd, ptrAddr);
            
            //System.out.println(String.format("VarInd: %s with value %s", varInd, memHeap.getIntValue(ptrAddr+ INT_SIZE)));
            binReader.nextBytes(varSize);  
        }
    }
    
    protected void allocateClassesMetaInfo() throws VMOutOfMemoryException, VmExecutionExeption{

        int secStart = program.readHeader(VmExeHeader.ClassesMetaInfoStart);
        int secEnd = program.readHeader(VmExeHeader.InstructionsStart);
        
        MemoryHeap memHeap = getMemHeap();
        
        BinaryReader binReader = new BinaryReader(program.getData());
        binReader.setCurPos(secStart);
 
        while( binReader.getCurPos() < secEnd ){ 
            int classInd = binReader.readIntAndNext();
            int metaInfoSize = binReader.readInt();
            int metaTablePtr =  memHeap.memAlloc(metaInfoSize);
            System.out.println(String.format("Class_ID: %s MetaSize: %s", classInd, metaInfoSize)  );
            addrTables.setAddrForIndex(VmExeHeader.ClassesTableSize, classInd, metaTablePtr);
            
            binReader.prevBytes(VM.INT_SIZE);
            Byte[] metaData =  binReader.readAndNextBytes(metaInfoSize + VM.INT_SIZE);
            memHeap.putValue(metaTablePtr, metaData);
            printClassMetaInfo(classInd);
        }
        
    }
    
    protected int getClassId(int objPtr){ 
        //First field is size, second is classID, third is linkCount
        int classId = getMemHeap().getIntValue(objPtr + VM.INT_SIZE);
        return classId;
    }
    
    protected int getClassMetaDataPointer(int classId){ 
   
        Integer classMetaDataPtr = addrTables.getAddrByIndex(VmExeHeader.ClassesTableSize, classId); 
        
        return classMetaDataPtr;
    }
    
    protected int getFieldOffset(int classMetaDataPtr, int fieldNum) throws VmExecutionExeption{
        BinaryReader binReader = new BinaryReader(this.getMemHeap().getData());
        int fieldsCount = readClassMetaDataHeader(classMetaDataPtr, VmMetaClassHeader.FIELDS_COUNT);
        int methodsCount = readClassMetaDataHeader(classMetaDataPtr, VmMetaClassHeader.METHODS_COUNT);
        int headersSize = BinBuilderClassesMetaInfo.HEADERS_SIZE * VM.INT_SIZE;
        int fieldsStart = headersSize + methodsCount * 2 * VM.INT_SIZE;
        
        binReader.setCurPos(classMetaDataPtr + fieldsStart);
        
        boolean flag = true;
        int offset = 0;
        
        int k = 0;
        while(k < fieldsCount){
            int fieldCode = binReader.readIntAndNext();
            
            if(fieldCode == fieldNum){
                return offset;
            }
            
            int fieldSize = binReader.readIntAndNext();
            offset += fieldSize;
            k++;
        }
        
       return -1;
    }
    
    protected int readClassMetaDataHeader(int classMetaDataPtr, VmMetaClassHeader header){
        return getMemHeap().getIntValue(classMetaDataPtr + header.ordinal() * VM.INT_SIZE);
    }
    
    protected int getFieldOffsetObj(int objPtr, int fieldNum) throws VmExecutionExeption{
         int classId = getClassId(objPtr);
         int metaDataPtr = this.getClassMetaDataPointer(classId);
         int fieldOffset = getFieldOffset(metaDataPtr, fieldNum);
         
         if(fieldOffset > -1){
             return fieldOffset;
         }else {    
            int parentClassId = readClassMetaDataHeader(metaDataPtr, VmMetaClassHeader.PARENT_ID); 
            while(parentClassId > -1){
                metaDataPtr = this.getClassMetaDataPointer(parentClassId);
                fieldOffset = getFieldOffset(metaDataPtr, fieldNum);
                if(fieldOffset > -1) return fieldOffset;
                parentClassId = readClassMetaDataHeader(metaDataPtr, VmMetaClassHeader.PARENT_ID); 
            }
            throw new VmExecutionExeption(String.format("Field with number %s not found", fieldNum));
         }
         
         
    }
    protected void printClassMetaInfo(int classId) throws VmExecutionExeption{
        int metaDataPtr = this.getClassMetaDataPointer(classId);
        int fieldsCount = readClassMetaDataHeader(metaDataPtr, VmMetaClassHeader.FIELDS_COUNT);
        int methodsCount = readClassMetaDataHeader(metaDataPtr, VmMetaClassHeader.METHODS_COUNT);
        int parentId = readClassMetaDataHeader(metaDataPtr, VmMetaClassHeader.PARENT_ID);
        
        System.out.println(String.format("Fields: %s, methods: %s.Parent_Id: %s", fieldsCount, methodsCount, parentId));
        
        int fieldOffset = getFieldOffset(metaDataPtr, 1);
         System.out.println(String.format("Offset of field %s is %s", 1, fieldOffset));
        
    }
    
    /**
     * It's possible not pretty, because we have postfix Size, however name without it
     * For example we have table of Const addresses, but tableType named constTableSize
     * Naturally later we should create more elegant solution
     * @param tableType 
     */
   
    protected void createDescrTables() throws VMOutOfMemoryException {
       this.addrTables.add(VmExeHeader.ConstTableSize);
       this.addrTables.add(VmExeHeader.VarTableSize);
       this.addrTables.add(VmExeHeader.ClassesTableSize);
    }
    
    protected MemoryHeap getMemHeap(){
        MemoryHeap heap = this.memoryManager.getMemHeap();
        return heap;
    }
    
    protected VMCommands getMnemonic(Byte code){
       return VMCommands.values()[code];
    }
    
    protected VarType getVarTypeMnemonic(Byte code){
       return VarType.values()[code];
    }
    

    
    protected void translateAdresses(){
        int progStart = memoryManager.getSysRegister(VmSysRegister.ProgOffsetAddr);
        int progEnd = memoryManager.getSysRegister(VmSysRegister.ProgEndAddr);
        
        MemoryHeap memHeap = getMemHeap();        
   
        for(int i = progStart; i < progEnd; i += COMMAND_SIZE){
            Byte commandCode = memHeap.getValue(i, 1)[0];
            VMCommands command = getMnemonic(commandCode);
            int constAdrPtr =0;
            int addr = getMemHeap().getIntValue(i + 1);
            int varAdrPtr;
            switch(command){
                case Jmp:
                   
                    //Convert relative adress to absolute
                    
                    if(addr !=0 ){
                        int absAddr =  addr + progStart; 
                    memoryManager.putValue(i + 1, absAddr);
                    }
                    
                    break;
                case Push:
                  
                    constAdrPtr = addrTables.getAddrByIndex(VmExeHeader.ConstTableSize, addr); 
                    
                    memHeap.putValue(i + 1, constAdrPtr);
                    memHeap.putValue(i, (byte)VMCommands.Push_Addr.ordinal());
   
                    
                    break;
                case Push_Addr_NO_UNBOX:
                    constAdrPtr = addrTables.getAddrByIndex(VmExeHeader.ConstTableSize, addr); 
                    
                    memHeap.putValue(i + 1, constAdrPtr);
                    break;
                case Invoke_Sys_Function: case Var_Declare_Local: //case Var_Load_Local: case Var_Put_Local:
                    constAdrPtr = addrTables.getAddrByIndex(VmExeHeader.ConstTableSize, addr); 
                    memHeap.putValue(i + 1, constAdrPtr);
                     
                    break;
                case Var_Load: 
                    varAdrPtr = addrTables.getAddrByIndex(VmExeHeader.VarTableSize, addr); 
                    memHeap.putValue(i, (byte)VMCommands.Push_Addr.ordinal());
                    memHeap.putValue(i + 1, varAdrPtr);
                    
                    break;
                case Var_Put:
                    
                    varAdrPtr = addrTables.getAddrByIndex(VmExeHeader.VarTableSize, addr); 
                    memHeap.putValue(i + 1, varAdrPtr);
                    // Change Push to Push ptr of value.
                   
                    break;
            }
        }
    }
    
    
    
    protected int stackPopInt() throws VmStackEmptyPop{
        return binConvertorService.bytesToInt(memoryManager.getMemStack().pop(), 0);
    }
    
    protected void dbgFirstCommand(){
             int startAddr = memoryManager.getSysRegister(VmSysRegister.ProgOffsetAddr);
        Byte commandCode = getMemHeap().getValue(startAddr, 1)[0];
            VMCommands commandD = getMnemonic(commandCode);
            System.err.println("Command Deg "  + commandD.toString());
    }
    
    protected int sysMemAllocStack() throws VmStackEmptyPop, VMStackOverflowException {
         MemoryStack memStack = this.memoryManager.getMemStack();
         int dataSize = stackPopInt();
         
         Byte[] data = new Byte[dataSize];
         int ptrStart = memStack.push(data);
         memStack.push(binConvertorService.integerToByte(ptrStart)); 
         
         System.out.println(String.format("Allocate on stack: %s in addr# %s", dataSize, ptrStart));
         return ptrStart;
    }
    
    protected int sysMemAlloc() throws VmStackEmptyPop, VMOutOfMemoryException, VMStackOverflowException{
        MemoryHeap memHeap = this.memoryManager.getMemHeap();
        MemoryStack memStack = this.memoryManager.getMemStack();
        
        int dataSize = stackPopInt();
        int ptrStart = memHeap.memAlloc(dataSize);
        
        memStack.push(binConvertorService.integerToByte(ptrStart)); 
        return ptrStart;
    }
    
    protected int sysMemAllocPtr() throws VmStackEmptyPop, VMOutOfMemoryException, VMStackOverflowException{
        MemoryHeap memHeap = this.memoryManager.getMemHeap();
        MemoryStack memStack = this.memoryManager.getMemStack();
        
        int dataSize = stackPopInt();
        int ptrStart = memHeap.memAlloc(dataSize + VM.INT_SIZE);
        memHeap.putValue(ptrStart, dataSize);
        
        
        memStack.push(binConvertorService.integerToByte(ptrStart)); 
        return ptrStart;
    }
    
    
    
    protected void sysSetPtrField() throws VmExecutionExeption{
        //TODO: Absolutley need to count field address, not ptr
        MemoryHeap memHeap = this.memoryManager.getMemHeap();
        MemoryStack memStack = this.memoryManager.getMemStack();
        
        
        int fieldNum = stackPopInt();
        Byte[] fieldValue = memStack.pop();
        int ptrAddr = stackPopInt();
        
        int fieldOffset = fieldNum * INT_SIZE;
        if(fieldNum > 1){
             fieldOffset = getFieldOffsetObj(ptrAddr, fieldNum);
        }
        
        memHeap.putValue(ptrAddr + VM.INT_SIZE + fieldOffset, fieldValue);
        
       // memHeap.putValue(ptrAddr, fieldValue);
       
        
    }
    
    protected void sysPrint() throws  VmExecutionExeption, UnsupportedEncodingException{
    //    stackPopInt();
        int ptrAddr = stackPopInt();
        MemoryStack memStack = this.memoryManager.getMemStack();
        Byte[] val =  getMemHeap().getPtrByteValue(ptrAddr);
        TypeString stringBinConv = (TypeString)TypesInfo.getInstance().getConvertor(VarType.String);
        
        System.err.println("SYS_PRINT: " + stringBinConv.getValue(val));
    }
    
    protected void sysPrintObjField() throws  VmExecutionExeption{
        int fieldNum = stackPopInt();
        int ptrAddr = stackPopInt();
        int fieldOffset = getFieldOffsetObj(ptrAddr, fieldNum);
        int value = getMemHeap().getIntValue(ptrAddr + VM.INT_SIZE + fieldOffset);
        System.err.println(String.format("PRINT_OBJ_FIELD: Value: %s FieldNum: %s  ", value, fieldNum) );
    }
    
    protected void callSysFunc(int funcTypeAddrPtr) throws VmExecutionExeption, UnsupportedEncodingException{   
        MemoryStack memStack = this.memoryManager.getMemStack();
        int funcType  = memStack.getIntPtrValue(funcTypeAddrPtr);
        int arg;
        int retVal;
        
        VMSysFunction sysFunc = VMSysFunction.values()[funcType];
      
         System.err.println("Sys func called: " + sysFunc.toString() + "(" + funcTypeAddrPtr + ")");
        switch(sysFunc){
            case MemAlloc:
                retVal = sysMemAlloc();
                System.out.println("Mem alloc at adress: " + retVal);
                break;
            case MemAllocStack:
                sysMemAllocStack();
                break;
            case MemAllocPtr:
                sysMemAllocPtr();
                break;
            case GetRegister:
                arg = stackPopInt();
                VmSysRegister register = VmSysRegister.values()[arg];
                int regValue = memoryManager.getSysRegister(register);
                
                //This registres is used to store addresses, so it's need to be transformed to absolute address
                if(register == VmSysRegister.F1){
                    regValue += memoryManager.getSysRegister(VmSysRegister.ProgOffsetAddr);
                }
                
                
                memStack.push(binConvertorService.integerToByte(regValue));
                break;
            case SetRegister:
                arg = stackPopInt();
                regValue = stackPopInt();  
                memoryManager.setSysRegister(VmSysRegister.values()[arg], regValue);
                break;
            case SetPtrField:
                sysSetPtrField();
                break;
            case Print:
                sysPrint();
                break;
            case PrintObjField:
                sysPrintObjField();
                break;
            default:
                System.err.println("Callede unreliased function: " + sysFunc.toString());
        }
    }
    
    public void run(Program program) throws Exception{
     // try{  
        this.program = program;
        
        
        int startInstrsInd = program.readHeader(VmExeHeader.InstructionsStart);
        int progStartPoint = program.readHeader(VmExeHeader.ProgramStartPoint);
        
        memoryManager.allocateProgramInstructions(program.getData(), startInstrsInd);
        int startAddr = progStartPoint + memoryManager.getSysRegister(VmSysRegister.ProgOffsetAddr);
        
        this.addrTables = new VMAddrTables(program, getMemHeap());
        this.createDescrTables();
        this.allocateData();
        this.allocateVariables();
        this.allocateClassesMetaInfo();
        this.translateAdresses();
        
      
        System.out.println("Entry Point is " + startAddr);
        showFullCode();
        
        int addr;
        Byte[] arg1Byte, arg2Byte;
        int arg1, arg2;
        int operRes;
        
        boolean haltFlag = false;

        MemoryProgram memProg =  new MemoryProgram(memoryManager, startAddr);
        
        MemoryStack memStack = this.memoryManager.getMemStack();
        MemoryHeap memHeap = this.memoryManager.getMemHeap();
        //Continue in jmp
        
        memProg.jump(startAddr);
        
        Byte[] value;
        
        while (!haltFlag) {
                VMCommands command = memProg.getCommand();
                addr = memProg.getCommandAddrArg();
                 System.out.println(String.format("Command %s at line %s Addr: %s", command.toString(),memProg.getAddr(), addr ));
                
                switch (command) {
                    case Push_Addr:
                        value = memoryManager.getPtrByteValue(addr);
                        memStack.push(value);
                       System.err.println("Stack push: " + binConvertorService.bytesToInt(value, 0));
                        break;
                    case Push_Addr_NO_UNBOX:
                       // value = memoryManager.getPtrByteValue(addr);
                        memStack.push(binConvertorService.integerToByte(addr));
                       //System.err.println("Stack push: " + binConvertorService.bytesToInt(value, 0));
                        break;    
                    case Pop:
                        memStack.pop();
                        break;
                    case Add:
                        arg1 = stackPopInt();
                        arg2 = stackPopInt();
                        operRes = arg1 + arg2;
                        memStack.push(binConvertorService.integerToByte(operRes));
                        break;
                    case Mul:
                        arg1 = stackPopInt();
                        arg2 = stackPopInt();
                        operRes = arg1 * arg2;
                        memStack.push(binConvertorService.integerToByte(operRes));
                        break;
                    case Var_Put:        
                        memStack.pop(addr);
                        break;
                    case Var_Load:
                        value = memoryManager.getPtrValue(addr);
                        memStack.push(value);
                        break;
                    case Jmp:
                        if(addr == 0){
                            addr = stackPopInt();
                            System.out.println("Jump addr from stack");
                        }
                        System.out.println("Jump to addr:" + addr);
                        memProg.jump(addr);
                        continue;
                        
                    case Invoke_Sys_Function:
                        callSysFunc(addr);
                        break;
                    case Halt:
                        haltFlag = true;
                        //System.out.println("Program finished with Halt Command"); 
                        break;
                    //TODO: Possibly this command should be replaced to call syss func?    
                    case Var_Declare_Local:
                        int varSize = stackPopInt();
                        memStack.push(new Byte[varSize]);
                        int locVarAddr = memoryManager.getSysRegister(VmSysRegister.StackHeadPos);
                        int varInd = memHeap.getIntPtrValue(addr);
                        
                        System.out.println(String.format( "Local var declared: %s with size %s at addr #%s ", varInd ,varSize, locVarAddr));
                        int frameStart = memoryManager.getSysRegister(VmSysRegister.FrameStackPos)  ;
                        memStack.putValue(frameStart + varInd * INT_SIZE, locVarAddr);
                        //int locVarPtrmemHeap.memAlloc(varSize);
                        break;
                    case Var_Put_Local:
                        /*Local var table is allocated at the begin of stack address space
                        Table Format: int|int|...
                        This table is table of pointers
                        */
                        varInd = addr;
                        frameStart = memoryManager.getSysRegister(VmSysRegister.FrameStackPos)  ;
                        int varAddr = memStack.getIntValue(frameStart + varInd * INT_SIZE);
                         
                        memStack.pop(varAddr);
                        
                        break;
                    case Mov:
                        int regInd = addr;
                        int val = memoryManager.getSysRegister(VmSysRegister.values()[stackPopInt()]);
                        memoryManager.setSysRegister(VmSysRegister.values()[regInd], val);
                        break;
                    case NOP:
                        break;
                    case Dup:
                        value = memStack.pop();
                        memStack.push(value);
                        memStack.push(value);
                        break;
                    default:
                        System.err.println("Unprocessed command: " + command.toString());
               
                }
                memProg.next();
            }
          varValuesDebug();
        /*} catch(Exception e){
            System.err.println("VM execution Error: " + e.getMessage());
        }*/
    }
    
    protected void varValuesDebug(){
     int N =  program.readHeader(VmExeHeader.VarTableSize);
     for(int i = 0; i < N; i++){
          int varAddrPtr =  addrTables.getAddrByIndex(VmExeHeader.VarTableSize, i);
         int varAddr = getMemHeap().getIntValue(varAddrPtr) ;
         System.out.println("Value of " + i +" var is " +getMemHeap().getIntValue(varAddrPtr + VM.INT_SIZE) );
     }
        System.out.println("Stack pos " + this.memoryManager.getSysRegister(VmSysRegister.StackHeadPos));
    }
    
  
    protected void showFullCode(){
        int startInstrsInd = program.readHeader(VmExeHeader.InstructionsStart);
        int progStartPoint = program.readHeader(VmExeHeader.ProgramStartPoint);
        int startAddr =  memoryManager.getSysRegister(VmSysRegister.ProgOffsetAddr);
     
        MemoryProgram memProg =  new MemoryProgram(memoryManager, startAddr);
        
        MemoryStack memStack = this.memoryManager.getMemStack();
        MemoryHeap memHeap = this.memoryManager.getMemHeap();
        //Continue in jmp
        
        memProg.jump(startAddr);
       Boolean haltFlag = false;  
       while (!haltFlag) {
             VMCommands command = memProg.getCommand();
             
             String argVal ="";
             Integer addr = memProg.getCommandAddrArg();
             
             switch(command){
               
                 case Halt:
                     break;
                 default:
                     argVal = String.format("%s(%s)", addr,  Integer.toString(memoryManager.getIntPtrValue(addr))) ;
                     
             }
             
             if(command == VMCommands.Halt) haltFlag = true;
            
             System.out.println(String.format("%s| %s| %s", memProg.getAddr(), memProg.getCommand().toString(), argVal));
             memProg.next();
        }
        
    }
    
  
}
