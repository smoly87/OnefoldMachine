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
import main.ByteUtils;
import virtual.machine.VM;

/**
 *
 * @author Andrey
 */
public class TypeString implements IType{

    @Override
    public Byte[] toBinary(String strValue) {
        byte[] bytes = strValue.getBytes();  
        return ByteUtils.convert(bytes);
            
    }
    
    public String getValue(Byte[] charArr) throws UnsupportedEncodingException{
        return new String(ByteUtils.convert(charArr), "UTF-8");
    }

    @Override
    public int getTypeSize() {
        //Because it's pointer
        return VM.INT_SIZE;
    }
   

  
    
}
