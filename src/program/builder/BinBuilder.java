/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.builder;

import compiler.metadata.ValueDescription;
import compiler.metadata.MetaClassesInfo;
import common.VarType;
import compiler.exception.CompilerException;
import compiler.metadata.VarDescription;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import main.ByteUtils;
import types.TypeBoolean;
import types.TypeString;
import virtual.machine.DataBinConvertor;
import types.TypesInfo;
import virtual.machine.VMCommands;
import virtual.machine.VM;
import virtual.machine.VmExeHeader;
import virtual.machine.memory.Memory;

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
         return VmExeHeader.values().length * VM.INT_SIZE;
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
     
     public void writeHeader(VmExeHeader header, int value){
         //All headers is int
         binConverter.setIntegerToByteList(fullData, value, header.ordinal() * VM.INT_SIZE); 
     }
     
     public BinBuilder addCommentsSection(LinkedList<String> comments){
         TypeString convertor =(TypeString) TypesInfo.getInstance().getConvertor(VarType.String);
         for(String comment: comments){
             Byte[] val = convertor.toBinary(comment);
             fullData.addAll(binConverter.integerToByteList(val.length));
             fullData.addAll(ByteUtils.listFromArr(val));
         }
         writeHeader(VmExeHeader.CommentsCount, comments.size()); 
         writeHeader(VmExeHeader.InstructionsStart, fullData.size()); 
         return this;
     }
     
     public BinBuilder addVarSection(LinkedHashMap<String, VarDescription> varsMap){

        
        //TypeBoolean boolConv = TypesInfo.getInstance().getBoolConvertor();
        
        for(Map.Entry<String, VarDescription> entry :varsMap.entrySet()){
            VarDescription varDescr = entry.getValue();
            fullData.addAll(binConverter.integerToByteList(varDescr.getCode()));
            int typeSize = typesInfoService.getTypeSize(varDescr.getType());
            fullData.addAll(binConverter.integerToByteList(typeSize));
            fullData.add((varDescr.getClassName() != "") ? Memory.GC_FLAG_PTR : 0);
        }
        
        //Each varible stored in 2 bytes - code and type
        // Each field is intger
        writeHeader(VmExeHeader.VarTableSize, varsMap.size()); 
        writeHeader(VmExeHeader.ClassesMetaInfoStart, fullData.size()); 

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
        
        writeHeader(VmExeHeader.ConstTableSize, valuesMap.size()); 
        writeHeader(VmExeHeader.ConstStart, getHeadersSize());
        writeHeader(VmExeHeader.VarTableStart, fullData.size()); 

        return this;
    }
    

    public BinBuilder addEntryPoint() {
        MetaClassesInfo metaInfo =  MetaClassesInfo.getInstance();
       /* if(!metaInfo.isFunctionExists("main")){
            throw new CompilerException("Function with name main not exists! It's mandatory.");
        }
        FunctionDescription mainFunc = metaInfo.getFuncDescr("main");*/
        writeHeader(VmExeHeader.ProgramStartPoint, metaInfo.getEntryPoint());
        return this;
    }
    
    public BinBuilder addClassesMetaInfo(){
        BinBuilderClassesMetaInfo metaBinBuilder = new BinBuilderClassesMetaInfo();
        fullData.addAll(metaBinBuilder.getClassesMetaInfo());
        
        writeHeader(VmExeHeader.ClassesTableSize, MetaClassesInfo.getInstance().getClassesMap().size()); 
        writeHeader(VmExeHeader.CommentsStart, fullData.size()); 
        return this;
    }
    
    public ArrayList<Byte> getResult(){
        return fullData;
    } 
}
