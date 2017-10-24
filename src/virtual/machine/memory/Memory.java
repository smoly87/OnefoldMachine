/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine.memory;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import virtual.machine.DataBinConvertor;
import virtual.machine.VMOutOfMemoryException;
import virtual.machine.VM;

/**
 *
 * @author Andrey
 */
public class Memory {
    protected ArrayList<Byte> data;

    public ArrayList<Byte> getData() {
        return data;
    }
    protected DataBinConvertor binConvertorService;
    protected int segmentOffset ;

    public int getSegmentOffset() {
        return segmentOffset;
    }
    
    
    //TODO:Maybe map to real memory through nmap API
    public Memory(int segmentOffset,  DataBinConvertor binConvertorService){
        this.segmentOffset = segmentOffset ;
        this.binConvertorService = binConvertorService;
       
    }
    protected void increaseDataSize(int size){
        ArrayList<Byte> newData = new ArrayList<Byte>(Collections.nCopies(size, (byte)0))  ;

        data.addAll(newData);
    }
    
    public Memory(int segmentOffset, ArrayList<Byte> data,  DataBinConvertor binConvertorService){
        this.segmentOffset = segmentOffset ;
        this.binConvertorService = binConvertorService;
        this.data = data;
    }
    
    public Byte getValue(int addr){
        return data.get(addr);
    }
    public Byte[] getValue(int addr, int length){
        Byte[] res = new Byte[length];
        int k = 0;
        for(int i = addr; i < addr + length; i++, k++){
            res[k] = data.get( i);
        }
        return res;
    }
    
    public Byte[] getPtrValue(int addr){
        int size = this.getIntValue(addr);
        return this.getValue(addr + VM.INT_SIZE, size);
    }
    
     public int  getIntPtrValue(int addr){
       // Addr check  
       return  binConvertorService.bytesToInt(data, addr + VM.INT_SIZE);
    }
    /**
     * This method uses to get a single cell as integer
     * In most cases it's a pointer to address
     * @param addr
     * @return 
     */
    public int  getIntValue(int addr){
       // Addr check  
       return  binConvertorService.bytesToInt(data, addr);
    }
    /**
     * This metod is used to recieve a value of pointer
     * Usually it used to get Byte representation for such types as
     * Integer, Float etc
     * @param addr
     * @return 
     */
    public Byte[] getPtrByteValue(int addr){
        //First 4 bytes is lenght of pointer
        int ptrSize = binConvertorService.bytesToInt(data, addr);
        //We move forward on 4 bytes. because first 4 bytes is lenght of pointer
        // See comment above in head of description
        return getValue(addr + VM.INT_SIZE, ptrSize);
    }
    
    protected Byte[] intToByte(int val){
        return binConvertorService.integerToByte(val);
    }
    
     public void putValue(int addr, byte val){
         data.set(addr, val);
     }
    
    public void putValue(int addr, int val){
       Byte[] byteVal =  intToByte(val);
       putValue(addr, byteVal);
    }
    
    public void  putValue(int addr, Byte[] byteVal, int start, int end){
         int k = 0;
        for(int i = start; i < end; i++, k++){
            data.set(addr + k, byteVal[i]);
        }
    }
    
    public void  putValue(int addr, Byte[] byteVal){
         putValue(addr, byteVal, 0, byteVal.length);
    }
    
    public void  putValue(int addr, ArrayList<Byte> byteVal){
         putValue(addr, byteVal, 0,  byteVal.size());
    }
    
    public void  putValue(int addr, ArrayList<Byte> byteVal, int start, int end){
        int k = 0; 
        for(int i = start; i < end; i++, k++){
            data.set(addr + k, byteVal.get(i));
        }
    }
    
    public void putPtrValue(int addr, Byte[] byteVal){
       binConvertorService.integerToByte(byteVal.length);
       
    }
    
    protected int getRegisterAddr(VmSysRegister register){
        int addr = register.ordinal() * VM.INT_SIZE;
        return addr;
    }    
    protected void setSysRegister(VmSysRegister register, int value){
       //System registers are allocated from 0 byte and size is integer - 4 byte 
       Byte[] byteVal =  intToByte(value);
       putValue(getRegisterAddr(register), byteVal,0,byteVal.length);
    }
    
    protected int getSysRegister(VmSysRegister register){
        return getIntValue(getRegisterAddr(register));
    }
    
    public Byte[] deferencePtr(int ptrAddr){
        int prtSize = this.getIntValue(ptrAddr);
        return this.getValue(ptrAddr + VM.INT_SIZE, prtSize);
    }
    
    public int deferenceIntPtr(int ptrAddr){
        return binConvertorService.bytesToInt(deferencePtr(ptrAddr), 0);
    }
    
    /**
     * 
     * @param addr
     * @param fieldNum Index starts from null
     */
    /*public getField(int addr, int fieldNum){
    }*/
   
    
   
    
    protected int getBlockEndAddr(int blockStartAddr){
        //INT_SIZE is size of dield with block size 
        return blockStartAddr + getIntValue(blockStartAddr) + VM.INT_SIZE;
    }
    
    
    protected void putData(Byte[] allocData, int startAddr){
         System.arraycopy(allocData, 0, data, startAddr, allocData.length);
    }
    
   
}
