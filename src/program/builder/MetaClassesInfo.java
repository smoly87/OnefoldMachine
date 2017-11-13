/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.builder;

import compiler.exception.CompilerException;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Andrey
 */
public class MetaClassesInfo {
    protected HashMap<String, Integer> fieldsCodes;
    protected HashMap<String, Integer> methodCodes;
    protected static MetaClassesInfo instance;
    protected HashMap<String, ClassInfo> classesMap;
    protected HashMap<String, Integer> funcsMap;
    
    public HashMap<String, ClassInfo> getClassesMap() {
        return classesMap;
    }
    
    public static MetaClassesInfo getInstance(){
        if(instance == null){
            instance = new MetaClassesInfo();
        }
        return instance;
    }
    
    private MetaClassesInfo(){
        methodCodes = new HashMap<>();
        fieldsCodes = new HashMap<>();
        classesMap = new HashMap<>();
        funcsMap = new HashMap<>();
    }
    
    public boolean isFuncExists(String funcName, String signature){
        String fullName = FunctionDescription.getFullName(funcName, signature);
        return funcsMap.containsKey(fullName);
    }
    
    public int getFuncCode(String funcName, String signature) {
        String fullName = FunctionDescription.getFullName(funcName, signature);
        if(funcsMap.containsKey(fullName)){
            return funcsMap.get(fullName);
        } else{
            return -1;
        }
        
    }
    public int getFuncCodeOrAdd(String funcName, String signature) throws CompilerException{
        String fullName = FunctionDescription.getFullName(funcName, signature);
        if(!funcsMap.containsKey(fullName)){
           int code = funcsMap.size(); 
           funcsMap.put(fullName, code);
           return code;
        } else{
            return funcsMap.get(fullName);
        }
        
    }
    
    
    
    
    
    
    public Boolean isFunctionExists(String funcName){
        return funcsMap.containsKey(funcName);
    }
    
    public Boolean isClassExists(String className){
       return  classesMap.containsKey(className);
    }
    

    public int getEntryPoint(){
        
        int maxLineNum = 0;
        for(Map.Entry<String, ClassInfo> entry: classesMap.entrySet()){
            ClassInfo classInfo = entry.getValue();
            if(classInfo.getEndClassLine()> maxLineNum){
                maxLineNum = classInfo.getEndClassLine() ;
            }
        }
        
        return maxLineNum;
    }
    
    public int getMethodCode(String methodName){
         return getOrAddCode(methodCodes, methodName);
    }
    
    public int getFieldCode(String fieldName){
        return getOrAddCode(fieldsCodes, fieldName);
    }
    
    public ClassInfo getClassInfo(String className){
        if(!getClassesMap().containsKey(className)){
            return null;
        } else{
            return getClassesMap().get(className);
        }
    }
    
    protected int getOrAddCode(HashMap<String, Integer> collection, String value){
        int code = 0;
        if(!collection.containsKey(value)){
            code = collection.size();
            collection.put(value, code);
        } else {
            code = collection.get(value);
        }
        return code;
    }        
            
    public void addClassInfo(ClassInfo classInfo){
        classInfo.setCode(classesMap.size());
        classesMap.put(classInfo.getClassName(), classInfo);
    }
    
    /*public int getFieldCode(String fieldName){
    }*/
}
