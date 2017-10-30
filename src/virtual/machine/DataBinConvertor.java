/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine;

import common.VarType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import types.IType;
import types.TypeBoolean;
import types.TypeInteger;
import types.TypesInfo;

/**
 *
 * @author Andrey
 */
public class DataBinConvertor  {
    protected static DataBinConvertor instance ;
    protected HashMap<VarType, IType> convertors;
    protected TypesInfo typesInfoService;
    
    private DataBinConvertor(){
       typesInfoService = TypesInfo.getInstance();
    }
    public Integer bytesToInt(ArrayList<Byte> arr, int start) {
       return getIntegerConventor().bytesToInt(arr, start);
    }

    protected TypeInteger getIntegerConventor(){
         TypeInteger convertor = (TypeInteger) typesInfoService.getConvertor(VarType.Integer);
         return convertor;
    }
    
    protected TypeBoolean getBoolConvertor(){
        TypeBoolean convertor = (TypeBoolean) typesInfoService.getConvertor(VarType.Boolean);
        return convertor;
    }
    
    public Integer bytesToInt(Byte[] arr, int start) {
        ArrayList<Byte> lst = new ArrayList<Byte>(Arrays.asList(arr));
        return bytesToInt(lst, start);
    }
    
    
    public Byte[] toBin(int value) {
        
        return  getIntegerConventor().toBinary(value);
    }
   public Boolean bytesToBool(Byte[] arr) {
       return getBoolConvertor().getValue(arr);
    }

    public Byte[] toBin(Boolean value) {
        
        return  getBoolConvertor().toBinary(value);
    }
    
    public int getIntegerValue(Byte[] byteValue)  {
      
       return getIntegerConventor().getValue(byteValue);
    }
    
   
   
    public  static DataBinConvertor getInstance(){
        if(instance == null) {
            instance = new DataBinConvertor();
        }
        return instance;
    }
    
    public void setIntegerToByteList(ArrayList<Byte> lst, int value, int start) {
        Byte[] byteVal =  getIntegerConventor().toBinary(value);

        for(int i = 0; i < byteVal.length; i++){
             lst.set(i + start, byteVal[i]);
        }
       
    }
        
    public ArrayList<Byte> integerToByteList(int value){
        return new ArrayList<>(Arrays.asList(getIntegerConventor().toBinary(value))); 
    }
    
  
 

}
