/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.expr;

import common.Token;
import compiler.AstCompiler;
import program.builder.FunctionDescription;
import program.builder.ProgramBuilder;
import syntax.analyser.AstNode;

/**
 *
 * @author Andrey
 */
public class FunctionCompiler extends AstCompiler{

    protected String funcName;
    protected int varsCount;
    
    FunctionDescription funcDescr = new FunctionDescription();
    protected void processVarsBlock(AstNode node, ProgramBuilder programBuilder){
        
        for (AstNode curNode : node.getChildNodes()) {
            Token curToken = curNode.getToken();
            funcDescr.addArgDecription(curToken.getValue(), curToken.getVarType());
        }
        programBuilder.addFunction(funcName, funcDescr);
    }
    
    protected void processReturnStatement(AstNode node, ProgramBuilder programBuilder){
        Token token = node.getToken(); 
        switch(token.getTagName()){
            case "Id":
                break;
            //All other types Int, Float..    
            default:
                
        }
    }
    
    @Override
    public void compileChild(AstNode node, ProgramBuilder programBuilder) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
       
        Token token = node.getToken();
        
        switch(node.getName()){
            case "Id":
                this.funcName = token.getName();
                break;
            case "VarsBlock":
                processVarsBlock(node, programBuilder);
                break;
            case "ReturnStatement":
                processReturnStatement(node, programBuilder);
                break;
        }
    }
    @Override
    public void compileRootPre(AstNode node, ProgramBuilder programBuilder) {
         programBuilder.setIsLocalContext(true);
    }
    
    @Override
    public void compileRootPost(AstNode node, ProgramBuilder programBuilder) {
         programBuilder.setIsLocalContext(false);
         
         // Stack should be cleared, it's way to delete all local variables
         for(int i = 0; i < varsCount; i++){
         }
    }
    
}
