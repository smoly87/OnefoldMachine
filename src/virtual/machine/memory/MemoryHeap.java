/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine.memory;

import java.util.ArrayList;
import java.util.Collections;
import virtual.machine.DataBinConvertor;
import virtual.machine.VMOutOfMemoryException;
import virtual.machine.VM;
import virtual.machine.VmExecutionExeption;


/**
 *
 * @author Andrey
 */
public class MemoryHeap extends Memory{
    

    protected int size;
    protected int maxSize;
    protected int  programDataOffset;
    
    public MemoryHeap(int maxSize, int segmentOffset, ArrayList<Byte> data, DataBinConvertor binConvertorService) throws VmExecutionExeption{
        super(segmentOffset, data, binConvertorService);
        setSysRegister(VmSysRegister.LastHeapPos, segmentOffset);
        this.maxSize = maxSize;
    }
    public int  memAllocPtr(int size) throws VmExecutionExeption{
        return memAlloc(size + PTR_HEADERS_SIZE);
    }
    
    public int  memAlloc(int size) throws  VmExecutionExeption{
        if( getFreeMemSize() < size){
            garbageCollect();
        }
        if(getFreeMemSize() < size) throw new VMOutOfMemoryException();
        
        
        
        int freeAddr = getSysRegister(VmSysRegister.LastHeapPos);
        int delta = (freeAddr - this.segmentOffset + size + VM.INT_SIZE) - this.size;
        if(delta > 0){
            this.increaseDataSize(delta);
            this.size += delta;
        }
        putValue(freeAddr, size);
        //Pointer - size of block
        //Length of field with size + size of block +satart address
        int newFreeAddr = freeAddr +  size + VM.INT_SIZE;
        
        setSysRegister(VmSysRegister.LastHeapPos, newFreeAddr);
        
        return freeAddr;
    }
   
    @Override
    public void  putValue(int addr, Byte[] byteVal) throws VmExecutionExeption{
        int sizeAfterAlloc = (addr - this.segmentOffset) + byteVal.length;
        if(sizeAfterAlloc > this.size){
            throw new VmExecutionExeption("Try to put value on heap, but data not allocated by memAlloc.");
            //Here increase to deltas
            /*int delta = sizeAfterAlloc - this.size ;
            this.data.addAll( new ArrayList<>(delta ) );
            this.size += delta;     */
        }
        super.putValue(addr, byteVal);
    }    
        
   
    public int dataSize(){
        return this.data.size();
    }
    
    public int getFreeMemSize(){
        int freeAddr = getSysRegister(VmSysRegister.LastHeapPos);
        // -HEAP_OFFSET to calculate lenght
        return this.maxSize  -  (freeAddr - segmentOffset);
    }
    
    protected int findNextBlock(int from){
        //For null object/variable first byte should be null
        //TODO: When we clear variable we should put null anyware.
        //But need some details
        int endAddr = segmentOffset + this.size;
        for(int pos = from; pos < endAddr; pos++){
             if(data.get(pos) != null) return pos;
        }
           
        return -1;   
    }
    
    public void garbageCollect(){
        int pos = getSysRegister(VmSysRegister.ProgDataMemHeapOffset);
        int prevBlockStart = findNextBlock(pos);
        if(prevBlockStart < 0) return;
        
        int prevBlockEnd = pos = getBlockEndAddr(prevBlockStart);
        
        int endAddr = this.segmentOffset + this.size;
        while(pos < endAddr){
            int curBlockStart = findNextBlock(pos);
            
            //TODO: Check if not null pointer
            //Rollback to prev
            Byte[] flagValue = getValue(curBlockStart, 1);
            if(flagValue[0] == 1){
            }
            //No bloks anymore
            if(curBlockStart == -1) {
                return;
            } 
            int curBlockSize = getIntValue(curBlockStart);
            //Think about +/- 1 position
            if(curBlockStart != prevBlockEnd){
                pos = getBlockEndAddr(curBlockStart);
                System.arraycopy(data, curBlockStart, data, prevBlockEnd, curBlockSize);    
                prevBlockEnd = prevBlockEnd + curBlockSize  + VM.INT_SIZE;;
            } else{
                prevBlockEnd =  getBlockEndAddr(curBlockStart);
            }      
        }
        
        
        
    }
    
}
