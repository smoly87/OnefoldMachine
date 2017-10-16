/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.expr;

import common.Token;
import common.VarType;
import compiler.AstCompiler;
import compiler.exception.CompilerException;
import program.builder.FunctionDescription;
import program.builder.ProgramBuilder;
import syntax.analyser.AstNode;
import types.TypesInfo;
import virtual.machine.VM;
import virtual.machine.VMCommands;
import virtual.machine.VMSysFunction;
import virtual.machine.memory.VmSysRegister;

/**
 *
 * @author Andrey
 */
public class FunctionCompiler extends AstCompiler{

    protected String funcName;
    protected int declaredVarsCount;
    protected FunctionDescription funcDescr ;
    protected int funStartCommandNum;
    protected TypesInfo typesInfo;
    protected int headVarsSize;
    
    public FunctionCompiler(){
        typesInfo = TypesInfo.getInstance();
    }
    
    protected void processVarDescription(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        Token token = node.getToken();
        declaredVarsCount++;
        funcDescr.addArgDecription(token.getValue(), token.getVarType());
        
        programBuilder.addLocalVar(token.getValue(), token.getVarType());
        int varInd = programBuilder.getLocalVarCode(token.getValue());
        
        int typeSize = typesInfo.getTypeSize(token.getVarType());
        headVarsSize += typeSize;
        String typeSizeStr = Integer.toString(typeSize);
        programBuilder.addInstruction(VMCommands.Push, typeSizeStr, VarType.Integer);
        programBuilder.addInstruction(VMCommands.Var_Declare_Local, Integer.toString(varInd), VarType.Integer);
        
       
    }
    
    protected void processReturnStatement(AstNode node, ProgramBuilder programBuilder){
        Token token = node.getToken(); 
        switch(token.getTagName()){
            case "Id":
                break;
            //All other types Int, Float..    
            default:
                 VarType type =  VarType.valueOf(token.getTagName()) ;
                 programBuilder.addInstruction(VMCommands.Push, token.getValue(), type);
                
        }
    }
    
    
    protected String regToStr(VmSysRegister reg){
        return  Integer.toString(reg.ordinal());
    }
    
    protected String sysFuncToStr(VMSysFunction sysFunc){
        return Integer.toString(sysFunc.ordinal());
    }
    
    protected void createFrameStack(ProgramBuilder programBuilder){
        programBuilder.addInstruction(VMCommands.Push, regToStr(VmSysRegister.StackHeadPos), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.GetRegister), VarType.Integer);

        programBuilder.addInstruction(VMCommands.Push, regToStr(VmSysRegister.FrameStackPos), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.SetRegister), VarType.Integer);

        funStartCommandNum = programBuilder.getLineCount();
        programBuilder.addInstruction(VMCommands.Push, "0", VarType.Integer); // PlaceHolder for Var count;
        //Create table of local variables addresses by index
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.MemAllocStack), VarType.Integer);

    }
    
    @Override
    public void compileChild(AstNode node, ProgramBuilder programBuilder) throws CompilerException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.  
        switch(node.getName()){
            case "FunctionId":  
                Token token = node.getToken();
                this.funcName = token.getValue();
                funcDescr = new FunctionDescription();
                headVarsSize = 0;
                
                programBuilder.setIsLocalContext(true);
                programBuilder.clearLocalVars();
                createFrameStack(programBuilder);
                
                break;
            case "VarDescription":
                processVarDescription(node, programBuilder);
                break;  
            case "ReturnStatement":
                //Clear local variables table
                programBuilder.addInstruction(VMCommands.Pop, "0", VarType.Integer);
                processReturnStatement(node, programBuilder);
                processVariables(programBuilder);
                programBuilder.addFunction(funcName, funcDescr);
                break;
        }
        this.callSubscribers(node, programBuilder);
    }
    @Override
    public void compileRootPre(AstNode node, ProgramBuilder programBuilder) {
       
         VarCompiler varCompiler  = (VarCompiler)this.getCompiler("Var");
         //if(node.getName() == "VarsBlock") processVarsBlock(node, programBuilder);
    }
    

    public void processVariables( ProgramBuilder programBuilder) {
         programBuilder.setIsLocalContext(false);
         VarCompiler varCompiler = (VarCompiler)this.getCompiler("Var");
         int totalVarsCount = declaredVarsCount + varCompiler.getLocalVarsCount();
         varCompiler.clearLocalVarsCount();
        
         //Amend placeholder to real value of vars count
         int localVarsTableSize = totalVarsCount * VM.INT_SIZE ;
         programBuilder.changeCommandArg(funStartCommandNum, localVarsTableSize , VarType.Integer);
        
    }
    
}
