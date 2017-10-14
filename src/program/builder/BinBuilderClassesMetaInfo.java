/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.builder;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Andrey
 */
public class BinBuilderClassesMetaInfo {
     protected BinObjBuilder binObjBuilder ;
     
     public BinBuilderClassesMetaInfo(){
         this.binObjBuilder = new BinObjBuilder();
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
           binObjBuilder.addInt(fieldDescr.getCode());
           binObjBuilder.addInt(fieldDescr.getFieldType().ordinal());
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
