/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.builder;

import common.VarType;
import java.util.HashMap;

/**
 *
 * @author Andrey
 */
public class FunctionDescription {
    
    protected HashMap<String, VarType> argsMap;
    public void addArgDecription(String varName, VarType varType){
        argsMap.put(varName, varType);
    }
    
    public VarType getType(String varName){
        return argsMap.get(varName);
    }
}
