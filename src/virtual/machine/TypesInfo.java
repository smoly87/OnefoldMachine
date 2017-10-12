/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine;

import common.VarType;
import java.util.HashMap;

/**
 *
 * @author Andrey
 */
public class TypesInfo {
    protected static TypesInfo instance ;
    private TypesInfo(){
    }
       
   
    public  static TypesInfo getInstance(){
        if(instance == null) {
            instance = new TypesInfo();
        }
        return instance;
    }
    public int getTypeSize(byte typeCode){
        VarType type = VarType.values()[(int)typeCode];
        return getTypeSize(type);
    }
    
    public int getTypeSize(VarType type){
        int size = 0;
        switch(type){
            case Integer:
                size = VM.INT_SIZE;
                break;
        }
        
        return size;
    }
}
