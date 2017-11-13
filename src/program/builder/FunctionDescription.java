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
    protected FuncSignatureBuilder argsSignatureBuilder;
    protected Integer lineNumber;

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }
    protected Integer startBody;
    protected Integer endLineNumber;
    protected String signature;
    protected Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getStartBody() {
        return startBody;
    }

    public void setStartBody(Integer startBody) {
        this.startBody = startBody;
    }

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

    public FunctionDescription(String funcName) {
        this.funcName = funcName;
        argsMap = new LinkedList<>();
        argsSignatureBuilder = new FuncSignatureBuilder();
    }
    
     
    public FunctionArgDescription getArgDescr(int argNum){
        return argsMap.get(argNum);
    }
    
    public void addArgDecription(String varName, VarType varType){
        FunctionArgDescription argDescr = new FunctionArgDescription(varName, argsMap.size(), varType);
        argsMap.add(argDescr);
         
        argsSignatureBuilder.addArgType(varType);
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
    
    
    protected String getSignature(){
        return argsSignatureBuilder.getSignature();
    }
    
    public static String getFullName(String funcName, String signature){
        return funcName + "#" + signature;
    }
    
    public String getFullName(){
        return this.getFullName(this.getFuncName(), this.getSignature());
    }
    /*public VarType getType(String varName){
        return argsMap.get(varName);
    }*/
}
