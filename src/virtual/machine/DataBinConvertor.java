/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Andrey
 */
public class DataBinConvertor {
    protected static DataBinConvertor instance ;
    private DataBinConvertor(){
        
    }
   
    public  static DataBinConvertor getInstance(){
        if(instance == null) {
            instance = new DataBinConvertor();
        }
        return instance;
    }
    
    public void setIntegerToByteList(ArrayList<Byte> lst, int value, int start){
        Byte[] byteVal = integerToByte(value);

        for(int i = 0; i < byteVal.length; i++){
             lst.set(i + start, byteVal[i]);
        }
       
    }
    
   /* public int getDataIntFromByteList(ArrayList<Byte> lst,  int start){
        int endInd = start + VirtualMachine.INT_SIZE;
        ArrayList<Byte> byteVal = new ArrayList<>(VirtualMachine.INT_SIZE);
        for(int i = 0; i < endInd; i++){
            byteVal lst.get(i + start);
        }
        return BytesToInt(byteVal);
    }*/
    
    public Byte[] integerToByte(int value){
         return new Byte[] {
            (byte)(value >>> 24),
            (byte)(value >>> 16),
            (byte)(value >>> 8),
            (byte)value};
    }
    
    public ArrayList<Byte> integerToByteList(int value){
        return new ArrayList<>(Arrays.asList(integerToByte(value))); 
    }
    
    public Integer bytesToInt(Byte[] arr, int start){
         ArrayList<Byte> lst = new ArrayList<Byte>(Arrays.asList(arr));
         return bytesToInt(lst, start);
    }
    public Integer bytesToInt(ArrayList<Byte> arr, int start){
        int value = 0;
        
        for(int i = 0; i < 4; i++){
            Byte curByte = arr.get(i+start);
            int offset = (3 - i) * 8;
            value |= (curByte& 0xFF) << (offset);
        }
      
        return value;
    }
}
