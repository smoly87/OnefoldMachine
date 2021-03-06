/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Andrey
 */
public class ByteUtils {
    public static byte[] convert(Byte[] arr){
        byte[] bytes = new byte[arr.length];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = arr[i];
        }
        
        return bytes;
    }
    
    public static Byte[] convert(byte[] arr ){
        Byte[] Bytes = new Byte[arr.length];   
          
        for (int i = 0; i < arr.length; i++) {
            Bytes[i] = arr[i];
        }
        
        return Bytes;
    }
    
    public static ArrayList<Byte> listFromArr(Byte[] arr){
       return new ArrayList<>(Arrays.asList(arr));
    }
    
    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
