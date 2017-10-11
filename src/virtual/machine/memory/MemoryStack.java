/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine.memory;

import java.util.ArrayList;
import virtual.machine.DataBinConvertor;
import virtual.machine.VMStackOverflowException;
import virtual.machine.VM;
import virtual.machine.VmStackEmptyPop;

/**
 *
 * @author Andrey
 */
public class MemoryStack extends Memory{
    
    protected static int firstElementOffset = 2;
    protected int stackSize;
    
    public MemoryStack(int stackSize, int segmentOffset, DataBinConvertor binConvertorService) {
        super(segmentOffset, binConvertorService);
        this.setSysRegister(VmSysRegister.StackHeadPos, segmentOffset );
        this.stackSize = stackSize;
    }
    public MemoryStack(int stackSize, int segmentOffset, ArrayList<Byte> data,  DataBinConvertor binConvertorService){
        super(segmentOffset, data, binConvertorService);
        this.setSysRegister(VmSysRegister.StackHeadPos, segmentOffset );
        this.stackSize = stackSize;
    }
    
    
    public Byte[] pop() throws VmStackEmptyPop{
        return pop(0);
    }
    
    public Byte[] pop(int addr) throws VmStackEmptyPop{
        //Control if no elements
        int headAddr = this.getSysRegister(VmSysRegister.StackHeadPos);
        if(headAddr == 0) throw new VmStackEmptyPop();
        
        int prevElemAddr = this.getIntValue(headAddr + VM.ADDR_SIZE);
        this.setSysRegister(VmSysRegister.StackHeadPos, prevElemAddr);
        
        int dataLen = getIntValue(headAddr);
        int ptrValueStart = headAddr + 2 * VM.INT_SIZE;
        
        int realLen = dataLen - 2 * VM.INT_SIZE;
        
        if(addr != 0){
            int endAddr = ptrValueStart + realLen;
            putValue(addr + VM.INT_SIZE, data, ptrValueStart,  endAddr);
        }
        
        return getValue(ptrValueStart , realLen);
    }
    
    public void push(Byte[] value) throws VMStackOverflowException{
       int headAddr = this.getSysRegister(VmSysRegister.StackHeadPos);
       //First 4 bytes is length of pointer
       // Next 4 bytes is previous element
       int freeSize = stackSize - (headAddr  - segmentOffset);
       if(freeSize < value.length) throw new VMStackOverflowException();
       
       //No elemenents in stack
       int newHeadAddr;
       if(headAddr == segmentOffset){
           newHeadAddr = segmentOffset + firstElementOffset;
       } else {
           int prevElemSize = this.getIntValue(headAddr); 
           newHeadAddr = headAddr + prevElemSize;
       }
       
       //Size of pointer
       this.putValue(newHeadAddr, value.length + (2 * VM.INT_SIZE) );
       //Link to previous element
       this.putValue(newHeadAddr + VM.INT_SIZE, headAddr);
       this.putValue(newHeadAddr + 2 * VM.INT_SIZE, value);
       
       this.setSysRegister(VmSysRegister.StackHeadPos, newHeadAddr);
       
    }
}
