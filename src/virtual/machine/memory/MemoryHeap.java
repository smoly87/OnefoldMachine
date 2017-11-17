/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import utils.Pair;
import virtual.machine.DataBinConvertor;
import virtual.machine.exception.VMOutOfMemoryException;
import virtual.machine.VM;
import virtual.machine.exception.VmExecutionExeption;


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
    
    
    protected void shiftPtrInVariables(){
    }
    
    public Pair<Integer, GarbageCollectorBlock> garbageCollect() throws VmExecutionExeption{
        int blockPos = getSysRegister(VmSysRegister.ProgDataMemHeapOffset) ;
        int endAddr = this.segmentOffset + this.size;
        int clearedSize = 0; 
        LinkedList<GarbageCollectorBlock> blocksList = new LinkedList<>();
        
        //int curBlockSize = getPtrSizeWithHeaders(blockPos);
        GarbageCollectorBlock gcBlock = new GarbageCollectorBlock();
        gcBlock.setBlockStart(blockPos);
        
        int gcBlockSize = 0;
        //Composite pointegers in blocks & find gaps
        while(blockPos < endAddr){
             int curBlockSize = getPtrSizeWithHeaders(blockPos);
            if(isNullLinks(blockPos)){
                
                gcBlock.setSize(gcBlockSize); 
                blocksList.add(gcBlock);
                gcBlockSize = 0;
                        
                GarbageCollectorBlock gcGap = new GarbageCollectorBlock();
                gcGap.setIsGap(true);
                gcGap.setSize(curBlockSize);
                blocksList.add(gcGap);
                
                gcBlock = new GarbageCollectorBlock(); 
                 blockPos += curBlockSize;
                gcBlock.setBlockStart(blockPos);
                
                clearedSize += curBlockSize;
                continue;
             } else { 
                gcBlock.addPtr(blockPos); 
                gcBlockSize += curBlockSize;
            }
            blockPos += curBlockSize;
        }
        
        if(gcBlock.getPtrAddressesLst().size() > 0){
             gcBlock.setSize(gcBlockSize);
             blocksList.add(gcBlock);
        }
        
        //
        boolean flag = true;
        Iterator<GarbageCollectorBlock> iter = blocksList.iterator();
        
        int blocksCnt = blocksList.size();
        int k = 0;
        while(k < blocksCnt){
            GarbageCollectorBlock block1 = iter.next();
            //TODO: figure out with this condition
            //Addition of shift!!!
            //Recount block start & end
            if(blocksCnt - k > 2){ 
                
                GarbageCollectorBlock gap = iter.next();
                int gapSize = gap.getSize();
                iter.remove();
                
                GarbageCollectorBlock block2 = iter.next();
                List<Byte> block2Data = new ArrayList<Byte>(data.subList( block2.getBlockStart(),  block2.getBlockEnd()));
                data.subList(block1.getBlockEnd(), block2.getBlockEnd()).clear();
                data.addAll(block1.getBlockEnd(), block2Data);

                //System.arraycopy(data, block2.getBlockStart(), data, block1.getBlockEnd(), block2.getSize()); 
                block2.shiftAddresses(-gapSize);
                block1 = block1.merge(block2);
                
                
                          
                blocksCnt -= 2;
                iter.remove();
                //TODO: change block cnt
            }
           
            k+=3;
        }
        
        //Always one block left in the end, either exception should be thrown
        GarbageCollectorBlock finalBlock;
        if(blocksList.size() == 1){
           finalBlock = blocksList.getFirst();
            
        } else{
            throw new VmExecutionExeption("Error in garbage collection logic");
        }
        
        
        
        int memHeapHead = getSysRegister(VmSysRegister.LastHeapPos);
        
        setSysRegister(VmSysRegister.LastHeapPos, memHeapHead - clearedSize);
        return new Pair<Integer, GarbageCollectorBlock>(clearedSize, finalBlock);
         
    }
    
    
    
    public int garbageCollectLegacy() throws VmExecutionExeption{
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
