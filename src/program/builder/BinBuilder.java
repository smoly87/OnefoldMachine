/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.builder;

import common.VarType;
import program.builder.VarDescription;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import virtual.machine.DataBinConvertor;
import types.TypesInfo;
import virtual.machine.VMCommands;
import virtual.machine.VM;
import virtual.machine.VmSections;

/**
 *
 * @author Andrey
 */
public class BinBuilder {
     protected ArrayList<Byte> fullData;
     protected DataBinConvertor binConverter;
     protected TypesInfo typesInfoService;
     
     public BinBuilder(){
         this.fullData = new ArrayList<>();
         binConverter =  DataBinConvertor.getInstance();
         typesInfoService = TypesInfo.getInstance();
     }
     
    
     
     public BinBuilder addSection( ArrayList<Byte> data){
         addData(data);
         return this;
     }
     
     protected int getHeadersSize(){
         return VmSections.values().length * VM.INT_SIZE;
     }
     
     public BinBuilder addHeadersPlaceHolder(){
         int headersSize = getHeadersSize();
         fullData = new ArrayList<>(Collections.nCopies(headersSize, (byte)0));
         
         return this;
     }
     
     public BinBuilder addData(ArrayList<Byte> data){
         fullData.addAll(data);
         return this;
     }
     
     public void writeHeader(VmSections header, int value){
         //All headers is int
         binConverter.setIntegerToByteList(fullData, value, header.ordinal() * VM.INT_SIZE); 
     }
     
     public BinBuilder addVarSection(LinkedHashMap<String, VarDescription> varsMap){

        
        
        for(Map.Entry<String, VarDescription> entry :varsMap.entrySet()){
            VarDescription varDescr = entry.getValue();
            fullData.addAll(binConverter.integerToByteList(varDescr.getCode()));
            int typeSize = typesInfoService.getTypeSize(varDescr.getType());
            fullData.addAll(binConverter.integerToByteList(typeSize));
        }
        
        //Each varible stored in 2 bytes - code and type
        // Each field is intger
        writeHeader( VmSections.VarTableSize, varsMap.size()); 
        writeHeader( VmSections.ClassesMetaInfoStart, fullData.size()); 

        return this;
        
    }
     
    public  BinBuilder addConstSection(LinkedHashMap<ValueDescription, Integer> valuesMap) {
        /*Byte structure is 
          int   byte  byte[] non fix length
          index|type|value
        */
        ArrayList<Byte> curData = new ArrayList<>();
        for(Map.Entry<ValueDescription, Integer> entry :valuesMap.entrySet()){
            ValueDescription valDescr = entry.getKey();  
            VarType varType = valDescr.getType();
            ArrayList<Byte> byteVal = typesInfoService.convertToBinList(valDescr.getValue(), varType);
            
            curData.addAll(binConverter.integerToByteList(entry.getValue()));
            curData.addAll(binConverter.integerToByteList(byteVal.size()));
            curData.addAll(byteVal);
        }
        
        fullData.addAll(curData);
        //Each varible stored in 2 bytes - code and type
        // Each field is intger
        
        writeHeader( VmSections.ConstTableSize, valuesMap.size()); 
        writeHeader( VmSections.ConstStart, getHeadersSize());
        writeHeader( VmSections.VarTableStart, fullData.size()); 

        return this;
    }
    

    
    public BinBuilder addClassesMetaInfo(){
        BinBuilderClassesMetaInfo metaBinBuilder = new BinBuilderClassesMetaInfo();
        fullData.addAll(metaBinBuilder.getClassesMetaInfo());
        
        writeHeader( VmSections.ClassesTableSize, MetaClassesInfo.getInstance().classesMap.size()); 
        writeHeader( VmSections.InstructionsStart, fullData.size()); 
        return this;
    }
    
    public ArrayList<Byte> getResult(){
        return fullData;
    } 
}
