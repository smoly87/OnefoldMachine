/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package program.builder;

import compiler.metadata.ValueDescription;
import compiler.metadata.VarDescription;
import compiler.metadata.MetaClassesInfo;
import common.Token;
import common.VarType;
import compiler.exception.CompilerException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
    
    protected LinkedList<String> codeComments;
    
    protected int totalLocalVarSizes;

    protected int commandsCount;

    public int getCommandsCount() {
        return commandsCount;
    }
    
    public int getTotalLocalVarSizes() {
        return totalLocalVarSizes;
    }
    
    protected int pos;
    protected boolean isLocalContext;

    protected ArrayList<Byte> progData;
    protected Integer lineCount;

    protected TypesInfo typesInfo;
    
    public Integer commandsSize() {
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
    
    public ProgramBuilder() throws CompilerException{
        asmText = new StringJoiner("\\n");
        instructionsService = Instructions.getInstance();
        binConvertorService = DataBinConvertor.getInstance();
        varsMap = new LinkedHashMap<>();
        valuesMap = new LinkedHashMap<>();
        localVarsMap = new LinkedHashMap<>();
        progData = new ArrayList<>();
        codeComments = new LinkedList<>();
        
        totalLocalVarSizes = 0;
        typesInfo = TypesInfo.getInstance();
        commandsCount = 0;
        addInstruction(VMCommands.NOP);
    }
    
    public void addVar(String name, VarType type){
       addVar(name, type, varsMap) ;   
    }
    
    public void addVar(String name, String className ){
        
       addVar(name, VarType.ClassPtr, className, varsMap) ;   
    }
    
    public void addLocalVar(String name, VarType type){
        addVar(name, type, localVarsMap) ;
        totalLocalVarSizes += typesInfo.getTypeSize(type);
    }
    
    public void addLocalVar(String name, String className){
        addVar(name, VarType.Integer,className, localVarsMap) ;
        totalLocalVarSizes += typesInfo.getTypeSize(VarType.Integer);
    }
    protected void addVar(String name, VarType type, LinkedHashMap<String, VarDescription> varContainer  ){
        VarDescription descr = new VarDescription( type, (byte)varContainer.size());
        if(!varContainer.containsKey(name)){
            varContainer.put(name, descr);
        } 
    }
    
    protected void addVar(String name, VarType type, String className, LinkedHashMap<String, VarDescription> varContainer  ) {
       
        MetaClassesInfo metaInfo = MetaClassesInfo.getInstance();
        int classId = metaInfo.getClassInfo(className).getCode();
        VarDescription descr = new VarDescription( type, (byte)varContainer.size(), classId);
        descr.setClassName(className);
        
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
        return getVarDescription(name).getCode();
    }
    
    public VarDescription getVarDescription(String name) throws CompilerException{
        if(isLocalVariableExists(name)){
            return localVarsMap.get(name);
        }
        
        if(isVarExists(name)){
            return varsMap.get(name);
        } else{
            throw new CompilerException("<<Undeclared variable: " + name);
        }
    }
    
    public int getVarClassId(String name) throws CompilerException{
       return getVarDescription(name).getClassId();
    }
    

    public Program getResult() throws CompilerException{
        BinBuilder binBuilder = new BinBuilder();
        
        ArrayList<Byte> binData = binBuilder.addHeadersPlaceHolder()
                  .addConstSection(valuesMap)
                  .addVarSection(varsMap)
                  .addClassesMetaInfo()
                  .addCommentsSection(codeComments)
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
    
    public void addComment(String comment){
        codeComments.add(comment);
        Integer commentId = codeComments.size();
        this.addInstruction(VMCommands.NOP, commentId.toString(), VarType.Integer, false);
    }
    
    public int addInstruction(VMCommands command) throws CompilerException{
        return this.addInstruction(command, "0", VarType.Integer );
    }
    
    protected int getConstInd(String value, VarType type){
        ValueDescription valDescr = new ValueDescription(type, value);
        int constInd = 0;
        if (!valuesMap.containsKey(valDescr)) {
            constInd = valuesMap.size();
            valuesMap.put(valDescr, constInd);
        } else {
            constInd = valuesMap.get(valDescr);
        }
        return constInd;
            
    }
    
    public void changeCommandArg(int commandNumStart, int value, VarType type){
        int constInd = getConstInd(Integer.toString(value), type);
        // System.err.println(String.format("Changeat pos: %s value: %s ", commandNumStart, value));
        binConvertorService.setIntegerToByteList(progData, constInd, commandNumStart + 1  );
    }
    
    public void changeCommandArgByNum(int commandNum, int value, VarType type, boolean transFormConst){
        int valueForPut = -1;
        if(transFormConst){
            valueForPut = getConstInd(Integer.toString(value), type);
        } else{
            valueForPut = value;
        }
     
        int startPos = commandNum * VM.COMMAND_SIZE + 1;
        binConvertorService.setIntegerToByteList(progData, valueForPut, startPos );
    }
    
    public int addInstruction(VMCommands command, int value, VarType type){
        return addInstruction(command, Integer.toString(value), type, true);
    }        
    
    public int addInstruction(VMCommands command, String value, VarType type) throws CompilerException{
       if(type == null) throw new CompilerException(String.format("Undefined variable type:%s. Seems as problem in parsing/compiler logic", value));
       return addInstruction(command, value, type, true );
    }
    
    public int addInstruction(VMCommands command, String value, VarType type, Boolean constantTransform){
        this.addData((byte)command.ordinal());
        ArrayList<Byte> binVal = null;
        if (constantTransform) {
            int constInd = getConstInd(value, type);

            binVal = binConvertorService.integerToByteList(constInd);
            asmText.add(command.toString() + " " + value + "("+ constInd +")");
        } else {
            binVal = binConvertorService.integerToByteList(Integer.parseInt(value));
            asmText.add(command.toString() + " " + value + "("+ value +")");
        }
        this.addData(binVal);
        return commandsCount++;
    }
    

    public int addInstructionVarArg(VMCommands command, String varName, Boolean isLocal) throws CompilerException{
        int varCode = -1 ;
        
        if (this.isLocalVariableExists(varName)) {
            varCode = this.getLocalVarCode(varName);
        } else {
            if (this.isVarExists(varName)) {
                varCode = this.getVarCode(varName);
            } else {
                throw new CompilerException(String.format(">>Undeclared local variable: %s", varName));
            }

        }

  
        
       
        this.addData((byte)command.ordinal());
        this.addData(binConvertorService.integerToByteList(varCode));
        
        
        asmText.add(String.format("%s %s (%s)", command.toString(), varName, varCode ));
        return commandsCount++;
    }
     
   
  
    
    public String getAsmText(){
        return asmText.toString();
    }
     
  

}
