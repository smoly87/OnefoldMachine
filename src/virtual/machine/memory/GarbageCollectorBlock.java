/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine.memory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author Andrey
 */
public class GarbageCollectorBlock {
    protected LinkedList<GCPtrInfo> ptrAddressesLst;
    protected Boolean isGap;
    protected Integer size;
    protected Integer blockStart;

    public Integer getBlockStart() {
        return blockStart;
    }
    
    public Integer getBlockEnd() {
        return blockStart + size;
    }
    
    public void setBlockStart(Integer blockStart) {
        this.blockStart = blockStart;
    }

    public Boolean getIsGap() {
        return isGap;
    }

    public void setIsGap(Boolean isGap) {
        this.isGap = isGap;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    
    public LinkedList<GCPtrInfo> getPtrAddressesLst() {
        return ptrAddressesLst;
    }
    
    public GarbageCollectorBlock() {
        ptrAddressesLst = new LinkedList<>();
    }

    
    public void addPtr(Integer ptrAddr){
        GCPtrInfo ptrInfo = new GCPtrInfo(ptrAddr);
        ptrAddressesLst.add(ptrInfo);
        
    }
    
    public void shiftAddresses(int delta){
        for(GCPtrInfo ptrInfo : ptrAddressesLst){
            ptrInfo.shiftAddress(delta);
        }
        
    }
    
    public GarbageCollectorBlock merge(GarbageCollectorBlock block){
       this.ptrAddressesLst.addAll(block.getPtrAddressesLst());
       this.setSize(this.getSize() + block.getSize());
       return this;
    }
}
