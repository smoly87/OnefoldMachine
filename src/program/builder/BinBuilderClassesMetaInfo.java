/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.builder;

import common.VarType;
import java.util.ArrayList;
import java.util.Map;
import types.TypesInfo;
import virtual.machine.VM;
import virtual.machine.VmMetaClassHeader;

/**
 *
 * @author Andrey
 */
public class BinBuilderClassesMetaInfo {
    
     protected TypesInfo typesInfo;
     public static final int HEADERS_SIZE = VmMetaClassHeader.values().length;
     
     public BinBuilderClassesMetaInfo(){

         this.typesInfo = TypesInfo.getInstance();
     }
     
     protected ArrayList<Byte> addClassBinMetaInfo(ClassInfo classInfo){
        BinObjBuilder binObjBuilder = new BinObjBuilder();
        //ClassCode|TotalSize|MethodsCount|FieldsCount|   MethodsList|FieldsList
        int totalSize = 0;
         System.out.println(">>Class " + classInfo.getCode());
        binObjBuilder.addInt(classInfo.getCode())
                     .addInt(totalSize)
                     .addInt(classInfo.getMethodsList().size())
                     .addInt(classInfo.getFieldsList().size())
                     .addInt(classInfo.getParentId());
        
        for(Map.Entry<Integer, MethodDescription> entry: classInfo.getMethodsList().entrySet()){
           MethodDescription methodDescr = entry.getValue();
           binObjBuilder.addInt(methodDescr.getCode())
                        .addInt(methodDescr.getAddress());
        }
        
       
        
        for(Map.Entry<Integer, FieldDescription> entry: classInfo.getFieldsList().entrySet()){
           FieldDescription fieldDescr = entry.getValue();
           int typeSize = typesInfo.getTypeSize(fieldDescr.getFieldType());
           totalSize += typeSize;
           binObjBuilder.addInt(fieldDescr.getCode())
                        .addInt(typeSize);
            System.out.println(String.format(">>fieldCode: %s %s" , fieldDescr.getCode(), fieldDescr.getFieldName()) );
        }
        // 4 is headers size
        totalSize += (classInfo.getMethodsList().size() + classInfo.getFieldsList().size() + HEADERS_SIZE) * VM.INT_SIZE;
        binObjBuilder.setInt(1, totalSize);
       
        return  binObjBuilder.getResult();
    }
    
    public ArrayList<Byte> getClassesMetaInfo(){
        ArrayList<Byte> res = new ArrayList<>();
        MetaClassesInfo metaInfo = MetaClassesInfo.getInstance();
        for(Map.Entry<String, ClassInfo> entry : metaInfo.getClassesMap().entrySet()){
           res.addAll( this.addClassBinMetaInfo(entry.getValue()));
        }
        
        return res;
        
    }
}