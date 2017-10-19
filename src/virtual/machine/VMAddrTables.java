/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine;

import java.util.HashMap;
import virtual.machine.memory.MemoryHeap;
import virtual.machine.memory.VmSysRegister;

/**
 *
 * @author Andrey
 */
public class VMAddrTables {
    protected HashMap<VmExeHeader, Integer> tablesOffset;
    protected Program program;
    protected MemoryHeap memHeap;
    protected int curOffset;
    
    public VMAddrTables(Program program, MemoryHeap memHeap){
        this.program = program;
        this.memHeap = memHeap;
        curOffset = memHeap.getSegmentOffset();
        tablesOffset = new HashMap<>();
    }
        
    public int getAddrByIndex(VmExeHeader tableType, int varInd){ 
        varInd++;
        int tableOffset = tablesOffset.get(tableType);
        //Everywhere is plus 1, because first index is length of pointer
        int varAdrPtr = memHeap.getIntValue(varInd  * VM.ADDR_SIZE + tableOffset);
        //Also we need deferencing of pointers
        //int varAdrPtr = memHeap.getIntValue(varAdrPtrPtr);
        return varAdrPtr;
    }
    
    public void setAddrForIndex(VmExeHeader tableType, int varInd, int addr){
        varInd++;
      int tableOffset = tablesOffset.get(tableType);  
      int cellAddr = varInd  * VM.ADDR_SIZE + tableOffset;
      //  System.err.println("Set index " +cellAddr + " " + varInd );
      memHeap.putValue(cellAddr, addr);
    }
    
    public void add(VmExeHeader tableType) throws VMOutOfMemoryException{
         tablesOffset.put(tableType, curOffset);
         int size = program.readHeader(tableType);
         memHeap.memAlloc((size + 1) * VM.INT_SIZE);
         curOffset += (size + 1)* VM.INT_SIZE;
    }
    
}
