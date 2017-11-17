/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine.memory;

import java.util.ArrayList;
import main.ByteUtils;
import virtual.machine.DataBinConvertor;
import virtual.machine.exception.VMStackOverflowException;
import virtual.machine.VM;
import virtual.machine.exception.VmExecutionExeption;
import virtual.machine.exception.VmStackEmptyPopException;

/**
 *
 * @author Andrey
 */
public class MemoryStack extends Memory{
    
    protected static int firstElementOffset = 2;
    protected int stackSize;
    
    public MemoryStack(int stackSize, int segmentOffset, DataBinConvertor binConvertorService) throws VmExecutionExeption{
        super(segmentOffset, binConvertorService);
        this.setSysRegister(VmSysRegister.StackHeadPos, segmentOffset );
        this.stackSize = stackSize;
    }
    public MemoryStack(int stackSize, int segmentOffset, ArrayList<Byte> data,  DataBinConvertor binConvertorService) throws VmExecutionExeption{
        super(segmentOffset, data, binConvertorService);
        this.setSysRegister(VmSysRegister.StackHeadPos, segmentOffset );
        this.stackSize = stackSize;
    }
    
    
    public Byte[] pop() throws VmStackEmptyPopException,  VmExecutionExeption{
        return pop(0);
    }
    
    public Byte[] pop(int addr) throws VmStackEmptyPopException, VmExecutionExeption{
        return pop(addr, 0);
    }
    
    public Byte[] pop(int addr, int offset) throws VmStackEmptyPopException, VmExecutionExeption{
        //Control if no elements
        int headAddr = this.getSysRegister(VmSysRegister.StackHeadPos);
        if(headAddr == 0) throw new VmStackEmptyPopException();
        
        int fullSize = this.getIntValue(headAddr + GC_FLAG_SIZE)+ PTR_HEADERS_SIZE;
        int prevElemAddr = this.getIntValue(headAddr + PTR_HEADERS_SIZE);
        this.setSysRegister(VmSysRegister.StackHeadPos, prevElemAddr);
                
        if(addr != 0){
            int startAddr = headAddr + (PTR_HEADERS_SIZE + VM.INT_SIZE);
            int endAddr = startAddr + fullSize - (PTR_HEADERS_SIZE + VM.INT_SIZE);
            putValue(addr + PTR_HEADERS_SIZE +offset , data, startAddr,  endAddr);
        }
        
        return getPtrByteValue(headAddr, VM.INT_SIZE);
    }
    
    
    
    public int push(Byte[] value) throws VMStackOverflowException, VmExecutionExeption{
       int headAddr = this.getSysRegister(VmSysRegister.StackHeadPos);
       //First 4 bytes is length of pointer
       // Next 4 bytes is previous element
       int freeSize = stackSize - (headAddr  - segmentOffset);
       if(freeSize < value.length) {
           throw new VMStackOverflowException(String.format("VM Stack overflow. FreeSize: %s. Need to allocate: %s", freeSize, value.length));
       }
       
       //No elemenents in stack
       int newHeadAddr;
       if(headAddr == segmentOffset){
           newHeadAddr = segmentOffset + firstElementOffset;
       } else {
           int prevElemSize = this.getIntValue(headAddr + GC_FLAG_SIZE) + PTR_HEADERS_SIZE; 
           newHeadAddr = headAddr + prevElemSize;
       }
 
       //Add Link to previous element      
       Byte[] byteHeadAddr = binConvertorService.integerToByte(headAddr);
       value = ByteUtils.concat(byteHeadAddr, value);
       
       //value = ByteUtils.concat(new Byte[]{0}, value);
       
       this.putPtrValue(newHeadAddr, value);
       
       this.setSysRegister(VmSysRegister.StackHeadPos, newHeadAddr);
       return newHeadAddr;
    }
    
    public int push(int value) throws VMStackOverflowException, VmExecutionExeption{
        return push(binConvertorService.toBin(value));
    }
}

