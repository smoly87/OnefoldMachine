/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
    
    public Map<Integer, Integer> transformResult(GarbageCollectorBlock gcFinalBlock){
        Stream<GCPtrInfo> ptrStream = gcFinalBlock.getPtrAddressesLst().stream();
        Map<Integer, Integer> ptrsMap = ptrStream.collect(Collectors.toMap(GCPtrInfo::getAddress, GCPtrInfo::getShiftedAddress));
        return ptrsMap;
    }
    
    public void reallocateAddresses(GarbageCollectorBlock gcFinalBlock) throws VmExecutionExeption{
        Map<Integer, Integer> ptrsMap = transformResult(gcFinalBlock);
        
        int memHeapStart = this.getSysRegister(VmSysRegister.ProgDataMemHeapOffset);
        int memHeapLast = this.getSysRegister(VmSysRegister.LastHeapPos);
        
        int varAddr = memHeapStart;
        
        while (varAddr < memHeapLast) {
            Byte[] objFlag = memHeap.getValue(varAddr, 1);
            int curBlockSize = getPtrSizeWithHeaders(varAddr);
            if(objFlag[0] == Memory.GC_FLAG_PTR){
                Integer ptrValue =  memHeap.getIntPtrValue(varAddr);
                if(ptrsMap.containsKey(ptrValue)){
                    memHeap.putPtrIntField(varAddr, ptrsMap.get(ptrValue), 0);
                }
            }
            
            varAddr = varAddr + curBlockSize;

        }
    }
    
   
}
