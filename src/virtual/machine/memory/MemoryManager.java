/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine.memory;

import java.util.ArrayList;
import java.util.Collections;
import virtual.machine.DataBinConvertor;
import virtual.machine.VM;
import virtual.machine.VmExecutionExeption;

/**
 *
 * @author Andrey
 */
public class MemoryManager extends Memory{
    public static final int SYS_SIZE = 1000;
    public static final int STACK_SIZE = 1000;
    public static final int HEAP_SIZE = 1000;
    
    public static final int STACK_OFFSET = SYS_SIZE; 
    protected static final int PROG_OFFSET = STACK_OFFSET + STACK_SIZE;
   // public static final int PROG_OFFSET = HEAP_OFFSET + HEAP_SIZE;
    protected int heapSegmentOffset;
    public static final int MEM_SIZE = 10000;
    
    protected MemoryHeap memHeap;
    protected MemoryStack memStack;
    protected MemoryProgram memProg;
    protected Memory memory;
    
    public MemoryProgram getMemProg() {
        return memProg;
    }
  
    
    public MemoryHeap getMemHeap() {
        return memHeap;
    }

    public MemoryStack getMemStack() {
        return memStack;
    }
    
    public MemoryManager( DataBinConvertor binConvertorService) throws VmExecutionExeption{
        super(0, binConvertorService);
        int initSize = SYS_SIZE + STACK_SIZE;
        this.data = new ArrayList<>(initSize);
        
        this.increaseDataSize(initSize);
        
       
        
        memStack = new MemoryStack(STACK_SIZE, STACK_OFFSET, this.data , binConvertorService);
        
    }
    
   
    
    
    public void allocateProgramInstructions(ArrayList<Byte> programData, int startInstrInd) throws VmExecutionExeption{
        
        int progInstrSize = programData.size() - startInstrInd;
        increaseDataSize(progInstrSize); 
        
        this.putValue(PROG_OFFSET, programData, startInstrInd, programData.size() );
        heapSegmentOffset = PROG_OFFSET + progInstrSize;
        memHeap = new MemoryHeap(HEAP_SIZE, heapSegmentOffset, data, binConvertorService);
        
        setSysRegister(VmSysRegister.ProgOffsetAddr, PROG_OFFSET);
        setSysRegister(VmSysRegister.ProgEndAddr, heapSegmentOffset);
        
       
       
    }
    
    @Override
    public int getSysRegister(VmSysRegister register){
        return super.getSysRegister(register);
    } 
    @Override
    public void setSysRegister(VmSysRegister register, int value) throws VmExecutionExeption{
       super.setSysRegister(register, value);
    }
    
   
}
