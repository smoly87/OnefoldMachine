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
    protected HashMap<String, FunctionDescription> funcsMap;
    
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
    public void addFunction(String funcName, FunctionDescription funcDescr){
        //funcAddressesMap
        funcsMap.put(funcName, funcDescr);
    }
    
    public FunctionDescription getFuncDescr(String funcName) throws CompilerException{
        if(!funcsMap.containsKey(funcName)){
            throw new CompilerException("Call unknown function: " + funcName);
        }
        return funcsMap.get(funcName);
    }
    
    public Boolean isFunctionExists(String funcName){
        return funcsMap.containsKey(funcName);
    }
    
    public int getEntryPoint(){
        //TODO: improve for class support
        int maxLineNum = 0;
        for(Map.Entry<String,FunctionDescription> entry: funcsMap.entrySet()){
            if(entry.getValue().getEndLineNumber() > maxLineNum){
                maxLineNum = entry.getValue().getEndLineNumber() ;
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
        if(!methodCodes.containsKey(value)){
            code = methodCodes.size();
            methodCodes.put(value, code);
        } else {
            code = methodCodes.get(value);
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
