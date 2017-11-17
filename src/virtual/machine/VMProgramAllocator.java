/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine;

import virtual.machine.exception.VMOutOfMemoryException;
import virtual.machine.exception.VmExecutionExeption;
import java.util.ArrayList;
import program.builder.BinaryReader;
import static virtual.machine.VM.COMMAND_SIZE;
import virtual.machine.memory.MemoryHeap;
import virtual.machine.memory.MemoryManager;
import static virtual.machine.memory.MemoryManager.HEAP_SIZE;
import virtual.machine.memory.VmSysRegister;

/**
 *
 * @author Andrey
 */
public class VMProgramAllocator {
    protected MemoryManager memoryManager;
    protected VMAddrTables addrTables;

    public VMAddrTables getAddrTables() {
        return addrTables;
    }
    protected Program program;
    
    public VMProgramAllocator(MemoryManager memoryManager, Program program){
        this.memoryManager = memoryManager;
        this.program = program;
    }
   
    public void allocateProgram() throws VmExecutionExeption{
        this.addrTables = new VMAddrTables(program, memoryManager.getMemHeap());
        this.createDescrTables();
        this.allocateData();
        this.allocateClassesMetaInfo();
        /*Start of program data segment on heap, where user objects/pointers stored and where
         garbage collection is carried out*/
        int heapSize =  memoryManager.getSysRegister(VmSysRegister.LastHeapPos);
        memoryManager.setSysRegister(VmSysRegister.ProgDataMemHeapOffset, heapSize);
        this.allocateVariables();
        
        this.translateAdresses();
    }
    protected void allocateVariables() throws VMOutOfMemoryException, VmExecutionExeption{
        MemoryHeap memHeap = memoryManager.getMemHeap();
        int secStart = program.readHeader(VmExeHeader.VarTableStart);
        int secEnd = program.readHeader(VmExeHeader.ClassesMetaInfoStart);
        
        BinaryReader binReader = new BinaryReader(program.getData());
        binReader.setCurPos(secStart);
 
        while( binReader.getCurPos() < secEnd){
             int varInd = binReader.readIntAndNext();
             int varSize = binReader.readIntAndNext(); 
             Byte isObjPtrFlag = binReader.readAndNextBytes(1)[0];
             int ptrAddr = memHeap.memAllocPtr(varSize);
             memHeap.putPtrValue(ptrAddr, varSize);
             memHeap.putValue(ptrAddr, isObjPtrFlag);
             addrTables.setAddrForIndex(VmExeHeader.VarTableSize, varInd, ptrAddr);
        }
    }
    
    protected void allocateData() throws VMOutOfMemoryException, VmExecutionExeption{
        int secStart = program.readHeader(VmExeHeader.ConstStart);
        int secEnd = program.readHeader(VmExeHeader.VarTableStart);
        
        MemoryHeap memHeap = memoryManager.getMemHeap();
        
        ArrayList<Byte> progData = program.getData();
        
        BinaryReader binReader = new BinaryReader(program.getData());
        binReader.setCurPos(secStart);
 
        while( binReader.getCurPos() < secEnd){ 
            int varInd =  binReader.readIntAndNext();
            int varSize =  binReader.readIntAndNext();           

            int ptrAddr = memHeap.memAllocPtr(varSize);
            
            memHeap.putPtrValue(ptrAddr, progData, binReader.getCurPos() , binReader.getCurPos() +   varSize);
            addrTables.setAddrForIndex(VmExeHeader.ConstTableSize, varInd, ptrAddr);
            
            //System.out.println(String.format("VarInd: %s with value %s", varInd, memHeap.getIntValue(ptrAddr+ INT_SIZE)));
            binReader.nextBytes(varSize);  
        }
    }
    
    protected void allocateClassesMetaInfo() throws VMOutOfMemoryException, VmExecutionExeption{

        int secStart = program.readHeader(VmExeHeader.ClassesMetaInfoStart);
        int secEnd = program.readHeader(VmExeHeader.CommentsStart);
        
        MemoryHeap memHeap = memoryManager.getMemHeap();
        
        BinaryReader binReader = new BinaryReader(program.getData());
        binReader.setCurPos(secStart);
 
        while( binReader.getCurPos() < secEnd ){ 
            int classInd = binReader.readIntAndNext();
            int metaInfoSize = binReader.readInt();
            int metaTablePtr =  memHeap.memAlloc(metaInfoSize);
            System.out.println(String.format("Class_ID: %s MetaSize: %s", classInd, metaInfoSize)  );
            addrTables.setAddrForIndex(VmExeHeader.ClassesTableSize, classInd, metaTablePtr);
            
            binReader.prevBytes(VM.INT_SIZE);
            Byte[] metaData =  binReader.readAndNextBytes(metaInfoSize);
            memHeap.putValue(metaTablePtr, metaData);
            //printClassMetaInfo(classInd);
        }
        
    }
    
    protected VMCommands getMnemonic(Byte code){
       return VMCommands.values()[code];
    }
    
    protected void translateAdresses() throws VmExecutionExeption{
        int progStart = memoryManager.getSysRegister(VmSysRegister.ProgOffsetAddr);
        int progEnd = memoryManager.getSysRegister(VmSysRegister.ProgEndAddr);
        
        MemoryHeap memHeap = memoryManager.getMemHeap();        
   
        for(int i = progStart; i < progEnd; i += COMMAND_SIZE){
            Byte commandCode = memHeap.getValue(i, 1)[0];
            VMCommands command = getMnemonic(commandCode);
            int constAdrPtr =0;
            int addr = memoryManager.getMemHeap().getIntValue(i + 1);
            int varAdrPtr;
            switch(command){
                case Push:
                  
                    constAdrPtr = addrTables.getAddrByIndex(VmExeHeader.ConstTableSize, addr); 
                    
                    memHeap.putValue(i + 1, constAdrPtr);
                    memHeap.putValue(i, (byte)VMCommands.Push_Addr_Value.ordinal());
   
                    
                    break;
                case Push_Addr:case Mov:
                    constAdrPtr = addrTables.getAddrByIndex(VmExeHeader.ConstTableSize, addr); 
                    
                    memHeap.putValue(i + 1, constAdrPtr);
                    break;
                case Invoke_Sys_Function: case Var_Declare_Local: case Var_Declare_Local_Def_value: //case Var_Load_Local: case Var_Put_Local:
                    constAdrPtr = addrTables.getAddrByIndex(VmExeHeader.ConstTableSize, addr); 
                    memHeap.putValue(i + 1, constAdrPtr);
                     
                    break;
                case Var_Load: 
                    varAdrPtr = addrTables.getAddrByIndex(VmExeHeader.VarTableSize, addr); 
                    memHeap.putValue(i, (byte)VMCommands.Push_Addr_Value.ordinal());
                    memHeap.putValue(i + 1, varAdrPtr);
                    
                    break;
                /*case Var_Load_Local:
                    varAdrPtr = addrTables.getAddrByIndex(VmExeHeader.VarTableSize, addr); 
                     memHeap.putValue(i + 1, varAdrPtr);
                    break;*/
                case Var_Put:
                    
                    varAdrPtr = addrTables.getAddrByIndex(VmExeHeader.VarTableSize, addr); 
                    memHeap.putValue(i + 1, varAdrPtr);
                    // Change Push to Push ptr of value.
                   
                    break;
            }
        }
    }
    
     /**
     * It's possible not pretty, because we have postfix Size, however name without it
     * For example we have table of Const addresses, but tableType named constTableSize
     * Naturally later we should create more elegant solution
     * @param tableType 
     */
   
    protected void createDescrTables() throws VMOutOfMemoryException, VmExecutionExeption {
       this.addrTables.add(VmExeHeader.ConstTableSize);
       this.addrTables.add(VmExeHeader.VarTableSize);
       this.addrTables.add(VmExeHeader.ClassesTableSize);
    }
}
