/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package virtual.machine;

import types.TypesInfo;
import common.VarType;
import virtual.machine.memory.Memory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import program.builder.BinaryReader;
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
    //... not realised yet
 

    
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
        int secStart = program.readHeader(VmSections.VarTableStart);
        int secEnd = program.readHeader(VmSections.ClassesMetaInfoStart);
        
        BinaryReader binReader = new BinaryReader(program.getData());
        binReader.setCurPos(secStart);
 
        while( binReader.getCurPos() < secEnd){
             int varInd = binReader.readIntAndNext();
             int varSize = binReader.readIntAndNext();        
             int ptrAddr = memHeap.memAlloc(varSize);
             addrTables.setAddrForIndex(VmSections.VarTableSize, varInd, ptrAddr);
        }
    }
    
    protected void allocateData() throws VMOutOfMemoryException{
        int secStart = program.readHeader(VmSections.ConstStart);
        int secEnd = program.readHeader(VmSections.VarTableStart);
        
        MemoryHeap memHeap = getMemHeap();
        
        ArrayList<Byte> progData = program.getData();
        
        BinaryReader binReader = new BinaryReader(program.getData());
        binReader.setCurPos(secStart);
 
        while( binReader.getCurPos() < secEnd){ 
            int varInd =  binReader.readIntAndNext();
            int varSize =  binReader.readIntAndNext();           

            int ptrAddr = memHeap.memAlloc(varSize);
            memHeap.putValue(ptrAddr+ INT_SIZE, progData, binReader.getCurPos() , binReader.getCurPos() +   varSize);
            addrTables.setAddrForIndex(VmSections.ConstTableSize, varInd, ptrAddr);
            
            binReader.nextBytes(varSize);  
        }
    }
    
    protected void allocateClassesMetaInfo() throws VMOutOfMemoryException{

        int secStart = program.readHeader(VmSections.ClassesMetaInfoStart);
        int secEnd = program.readHeader(VmSections.InstructionsStart);
        
        MemoryHeap memHeap = getMemHeap();
        
        BinaryReader binReader = new BinaryReader(program.getData());
        binReader.setCurPos(secStart);
 
        while( binReader.getCurPos() < secEnd){ 
            int classInd = binReader.readIntAndNext();
            int metaInfoSize = binReader.readIntAndNext();
            int metaTablePtr =  memHeap.memAlloc(metaInfoSize);
            System.out.println("MetaSize:" + metaInfoSize);
            addrTables.setAddrForIndex(VmSections.ClassesTableSize, classInd, metaTablePtr);
            
            binReader.nextBytes(metaInfoSize - VM.INT_SIZE);
            
        }
    }
    
    /**
     * It's possible not pretty, because we have postfix Size, however name without it
     * For example we have table of Const addresses, but tableType named constTableSize
     * Naturally later we should create more elegant solution
     * @param tableType 
     */
   
    protected void createDescrTables() throws VMOutOfMemoryException {
       this.addrTables.add(VmSections.ConstTableSize);
       this.addrTables.add(VmSections.VarTableSize);
       this.addrTables.add(VmSections.ClassesTableSize);
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
   
        for(int i = progStart; i < progEnd; i += 5){
            Byte commandCode = memHeap.getValue(i, 1)[0];
            VMCommands command = getMnemonic(commandCode);
            
            int addr = getMemHeap().getIntValue(i + 1);
            int varAdrPtr;
            switch(command){
                case Jmp:
                   
                    //Convert relative adress to absolute
                    int absAddr =  addr + progStart; 
                    memoryManager.putValue(i + 1, absAddr);
                    break;
                case Push:
                  
                    int constAdrPtr = addrTables.getAddrByIndex(VmSections.ConstTableSize, addr); 
                    System.err.println("Read addr dbg: " + addr+ " ,"+ constAdrPtr);
                    memHeap.putValue(i + 1, constAdrPtr);
                    memHeap.putValue(i, (byte)VMCommands.Push_Addr.ordinal());
   
                    
                    break;
                case Var_Load: 
                    varAdrPtr = addrTables.getAddrByIndex(VmSections.VarTableSize, addr); 
                    memHeap.putValue(i, (byte)VMCommands.Push_Addr.ordinal());
                    memHeap.putValue(i + 1, varAdrPtr);
                    
                    break;
                case Var_Put:
                    
                    varAdrPtr = addrTables.getAddrByIndex(VmSections.VarTableSize, addr); 
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
         int ptrSize = stackPopInt();
        /* new Byte[ptrSize]*/
        // int ptrStart = memStack.push();
         
         return 0;
    }
    
    
    protected void callSysFunc(int funcType) throws VmStackEmptyPop, VMStackOverflowException{
        VMSysFunction sysFunc = VMSysFunction.values()[funcType];
        switch(sysFunc){
            case MemAllocStack:
                sysMemAllocStack();
                break;
        }
    }
    
    public void run(Program program) throws Exception{
     // try{  
        this.program = program;
        
        
        int startInstrsInd = program.readHeader(VmSections.InstructionsStart);
        
        memoryManager.allocateProgramInstructions(program.getData(), startInstrsInd);
        int startAddr = memoryManager.getSysRegister(VmSysRegister.ProgOffsetAddr);
        
        this.addrTables = new VMAddrTables(program, getMemHeap());
        this.createDescrTables();
        this.allocateData();
        this.allocateVariables();
        this.allocateClassesMetaInfo();
        this.translateAdresses();
        
      
   
       
        
        int addr;
        Byte[] arg1Byte, arg2Byte;
        int arg1, arg2;
        int operRes;
        
        boolean haltFlag = false;

        MemoryProgram memProg =  new MemoryProgram(memoryManager, startAddr);
        
        MemoryStack memStack = this.memoryManager.getMemStack();
        
        //Continue in jmp
        
        memProg.jump(startAddr);
        
        
        
        while (!haltFlag) {
                VMCommands command = memProg.getCommand();
                addr = memProg.getCommandAddrArg();
                 System.err.println("Command "  + command.toString() + " Addr: " + addr);
                
                switch (command) {
                    case Push_Addr:
                        Byte[] value = memoryManager.getPtrByteValue(addr);
                        memStack.push(value);
                       System.err.println("Stack push: " + binConvertorService.bytesToInt(value, 0));
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
                         arg1 = memoryManager.getIntValue(addr);
                           System.err.println("Var_PUt: " + addr + " " + arg1 );
                        break;
                    case Var_Load:
                        arg1 = memoryManager.getValue(addr);
                        System.err.println("Var_Load: " + addr + " " + arg1);
                        memStack.push(binConvertorService.integerToByte(arg1));
                        break;
                    case Jmp:
                        if(addr == 0){
                            addr = stackPopInt();
                        }
                        memProg.jump(addr);
                        break;
                    case Invoke_Sys_Function:
                        callSysFunc(addr);
                        break;
                    case Halt:
                        haltFlag = true;
                        System.out.println("Program finished with Halt Command"); 
                        break;
                    default:
                        System.err.println("Unprocessed command: " + command.toString());
                    /*case "Pop":
                    int val = stack.pop();
                    addr = this.getIntValueMovePointer();
                    memory.putValue(addr, val);
                    break;
                case "Jmp":
                    this.pos = this.getIntValueMovePointer();
                    break;  
                case "InvokeFunc":
                    break;*/
                }
                memProg.next();
            }
          varValuesDebug();
        /*} catch(Exception e){
            System.err.println("VM execution Error: " + e.getMessage());
        }*/
    }
    
    protected void varValuesDebug(){
     int N =  program.readHeader(VmSections.VarTableSize);
     for(int i = 0; i < N; i++){
          int varAddrPtr =  addrTables.getAddrByIndex(VmSections.VarTableSize, i);
         int varAddr = getMemHeap().getIntValue(varAddrPtr) ;
         System.out.println("Value of " + i +" var is " +getMemHeap().getIntValue(varAddrPtr + VM.INT_SIZE) );
     }
        System.out.println("Stack pos " + this.memoryManager.getSysRegister(VmSysRegister.StackHeadPos));
    }
    
    
  
}
