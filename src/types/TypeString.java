/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package types;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Andrey
 */
public class TypeString implements IType{

    @Override
    public Byte[] toBinary(String strValue) {
        byte[] bytes = strValue.getBytes();  
        Byte[] Bytes = new Byte[bytes.length];   
          
        for (int i = 0; i < bytes.length; i++) {
            Bytes[i] = bytes[i];
        }
        
        return Bytes;
            
    }
    
    
    
    public String getValue(Byte[] charArr) throws UnsupportedEncodingException{
        byte[] bytes = new byte[charArr.length];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = charArr[i];
        }
        
        return new String(bytes, "UTF-8");
    }

    @Override
    public int getTypeSize() {
        return 4;
    }
   

  
    
}
