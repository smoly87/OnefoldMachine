/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package types;

import common.VarType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import virtual.machine.VM;

/**
 *
 * @author Andrey
 */
public class TypesInfo {
    protected static TypesInfo instance ;
    protected HashMap<VarType, IType> convertors;
    
    private TypesInfo(){
        convertors = new HashMap<>();
    }
       
   
    public  static TypesInfo getInstance(){
        if(instance == null) {
            instance = new TypesInfo();
        }
        return instance;
    }
    
    public Byte[] convertToBin(String value, VarType type){
        IType convertor = getConvertor(type); 
        return convertor.toBinary(value);
    }
    
    public ArrayList<Byte> convertToBinList(String value, VarType type) {
        return new ArrayList(Arrays.asList(convertToBin(value, type)));
    }
    
    public IType getConvertor(VarType type)  {
        IType convertor = null;
        if (convertors.containsKey(type)) {
            convertor = convertors.get(type);
        } else {
            try {
                String className = "types.Type" + type.toString();
                convertor = (IType) Class.forName(className).newInstance();
                convertors.put(type, convertor);
            } catch (Exception ex) {
                System.err.println("Type Info Class not found:" + type.toString() + " " + ex.getMessage());
            }
        }

        return convertor;
       
    }
    
    
    public int getTypeSize(VarType type)  {
       IType  convertor =getConvertor(type);
       return convertor.getTypeSize();
    }
    
   
}
