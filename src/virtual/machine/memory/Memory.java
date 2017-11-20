/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine.memory;

import common.VarType;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import types.TypesInfo;
import virtual.machine.DataBinConvertor;
import virtual.machine.exception.VMOutOfMemoryException;
import virtual.machine.VM;
import virtual.machine.exception.VmExecutionExeption;

/**
 *
 * @author Andrey
 */
public class Memory {
    protected ArrayList<Byte> data;
    public static final int GC_FLAG_SIZE = 1;
    public static final byte GC_FLAG_OBJ = 2;
    public static final byte GC_FLAG_PTR = 3;
    
    public static final int PTR_HEADERS_SIZE = GC_FLAG_SIZE + VM.INT_SIZE  ;

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
       return  binConvertorService.bytesToInt(data, addr + PTR_HEADERS_SIZE);
    }
    
    public float  getFloatPtrValue(int addr){
       // Addr check  
       //Byte[] value = foo.toArray(new Integer[foo.size()]);
        final int floatSize = TypesInfo.getInstance().getTypeSize(VarType.Float);
        List<Byte> subList = data.subList(addr + PTR_HEADERS_SIZE, addr + PTR_HEADERS_SIZE+ floatSize);
        Byte[] byteValue = subList.toArray(new Byte[floatSize]);
       return  binConvertorService.bytesToFloat(byteValue);
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
    public Byte[] getPtrByteValue(int addr, int offset){
        
        int ptrSize = getPtrSize(addr);
        //We move forward on 4 bytes. because first 4 bytes is lenght of pointer
        // See comment above in head of description
        return getValue(addr + PTR_HEADERS_SIZE + offset, ptrSize - offset);
    }
    
    public int  getPtrSize(int addr){
       //First 4 bytes is lenght of pointer
       return binConvertorService.bytesToInt(data, addr + GC_FLAG_SIZE); 
    }
    
    public int  getPtrSizeWithHeaders(int addr){
        return getPtrSize(addr) + PTR_HEADERS_SIZE;
    }
    
    public Byte[] getPtrField(int addr, int fieldOffset, int fieldSize){
        return getValue(addr + PTR_HEADERS_SIZE + fieldOffset, fieldSize);
    }
    
    public int getPtrIntField(int addr, int fieldOffset){
        Byte[] val =  getValue(addr + PTR_HEADERS_SIZE + fieldOffset, VM.INT_SIZE);
        return binConvertorService.getIntegerValue(val);
    }
    
    public Byte[] getPtrByteValue(int addr){
        return getPtrByteValue(addr, 0);
    }
    protected Byte[] intToByte(int val){
        return binConvertorService.toBin(val);
    }
    
     public void putValue(int addr, byte val) throws VmExecutionExeption{
         data.set(addr, val);
     }
    
    public void putValue(int addr, int val) throws VmExecutionExeption{
       Byte[] byteVal =  intToByte(val);
       putValue(addr, byteVal);
    }
    
    public void  putValue(int addr, Byte[] byteVal, int start, int end) throws VmExecutionExeption{
         int k = 0;
        for(int i = start; i < end; i++, k++){
            data.set(addr + k, byteVal[i]);
        }
    }
    
    public void  putValue(int addr, Byte[] byteVal) throws VmExecutionExeption{
         putValue(addr, byteVal, 0, byteVal.length);
    }
    
    public void  putValue(int addr, ArrayList<Byte> byteVal) throws VmExecutionExeption{
         putValue(addr, byteVal, 0,  byteVal.size());
    }
    
    public void  putValue(int addr, ArrayList<Byte> byteVal, int start, int end) throws VmExecutionExeption{
        int k = 0; 
        for(int i = start; i < end; i++, k++){
            data.set(addr + k, byteVal.get(i));
        }
    }
    
   
    public void putPtrValue(int addr, Byte[] byteVal) throws VmExecutionExeption{
       putValue(addr, new Byte[]{0});
       putValue(addr + GC_FLAG_SIZE, byteVal.length);
       putValue(addr + PTR_HEADERS_SIZE, byteVal);
    }
    
     public void putPtrIntField(int addr, int value, int fieldOffset) throws VmExecutionExeption{
          putValue(addr + PTR_HEADERS_SIZE + fieldOffset, value);
     }
    
    public void putPtrValue(int addr, int ptrSize) throws VmExecutionExeption{
      
       putValue(addr + GC_FLAG_SIZE, ptrSize);
    }
    
    public void  putPtrValue(int addr, ArrayList<Byte> byteVal, int start, int end) throws VmExecutionExeption{
        int k = 0; 
        putValue(addr, new Byte[]{0});
        putValue(addr + GC_FLAG_SIZE, end - start);
       
        for(int i = start; i < end; i++, k++){
            data.set(addr + PTR_HEADERS_SIZE + k, byteVal.get(i));
        }
    }
    
    public void fillPtrWithNull(int addr){
        int ptrSize = getIntValue(addr + GC_FLAG_SIZE);
        int fullSize = ptrSize + PTR_HEADERS_SIZE;
        int endPtr = addr + fullSize;
        
        for(int i = addr; i < endPtr; i++){
             data.set(i, null);
        }
       
    }
    
    protected int getRegisterAddr(VmSysRegister register){
        int addr = register.ordinal() * VM.INT_SIZE;
        return addr;
    }    
    protected void setSysRegister(VmSysRegister register, int value) throws VmExecutionExeption{
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
    

    
    protected int getBlockEndAddr(int blockStartAddr){
        //INT_SIZE is size of dield with block size 
        return blockStartAddr + getIntValue(blockStartAddr + GC_FLAG_SIZE) + PTR_HEADERS_SIZE;
    }
    
    
    protected void putData(Byte[] allocData, int startAddr){
         System.arraycopy(allocData, 0, data, startAddr, allocData.length);
    }
    
   
}
