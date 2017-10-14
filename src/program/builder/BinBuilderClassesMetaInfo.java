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

/**
 *
 * @author Andrey
 */
public class BinBuilderClassesMetaInfo {
     protected BinObjBuilder binObjBuilder ;
     protected TypesInfo typesInfo;
     
     public BinBuilderClassesMetaInfo(){
         this.binObjBuilder = new BinObjBuilder();
         this.typesInfo = TypesInfo.getInstance();
     }
     
     protected void addClassBinMetaInfo(ClassInfo classInfo){
        //ClassCode|MethodsCount|FieldsCount|MethodsList|FieldsList
        binObjBuilder.addInt(classInfo.getCode());
        binObjBuilder.addInt(classInfo.getMethodsList().size());
        binObjBuilder.addInt(classInfo.getFieldsList().size());
        
        for(MethodDescription methodDescr: classInfo.getMethodsList()){
           binObjBuilder.addInt(methodDescr.getCode());
           binObjBuilder.addInt(methodDescr.getAddress());
        }
        
       
        
        for(FieldDescription fieldDescr: classInfo.getFieldsList()){
           int typeSize = typesInfo.getTypeSize(fieldDescr.getFieldType());
           
           binObjBuilder.addInt(fieldDescr.getCode());
           binObjBuilder.addInt(typeSize);
        }
        
        
       
        
    }
    
    public ArrayList<Byte> getClassesMetaInfo(){
        MetaClassesInfo metaInfo = MetaClassesInfo.getInstance();
        for(Map.Entry<String, ClassInfo> entry : metaInfo.getClassesMap().entrySet()){
            this.addClassBinMetaInfo(entry.getValue());
        }
        
        return  binObjBuilder.getResult();
    }
}
