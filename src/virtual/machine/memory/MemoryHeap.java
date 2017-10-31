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
        int freeAddr = memAlloc(size + PTR_HEADERS_SIZE);
        putValue(freeAddr, size);
        return freeAddr;
    }
    
    public int  memAlloc(int size) throws  VmExecutionExeption{
        if( getFreeMemSize() < size){
            garbageCollect();
        }
        if(getFreeMemSize() < size) throw new VMOutOfMemoryException();
        
        
        
        int freeAddr = getSysRegister(VmSysRegister.LastHeapPos);
        int delta = (freeAddr - this.segmentOffset + size ) - this.size;
        if(delta > 0){
            this.increaseDataSize(delta);
            this.size += delta;
        }
        
        //Pointer - size of block
        //Length of field with size + size of block +satart address
        int newFreeAddr = freeAddr +  size ;
        
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
    
    protected boolean isNullLinks(int blockStart){
        Byte[] flagValue = getValue(blockStart, 1);
        if (flagValue[0] == GC_FLAG_OBJ) {
            //TODO: If NO links to Object, it should been removed
            int lnkCount = getIntValue(blockStart + PTR_HEADERS_SIZE + VM.INT_SIZE);
            return lnkCount == 0;
        }
        return false;
    }
    
    public int garbageCollect() throws VmExecutionExeption{
        int blockPos = getSysRegister(VmSysRegister.ProgDataMemHeapOffset) ;
        int endAddr = this.segmentOffset + this.size;
        int clearedSize = 0; 
        
        while(blockPos < endAddr){
            int curBlockSize = getPtrSizeWithHeaders(blockPos);
            if(isNullLinks(blockPos)){
                 int newBlockPos = getBlockEndAddr(blockPos);
                 int newBlockSize = 0;
                 if(newBlockPos < endAddr){
                    newBlockSize = getPtrSizeWithHeaders(blockPos);
                    System.arraycopy(data, newBlockPos, data, blockPos, newBlockSize); 
                 } else{
                    clearedSize += curBlockSize;
                    break;
                 }
                 
                 clearedSize += curBlockSize;
                 blockPos = newBlockPos + newBlockSize;
             } else { 
                blockPos += curBlockSize;
            }
        }
        
        int memHeapHead = getSysRegister(VmSysRegister.LastHeapPos);
        
        setSysRegister(VmSysRegister.LastHeapPos, memHeapHead - clearedSize);
        return clearedSize;
         
    }
   
    
}
