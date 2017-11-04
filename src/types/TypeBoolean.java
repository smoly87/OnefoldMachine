/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package types;


/**
 *
 * @author Andrey
 */
public class TypeBoolean implements IType{

    @Override
    public Byte[] toBinary(String strValue) {
       return new Byte[]{strValue == "True" ? (byte)1 : (byte)0};
            
    }
    
     public Byte[] toBinary(Boolean value) {
       return new Byte[]{value ? (byte)1 : (byte)0};
            
    }
    public Boolean getValue(Byte[] value) {
        return value[0] == 1 ? true: false;
    }

    @Override
    public int getTypeSize() {
        return 1;
    }
   

  
    
}
