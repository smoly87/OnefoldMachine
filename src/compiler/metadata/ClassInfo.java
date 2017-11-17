/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.metadata;

import common.VarType;
import compiler.exception.CompilerException;
import java.util.HashSet;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import javafx.collections.transformation.SortedList;
import program.builder.ClassMemeberComparator;
import types.TypesInfo;

/**
 *
 * @author Andrey
 */
public class ClassInfo {
    protected MetaClassesInfo metaInfo;
    protected TreeMap<Integer, FunctionDescription> methodsList;
    protected TreeMap<Integer, FieldDescription> fieldsList;
    protected HashSet<String> methodsListStr;
    protected HashSet<String> fieldsListStr;
    
    protected String parentClass;
    protected int parentId = -1;

    protected int endClassLine;

    public int getEndClassLine() {
        return endClassLine;
    }

    public void setEndClassLine(int endClassLine) {
        this.endClassLine = endClassLine;
    }
    
    /*public FunctionDescription getFunctionDescription(){
        
    }*/
    
    public boolean isMethodExists(String methodName, String signature){
        int code = metaInfo.getFuncCode(methodName, signature);
        if(methodsList.containsKey(code)) return true;
        if(parentClass !=""){
           String curParentClass =  parentClass;
           while(curParentClass !=null){
              ClassInfo parentClassInfo = metaInfo.getClassInfo(curParentClass); 
              if(parentClassInfo.isMethodExists( methodName, signature)) return true;
              curParentClass = parentClassInfo.getParentClass();
           } 
        }
        return false;
    }
    
    public FunctionDescription getMethodDescription(String methodName, String signature){
 
        int code = metaInfo.getFuncCode(methodName, signature);
        if(methodsList.containsKey(code)) return methodsList.get(code);
        
        if(parentClass !=""){
           String curParentClass =  parentClass;
           while(curParentClass !=null){
              ClassInfo parentClassInfo = metaInfo.getClassInfo(curParentClass); 
              if(parentClassInfo.isMethodExists( methodName, signature)) {
                 return parentClassInfo.getMethodDescription(methodName, signature);
              }
              curParentClass = parentClassInfo.getParentClass();
           } 
        }
        return null;
        //return methodsList.get(code);
    }
    
    public int getParentId() {
        return parentId;
    }

    

    public String getParentClass() {
        return parentClass;
    }

    public void setParentClass(String parentClass) {
        this.parentClass = parentClass;
        this.parentId = MetaClassesInfo.getInstance().getClassInfo(parentClass).getCode();
    }


    public Boolean isFieldExists(String fieldName){
        if (fieldsListStr.contains(fieldName)) return true;
        if(parentClass !=""){
           String curParentClass =  parentClass;
           while(curParentClass !=null){
              ClassInfo parentClassInfo = metaInfo.getClassInfo(curParentClass); 
              if(parentClassInfo.isFieldExists(fieldName)) return true;
              curParentClass = parentClassInfo.getParentClass();
           } 
        }
        
        return false;
    }
    
    public TreeMap<Integer, FunctionDescription> getMethodsList() {
        return methodsList;
    }

    public TreeMap<Integer, FieldDescription> getFieldsList() {
        return getFieldsList(new TreeMap<>());
    }
    
    protected TreeMap<Integer, FieldDescription> getFieldsList(TreeMap<Integer, FieldDescription> childsFieldsMap) {
        this.addDifference(childsFieldsMap);
        
        if(this.parentClass != null){
            ClassInfo parentClassInfo = metaInfo.getClassInfo(this.parentClass);
            parentClassInfo.addDifference(childsFieldsMap);
        }
        
        return childsFieldsMap;
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
        methodsList = new TreeMap<>();
        fieldsList = new TreeMap<>();
        
        methodsListStr = new HashSet<>();
        fieldsListStr = new HashSet<>();
        //Add auto generated fields ClassId|LinksCount
        this.addField("__ClassId", VarType.Integer);
        this.addField("__LinksCount", VarType.Integer);
        
        
        this.className = className;
    }
    
    public void addMethod(FunctionDescription methodDescr) throws CompilerException{
       //FunctionDescription methodDescr =   metaInfo.getFuncDescr(name, signature);
      /* MethodDescription methodDescr = new MethodDescription();
       
       methodDescr.setCode(methodCode);
       methodDescr.setAddress(address);*/
       //methodDescr.setCode(methodsList.size());
       String methodSign = methodDescr.getSignature();
       String funcName = methodDescr.getFuncName();
       methodDescr.setCode(metaInfo.getFuncCodeOrAdd(funcName, methodSign));
       this.methodsList.put(methodDescr.getCode(), methodDescr);
       //methodsList.put(methodDescr.getCode(), methodDescr);
       methodsListStr.add(methodDescr.getFullName());
       
    }
    
    public void addField(String name, VarType type){
       int methodCode =  metaInfo.getFieldCode(name);
     
       FieldDescription fieldDescr = new FieldDescription();
       fieldDescr.setCode(methodCode);
       fieldDescr.setFieldType(type);
       fieldDescr.setFieldName(name);
       
       fieldsList.put(methodCode, fieldDescr);
       fieldsListStr.add(name);
    }
    
    public int getFieldsSize(){
       return getFieldsSize(new TreeMap<>());
    }
    
    /**
     * 
     * @return size of difference with current class
     */
    protected int addDifference(TreeMap<Integer, FieldDescription> childsFieldsMap){
        int totalSize = 0;
        TypesInfo typesInfo = TypesInfo.getInstance();
        for(Map.Entry<Integer, FieldDescription> entry :fieldsList.entrySet()){
            FieldDescription fieldDescription = entry.getValue();
            if(!childsFieldsMap.containsKey(fieldDescription.getCode())){
                totalSize += typesInfo.getTypeSize(fieldDescription.getFieldType()); 
                childsFieldsMap.put(entry.getKey(), fieldDescription);
            }
            
        }
        
        return totalSize;
    }
    
    public int getFieldsSize(TreeMap<Integer, FieldDescription> childFieldsMap){

        int totalSize = this.addDifference(childFieldsMap);
      
        
        if(this.parentClass != null){
            ClassInfo parentClassInfo = metaInfo.getClassInfo(this.parentClass);
            totalSize += parentClassInfo.getFieldsSize(childFieldsMap);
        }
       
        return totalSize;
    }
}
