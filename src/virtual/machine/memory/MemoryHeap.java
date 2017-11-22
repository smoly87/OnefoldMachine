/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine.memory;

import common.VarType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import utils.Pair;
import virtual.machine.DataBinConvertor;
import virtual.machine.MemoryVariable;
import virtual.machine.MemoryVariables;
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
        return this.size;
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
    
    protected MemoryVariables getMemVarsIterator(){
        int blockPos = getSysRegister(VmSysRegister.ProgDataMemHeapOffset) ;
        int endAddr = this.segmentOffset + this.size;
        MemoryVariables memVars = new MemoryVariables(this, blockPos, endAddr);
        return memVars;
    }
    
    protected  HashMap<Integer, Boolean> createObjReacheablityMap(){
        MemoryVariables memVars = getMemVarsIterator();
        HashMap<Integer, Boolean> objReachabilityMap = new HashMap<>();
        while(memVars.hasNext()){
            MemoryVariable memoryVariable = memVars.next();
            if(memoryVariable.getVarType() == VarType.Object){
                objReachabilityMap.put(memoryVariable.getAddr(), false);
            }
        }
        
        return objReachabilityMap;
    }
    
    public void countReachable() throws VmExecutionExeption{
  
      HashMap<Integer, Boolean> objReachabilityMap = createObjReacheablityMap();
      MemoryVariables memVars = getMemVarsIterator();
      while(memVars.hasNext()){
          MemoryVariable memoryVariable = memVars.next();
          if(memoryVariable.getVarType() != VarType.Pointer) continue;
          
          int objAddr = this.getIntPtrValue(memoryVariable.getAddr());
          if(objReachabilityMap.containsKey(objAddr)){
              objReachabilityMap.put(objAddr, true);
          }  
      }  

      for(Map.Entry<Integer, Boolean> entry: objReachabilityMap.entrySet()){
          int value =entry.getValue() ? 1: 0;
          this.putPtrIntField(entry.getKey(), value, VM.INT_SIZE);   
          
      }
        //System.err.println(k);
    }
  
    
    public Pair<Integer, GarbageCollectorBlock> garbageCollect() throws VmExecutionExeption{
        int blockPos = getSysRegister(VmSysRegister.ProgDataMemHeapOffset) ;
        int endAddr = this.segmentOffset + this.size;
        int clearedSize = 0; 
        LinkedList<GarbageCollectorBlock> blocksList = new LinkedList<>();
        countReachable();
       // //int curBlockSize = getPtrSizeWithHeaders(blockPos);
        GarbageCollectorBlock gcBlock = new GarbageCollectorBlock();
        gcBlock.setBlockStart(blockPos);
        
        int gcBlockSize = 0;
        GarbageCollectorBlock prevBlock = null;
        
       
        //Composite pointers in blocks & find gaps
        int varsCnt = 0;
        while(blockPos < endAddr){
             varsCnt++;
             int curBlockSize = getPtrSizeWithHeaders(blockPos);
            if(isNullLinks(blockPos)){
                
                
                        
                if(prevBlock != null && prevBlock.getIsGap()){
                    prevBlock.setSize(prevBlock.getSize() + curBlockSize);
                } else{
                    gcBlock.setSize(gcBlockSize);
                    blocksList.add(gcBlock);
                    gcBlockSize = 0;
                    
                    GarbageCollectorBlock gcGap = new GarbageCollectorBlock();
                    gcGap.setIsGap(true);
                    gcGap.setSize(curBlockSize);
                    blocksList.add(gcGap);
                    prevBlock = gcGap;
                }
     
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
        
        
        System.err.println("Vars:" + varsCnt);
        
        if(blocksList.getLast().getIsGap()){
            blocksList.removeLast();
        }
        
        
        boolean flag = true;
        Iterator<GarbageCollectorBlock> iter = blocksList.iterator();
        
        int blocksCnt = blocksList.size();
        int k = 0;
        
        GarbageCollectorBlock block1;
        boolean needNext = true;
        block1 = iter.next();
        
        while(k < blocksCnt){
             
            if(blocksCnt - k > 2){ 
                
                GarbageCollectorBlock gap = iter.next();
                int gapSize = gap.getSize();
                iter.remove();
                
                GarbageCollectorBlock block2 = iter.next();
                List<Byte> block2Data = new ArrayList<Byte>(data.subList( block2.getBlockStart(),  block2.getBlockEnd()));
                data.subList(block1.getBlockEnd(), block2.getBlockEnd()).clear();
                data.addAll(block1.getBlockEnd(), block2Data);

                block2.shiftAddresses(-gapSize);
                block1 = block1.merge(block2);

                iter.remove();
 
            } else{
                break;
            }
           
            k+=2;
        }
        
        //Always one block left in the end, either exception should be thrown
        GarbageCollectorBlock finalBlock;
        if(blocksList.size() == 1){
           finalBlock = blocksList.getFirst();
            
        } else{
            throw new VmExecutionExeption("Error in garbage collection logic.Try to clear:" + clearedSize);
        }
        
        
        
        int memHeapHead = getSysRegister(VmSysRegister.LastHeapPos);
        
        setSysRegister(VmSysRegister.LastHeapPos, memHeapHead - clearedSize);
        return new Pair<Integer, GarbageCollectorBlock>(clearedSize, finalBlock);
         
    }
       
    
}
