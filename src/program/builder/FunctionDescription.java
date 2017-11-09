/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.builder;

import common.VarType;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import types.TypesInfo;

/**
 *
 * @author Andrey
 */
public class FunctionDescription {
    
    protected LinkedList<FunctionArgDescription> argsMap;
    protected Integer lineNumber;
    protected Integer endLineNumber;

    public Integer getEndLineNumber() {
        return endLineNumber;
    }

    public void setEndLineNumber(Integer endLineNumber) {
        this.endLineNumber = endLineNumber;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }
    protected String funcName;

    public String getFuncName() {
        return funcName;
    }
    protected Integer localVarsCount;

    public Integer getLocalVarsCount() {
        return localVarsCount;
    }

    public void setLocalVarsCount(Integer localVarsCount) {
        this.localVarsCount = localVarsCount;
    }

    public FunctionDescription(String funcName, Integer lineNumber) {
        this.lineNumber = lineNumber;
        this.funcName = funcName;
        argsMap = new LinkedList<>();
    }
    
     
    public FunctionArgDescription getArgDescr(int argNum){
        return argsMap.get(argNum);
    }
    
    public void addArgDecription(String varName, VarType varType){
        FunctionArgDescription argDescr = new FunctionArgDescription(varName, argsMap.size(), varType);
        argsMap.add(argDescr);
    }
    
    protected int fullVarsSize(){
        int totalSize = 0;
        TypesInfo typesInfo = TypesInfo.getInstance();
        
        for(FunctionArgDescription argsDescr: argsMap){
            totalSize += typesInfo.getTypeSize(argsDescr.getVarType());
        }
        
        return totalSize;
    }
    
    public int getArgsCount(){
        return argsMap.size();
    }
    
    public int getTotalVarsCount(){
        return this.getArgsCount() + this.getLocalVarsCount();
    }
    
    /*public VarType getType(String varName){
        return argsMap.get(varName);
    }*/
}
