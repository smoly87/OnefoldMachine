/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package types;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Andrey
 */
public class TypeInteger implements IType{

    @Override
    public Byte[] toBinary(String strValue) {
         Integer value = Integer.parseInt(strValue);
         return toBinary(value);
    }
    
    public Byte[] toBinary(int value){
        return new Byte[] {
            (byte)(value >>> 24),
            (byte)(value >>> 16),
            (byte)(value >>> 8),
            (byte)value};
    }
    
    public Integer getValue(Byte[] arr, int start){
         ArrayList<Byte> lst = new ArrayList<Byte>(Arrays.asList(arr));
         return bytesToInt(lst, start);
    }
    
    public Integer getValue(Byte[] byteValue){
        return getValue(byteValue, 0);
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

    @Override
    public int getTypeSize() {
        return 4;
    }
   

  
    
}
