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
import virtual.machine.VMCommands;

/**
 *
 * @author Andrey
 */
public class FunctionCallCompiler extends AstCompiler{

    protected FunctionDescription funcDescr;
    //Used to return control to line whoes call function
    protected Integer callFromLineNum;
    
    @Override
    public void compileChild(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        Token token =  node.getToken();
        String tokName = token.getTagName();
        switch(tokName){
             //Do Nothing with mathExpr
             //In math expr loadvar
             case "Id":
                 //ToDO: check in global context
                 funcDescr = programBuilder.getFuncDescr(token.getValue());
                 break;
             case "Integer":
                 programBuilder.addInstruction(VMCommands.Push, node.getToken().getValue(), VarType.Integer);
                 break;
        }
    }

    @Override
    public void compileRootPre(AstNode node, ProgramBuilder programBuilder) {
        callFromLineNum = programBuilder.getLineCount();
    }
    
    @Override
    public void compileRootPost(AstNode node, ProgramBuilder programBuilder) {
        programBuilder.addInstruction(VMCommands.Jmp, callFromLineNum.toString());
    }
}
