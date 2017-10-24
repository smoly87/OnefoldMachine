/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package program.builder;

import common.Token;
import common.VarType;
import compiler.exception.CompilerException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import types.TypesInfo;
import virtual.machine.DataBinConvertor;
import virtual.machine.Instructions;
import virtual.machine.Program;
import virtual.machine.VM;
import virtual.machine.VMCommands;
import virtual.machine.VMSysFunction;
import virtual.machine.VmExeHeader;

/**
 *
 * @author Andrey
 */
public class ProgramBuilder  {
   
    
    protected StringJoiner asmText;
    protected Instructions instructionsService;
    protected DataBinConvertor binConvertorService;
    protected LinkedHashMap<String, VarDescription> varsMap;
    protected LinkedHashMap<String, VarDescription> localVarsMap;
    protected LinkedHashMap<ValueDescription, Integer> valuesMap;
    
    protected int totalLocalVarSizes;

    public int getTotalLocalVarSizes() {
        return totalLocalVarSizes;
    }
    
    protected int pos;
    protected boolean isLocalContext;

    protected ArrayList<Byte> progData;
    protected Integer lineCount;

    protected TypesInfo typesInfo;
    
    public Integer getLineCount() {
        return progData.size();
    }
    
    
    public boolean isIsLocalContext() {
        return isLocalContext;
    }

    public void setIsLocalContext(boolean isLocalContext) {
        this.isLocalContext = isLocalContext;
    }
    
    public void clearLocalVars(){
        localVarsMap.clear();
        totalLocalVarSizes = 0;
    }
    
    public ProgramBuilder(){
        asmText = new StringJoiner("\\n");
        instructionsService = Instructions.getInstance();
        binConvertorService = DataBinConvertor.getInstance();
        varsMap = new LinkedHashMap<>();
        valuesMap = new LinkedHashMap<>();
        localVarsMap = new LinkedHashMap<>();
        progData = new ArrayList<>();
        
        totalLocalVarSizes = 0;
        typesInfo = TypesInfo.getInstance();
        
        addInstruction(VMCommands.NOP, 0, VarType.Integer);
    }
    
    public void addVar(String name, VarType type){
       addVar(name, type, varsMap) ;   
    }
    
    public void addLocalVar(String name, VarType type){
        addVar(name, type, localVarsMap) ;
        totalLocalVarSizes += typesInfo.getTypeSize(type);
    }
    
    protected void addVar(String name, VarType type,LinkedHashMap<String, VarDescription> varContainer  ){
        VarDescription descr = new VarDescription( type, (byte)varContainer.size());
        if(!varContainer.containsKey(name)){
            varContainer.put(name, descr);
        } 
    }
    
    public boolean isVarExists(String varName){
        return varsMap.containsKey(varName);
    }
   
    public int  getLocalVarCode(String name) throws CompilerException{
        if(localVarsMap.containsKey(name)){
            return localVarsMap.get(name).getCode();
        } else {
           throw new CompilerException("Undeclared local variable: " + name);
        
        }
    }
    
    public Boolean isLocalVariableExists(String varName){
        return  localVarsMap.containsKey(varName);
    }
    
    public int  getVarCode(String name) throws CompilerException{
        if(varsMap.containsKey(name)){
            return varsMap.get(name).getCode();
        } else{
            throw new CompilerException("Undeclared variable: " + name);
        }
    }
    
    public int getConstCode(ValueDescription descr){
        return 0;
    }
  
    public Program getResult() throws CompilerException{
        BinBuilder binBuilder = new BinBuilder();
        
        ArrayList<Byte> binData = binBuilder.addHeadersPlaceHolder()
                  .addConstSection(valuesMap)
                  .addVarSection(varsMap)
                  .addClassesMetaInfo()
                  .addSection(this.progData)
                  .addEntryPoint()
                  .getResult();
        
        return new Program(binData);
    }
    
       
    protected void addData(Byte data){
        this.progData.add(data);
    }
    
    protected void addData(ArrayList<Byte> data){
        this.progData.addAll(data);
    }
    
    public void addInstruction(VMCommands command) throws CompilerException{
        this.addInstruction(command, "0", VarType.Integer );
    }
    
    public void changeCommandArg(int commandNumStart, int value, VarType type){
         ValueDescription valDescr = new ValueDescription(type, Integer.toString(value));
        int constInd = 0;
         if(!valuesMap.containsKey(valDescr)){
            constInd = valuesMap.size();
            valuesMap.put(valDescr, constInd);
        } else{
            constInd = valuesMap.get(valDescr);
        }
        // System.err.println(String.format("Changeat pos: %s value: %s ", commandNumStart, value));
        binConvertorService.setIntegerToByteList(progData, constInd, commandNumStart + 1  );
    }
    
    public void addInstruction(VMCommands command, int value, VarType type){
        addInstruction(command, Integer.toString(value), type, true);
    }        
    
    public void addInstruction(VMCommands command, String value, VarType type) throws CompilerException{
        if(type == null){
            throw new CompilerException(String.format("Type of variable %s is undefined! Probably it's error in compiler!", value));
        }
        addInstruction(command, value, type, true );
    }
    
    public void addInstruction(VMCommands command, String value, VarType type, Boolean constantTransform){
        this.addData((byte)command.ordinal());
        ArrayList<Byte> binVal = null;
        if (constantTransform) {
            ValueDescription valDescr = new ValueDescription(type, value);
            int constInd = 0;
            if (!valuesMap.containsKey(valDescr)) {
                constInd = valuesMap.size();
                valuesMap.put(valDescr, constInd);
            } else {
                constInd = valuesMap.get(valDescr);
            }

            binVal = binConvertorService.integerToByteList(constInd);
            asmText.add(command.toString() + " " + value + "("+ constInd +")");
        } else {
            binVal = binConvertorService.integerToByteList(Integer.parseInt(value));
            asmText.add(command.toString() + " " + value + "("+ value +")");
        }
        this.addData(binVal);
        
    }
    

    public void addInstructionVarArg(VMCommands command, String varName, Boolean isLocal) throws CompilerException{
        int varCode ;
        if(isLocal){
            varCode = this.getLocalVarCode(varName) ;
        } else {
            varCode = this.getVarCode(varName);
        }
        
       
        this.addData((byte)command.ordinal());
        this.addData(binConvertorService.integerToByteList(varCode));
        
       
        asmText.add(String.format("%s %s (%s)", command.toString(), varName, varCode ));
    }
     
   
  
    
    public String getAsmText(){
        return asmText.toString();
    }
     
  

}
