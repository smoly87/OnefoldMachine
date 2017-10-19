/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.builder;

import common.VarType;
import java.util.SortedMap;
import java.util.TreeSet;
import javafx.collections.transformation.SortedList;
import types.TypesInfo;

/**
 *
 * @author Andrey
 */
public class ClassInfo {
    protected MetaClassesInfo metaInfo;
    protected TreeSet<MethodDescription> methodsList;
    protected TreeSet<FieldDescription> fieldsList;


    
    public TreeSet<MethodDescription> getMethodsList() {
        return methodsList;
    }

    public TreeSet<FieldDescription> getFieldsList() {
        return fieldsList;
    }
    
    protected String className;

    protected int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
    
    public String getClassName() {
        return className;
    }
    
    public ClassInfo(String className){
        metaInfo = MetaClassesInfo.getInstance();
        
        ClassMemeberComparator comparator = new ClassMemeberComparator();
        methodsList = new TreeSet<>(comparator);
        fieldsList = new TreeSet<>(comparator);
        
        this.className = className;
    }
    
    public void addMethod(String name, String signature, int address){
       int methodCode =  metaInfo.getMethodCode(name);
       MethodDescription methodDescr = new MethodDescription();
       methodDescr.setCode(methodCode);
       methodDescr.setAddress(address);
       
       methodsList.add(methodDescr);
    }
    
    public void addField(String name, VarType type){
       int methodCode =  metaInfo.getFieldCode(name);
     
       FieldDescription fieldDescr = new FieldDescription();
       fieldDescr.setCode(methodCode);
       fieldDescr.setFieldType(type);
       
       fieldsList.add(fieldDescr);
    }
    
    public int getFieldsSize(){
        int totalSize = 0;
        TypesInfo typesInfo = TypesInfo.getInstance();
        for(FieldDescription fieldDescription:fieldsList){
            totalSize += typesInfo.getTypeSize(fieldDescription.getFieldType()); 
        }
        
        return totalSize;
    }
}
