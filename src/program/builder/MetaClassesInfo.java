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
    
    public static MetaClassesInfo getInstance(){
        if(instance == null){
            instance = new MetaClassesInfo();
        }
        return instance;
    }
    
    private MetaClassesInfo(){
        methodCodes = new HashMap<>();
        fieldsCodes = new HashMap<>();
    }
    
    public int getMethodCode(String methodName){
        int code = 0;
        if(!methodCodes.containsKey(methodName)){
            code = methodCodes.size();
            methodCodes.put(methodName, code);
        } else {
            code = methodCodes.get(methodName);
        }
        return code;
    }
    
    /*public int getFieldCode(String fieldName){
    }*/
}
