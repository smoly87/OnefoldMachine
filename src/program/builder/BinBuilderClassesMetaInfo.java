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

/**
 *
 * @author Andrey
 */
public class BinBuilderClassesMetaInfo {
     protected BinObjBuilder binObjBuilder ;
     protected TypesInfo typesInfo;
     protected final int HEADERS_SIZE = 4;
     
     public BinBuilderClassesMetaInfo(){
         this.binObjBuilder = new BinObjBuilder();
         this.typesInfo = TypesInfo.getInstance();
     }
     
     protected void addClassBinMetaInfo(ClassInfo classInfo){
        //ClassCode|MethodsCount|FieldsCount|MethodsList|FieldsList
        int totalSize = 0;
        binObjBuilder.addInt(classInfo.getCode())
                     .addInt(totalSize)
                     .addInt(classInfo.getMethodsList().size())
                     .addInt(classInfo.getFieldsList().size());
        
        for(MethodDescription methodDescr: classInfo.getMethodsList()){
           binObjBuilder.addInt(methodDescr.getCode())
                        .addInt(methodDescr.getAddress());
        }
        
       
        
        for(FieldDescription fieldDescr: classInfo.getFieldsList()){
           int typeSize = typesInfo.getTypeSize(fieldDescr.getFieldType());
           totalSize += typeSize;
           binObjBuilder.addInt(fieldDescr.getCode())
                        .addInt(typeSize);
        }
        // 4 is headers size
        totalSize += (classInfo.getMethodsList().size() + classInfo.getFieldsList().size() + HEADERS_SIZE) * VM.INT_SIZE;
        binObjBuilder.setInt(1, totalSize);
       
        
    }
    
    public ArrayList<Byte> getClassesMetaInfo(){
        MetaClassesInfo metaInfo = MetaClassesInfo.getInstance();
        for(Map.Entry<String, ClassInfo> entry : metaInfo.getClassesMap().entrySet()){
            this.addClassBinMetaInfo(entry.getValue());
        }
        
        return  binObjBuilder.getResult();
    }
}
