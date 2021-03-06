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
import compiler.metadata.ClassInfo;
import compiler.metadata.FuncSignatureBuilder;
import java.util.LinkedList;
import compiler.metadata.FunctionDescription;
import compiler.metadata.MetaClassesInfo;
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

    protected FunctionDescription funcDescr ;
    protected int funStartCommandNum;
    protected TypesInfo typesInfo;
    protected int totalVarsTableSizeInstr;
    protected boolean firstStage = true;

    public boolean isFirtStage() {
        return firstStage;
    }

    public void setFirtStage(boolean firtStage) {
        this.firstStage = firtStage;
    }

    public FunctionCompiler(ProgramBuilder programBuilder){
        super(programBuilder);
        typesInfo = TypesInfo.getInstance();
    }
      
    protected void processVarDescription(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        Token token = node.getToken();
        //funcDescr.addArgDecription(token.getValue(), token.getVarType());
        programBuilder.addLocalVar(token.getValue(), token.getVarType());  
    }
    
    protected void addThisVar(ProgramBuilder programBuilder) throws CompilerException {      
        funcDescr.addArgDecription("this", VarType.Integer);
        programBuilder.addLocalVar("this", VarType.Integer);

    }
    
    protected void processReturnStatement(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        Token token = node.getToken(); 
        switch(token.getTagName()){
            case "Id":
                addVarLoadCommand(token.getValue());
                programBuilder.addInstruction(VMCommands.Push, VmSysRegister.T2.ordinal(), VarType.Integer);
                programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.SetRegister), VarType.Integer);
                break;
            //All other types Int, Float..    
            default:
                VarType type =  VarType.valueOf(token.getTagName()) ;
                programBuilder.addInstruction(VMCommands.Push, token.getValue(), type);
                programBuilder.addInstruction(VMCommands.Push, VmSysRegister.T2.ordinal(), VarType.Integer);
                programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.SetRegister), VarType.Integer);
        }
    }

    
    @Override
    public void compileChild(AstNode node) throws CompilerException {
        if(firstStage) {
            if(node.getName().equals("VarDescription")){
                Token token = node.getToken();
                funcDescr.addArgDecription(token.getValue(), token.getVarType());
            } 
            return;
        }
        switch(node.getName()){
            case "VarDescription":
                processVarDescription(node, programBuilder);
                break;  
            case "StartFunctionBody":
                programBuilder.addInstruction(VMCommands.NOP);
                funcDescr.setStartBody(programBuilder.commandsSize());
                programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.ArrangeFuncParams), VarType.Integer);
                break;
            case "ReturnStatement":             
                processReturnStatement(node, programBuilder);
                //Return to call address
                programBuilder.addComment("Load Return Addr");
                programBuilder.addInstructionVarArg(VMCommands.Var_Load_Local, "__ReturnAddress",  true);
                programBuilder.addInstruction(VMCommands.Push, VmSysRegister.T1.ordinal(), VarType.Integer);
                programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.SetRegister), VarType.Integer);
                
                programBuilder.addInstructionVarArg(VMCommands.Var_Load_Local, "__FrameTableStart",  true);
                programBuilder.addInstructionVarArg(VMCommands.Var_Load_Local, "__FrameStackRegister",  true);
                programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.DeleteFrame), VarType.Integer);
                
                programBuilder.addInstruction(VMCommands.Push, VmSysRegister.T2.ordinal(), VarType.Integer);
                programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.GetRegister), VarType.Integer);
               
               
                programBuilder.addInstruction(VMCommands.Push, VmSysRegister.T1.ordinal(), VarType.Integer);
                programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.GetRegister), VarType.Integer);
                programBuilder.addInstruction(VMCommands.Jmp, 0, VarType.Integer);
                
                programBuilder.addInstruction(VMCommands.NOP);
                
                
                funcDescr.setEndLineNumber(programBuilder.commandsSize() );
                processVariables(programBuilder);
               
                
                break;
            case "EndFunction":
                break;
        }
    }
    
     @Override
    public void compileRootPre(AstNode node) throws CompilerException {
         Token token = node.findChild("FunctionId").getToken();
         programBuilder.setIsLocalContext(true);
         programBuilder.clearLocalVars();

         this.funcName = token.getValue();
         FuncSignatureBuilder signBuilder = new FuncSignatureBuilder();
         if(firstStage){
           funcDescr = new FunctionDescription(this.funcName);
           funcDescr.setLineNumber(programBuilder.commandsSize());
         } else{
           //
             LinkedList<AstNode> headerVarsNode = node.findChilds("VarDescription"); 
             for(AstNode varNode: headerVarsNode){
               signBuilder.addArgType(varNode.getToken().getVarType());
             }
             String argSignature = signBuilder.getSignature();
             ClassCompiler classCompiler = (ClassCompiler)this.getCompiler("Class");
             ClassInfo classInfo = classCompiler.getClassInfo();
             funcDescr = classInfo.getMethodDescription(this.funcName, argSignature);
             
             LinkedList<AstNode> localVarList = node.findChild("FunctionBody").findChilds("Var");
             funcDescr.setLocalVarsCount(localVarList.size());
         }
        
         
         
    }

    public void processVariables( ProgramBuilder programBuilder) {
         programBuilder.setIsLocalContext(false);
         programBuilder.clearLocalVars();
         VarCompiler varCompiler = (VarCompiler)this.getCompiler("Var");
         varCompiler.clearLocalVarsCount();
    }
    
    
    public FunctionDescription getCurrentFunction(){
        return funcDescr;
    }
    
}
