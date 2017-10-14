/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.expr;

import common.Token;
import common.VarType;
import compiler.AstCompiler;
import program.builder.FunctionDescription;
import program.builder.ProgramBuilder;
import syntax.analyser.AstNode;
import virtual.machine.VMCommands;

/**
 *
 * @author Andrey
 */
public class FunctionCompiler extends AstCompiler{

    protected String funcName;
    protected int declaredVarsCount;
    protected FunctionDescription funcDescr ;
    
    protected void processVarDescription(AstNode node, ProgramBuilder programBuilder){
        Token token = node.getToken();
        declaredVarsCount++;
        funcDescr.addArgDecription(token.getValue(), token.getVarType());
       
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
    
    @Override
    public void compileChild(AstNode node, ProgramBuilder programBuilder) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.  
        switch(node.getName()){
            case "FunctionId":  
                Token token = node.getToken();
                this.funcName = token.getValue();
                funcDescr = new FunctionDescription();
                programBuilder.setIsLocalContext(true);
                break;
            case "VarDescription":
                processVarDescription(node, programBuilder);
                break;
            case "ReturnStatement":
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
         // Stack should be cleared, it's way to delete all local variables
         if(totalVarsCount > 1){
             for (int i = 0; i < totalVarsCount; i++) {
                 programBuilder.addInstruction(VMCommands.Pop, "0", VarType.Integer);
             }
         }
        
    }
    
}
