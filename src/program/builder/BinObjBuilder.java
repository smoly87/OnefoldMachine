/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.builder;

import java.util.ArrayList;
import virtual.machine.DataBinConvertor;

/**
 *
 * @author Andrey
 */
public class BinObjBuilder {
   
   protected ArrayList<Byte> result ;

    public ArrayList<Byte> getResult() {
        return result;
    }
   protected DataBinConvertor binConverter;
    
   public  BinObjBuilder(){
       binConverter =  DataBinConvertor.getInstance();
       result = new ArrayList<>();
   }
   
   public BinObjBuilder addInt(int value){
       result.addAll(binConverter.integerToByteList(value));
       System.out.println("b>" + value);
       return this;
   } 
}
