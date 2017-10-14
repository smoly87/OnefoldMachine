/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.builder;

import java.util.HashMap;


/**
 *
 * @author Andrey
 */
public class MetaClassesInfo {
    protected HashMap<String, Integer> fieldsCodes;
    protected HashMap<String, Integer> methodCodes;
    protected static MetaClassesInfo instance;
    protected HashMap<String, ClassInfo> classesMap;

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
    }
    
    public int getMethodCode(String methodName){
         return getOrAddCode(methodCodes, methodName);
    }
    
    public int getFieldCode(String fieldName){
        return getOrAddCode(fieldsCodes, fieldName);
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
        classesMap.put(classInfo.getClassName(), classInfo);
    }
    
    /*public int getFieldCode(String fieldName){
    }*/
}
