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
import program.builder.MetaClassesInfo;
import syntax.analyser.AstNode;
import program.builder.ProgramBuilder;
import virtual.machine.VMCommands;
import virtual.machine.VMSysFunction;

/**
 *
 * @author Andrey
 */
public class LetCompiler extends AstCompiler{
    
    protected boolean objLeftPart;
    
    public void addRightPartCommands(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        String tokName = node.getToken().getTagName();
         String varName = node.getToken().getValue();
        switch(tokName){
             //Do Nothing with mathExpr
             //In math expr loadvar
            case  "Id":
                if (!programBuilder.isIsLocalContext()) {
                    programBuilder.addInstructionVarArg(VMCommands.Var_Load, varName, programBuilder.isIsLocalContext());
                } else {
                    programBuilder.addInstructionVarArg(VMCommands.Var_Load_Local, varName, programBuilder.isIsLocalContext());
                }
             case "Integer":            
                 programBuilder.addInstruction(VMCommands.Push, node.getToken().getValue(), VarType.Integer);
                 break;
             
         
         }
    }
    protected void addCommandsLeftPartObj(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        String varName = node.getToken().getValue();
         //Put obj pointer to stack
        if (!programBuilder.isIsLocalContext()) {
            programBuilder.addInstructionVarArg(VMCommands.Var_Load, varName, programBuilder.isIsLocalContext());
        } else {
            programBuilder.addInstructionVarArg(VMCommands.Var_Load_Local, varName, programBuilder.isIsLocalContext());
        }
    }
    
    protected void addCommandsLeftPartObjField(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
       
       
    
        MetaClassesInfo metaInfo = MetaClassesInfo.getInstance();
        Integer fieldNum = metaInfo.getFieldCode(node.getToken().getValue());
        
      
        //programBuilder.addInstruction(VMCommands.Push, fieldValueToken.getValue(), fieldValueToken.getVarType());// fieldValue
        
        programBuilder.addInstruction(VMCommands.Push, fieldNum, VarType.Integer); // fieldNum
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.SetPtrField), VarType.Integer);
    }
    
    protected void addCommandsLeftPartVar(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        String varName = node.getToken().getValue();
        if (!programBuilder.isIsLocalContext()) {
            programBuilder.addInstructionVarArg(VMCommands.Var_Put, varName, programBuilder.isIsLocalContext());
        } else {
            programBuilder.addInstructionVarArg(VMCommands.Var_Put_Local, varName, programBuilder.isIsLocalContext());
        }
    }
    @Override
    public void compileChild(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        String nodeName = "";
        if( node.getName() != null) nodeName = node.getName();
        
        switch(nodeName){
            case "LetStart":
                objLeftPart = false;
                break;
            case "LeftObjName":
                objLeftPart = true;
                addCommandsLeftPartObj(node, programBuilder);
                break;
            case "RightPartExpr":
                addRightPartCommands(node, programBuilder);
                break;
            case "LeftVarName":
                if(objLeftPart){
                    addCommandsLeftPartObjField(node, programBuilder);
                } else{
                    addCommandsLeftPartVar(node, programBuilder);
                }
                break;
            
            /*default:
                System.err.println("Unknown part in Let compiler:" + node.getToken().getValue());*/
        }
    }

   
    
}
