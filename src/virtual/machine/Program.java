/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine;

import java.util.ArrayList;

/**
 *
 * @author Andrey
 */
public class Program {
    protected ArrayList<Byte> data;
    protected DataBinConvertor binConvertorService;
    
    public Program(){
        binConvertorService = DataBinConvertor.getInstance();
    }
    
    public Program(ArrayList<Byte> data){
        this();
        this.data = data;
    }
    
    public ArrayList<Byte> getData() {
        return data;
    }
    
    public int readHeader(VmExeHeader header){
      int headerOffset =  header.ordinal() * VM.INT_SIZE; 
      return  this.binConvertorService.bytesToInt(data, headerOffset); 
    }
    
}
