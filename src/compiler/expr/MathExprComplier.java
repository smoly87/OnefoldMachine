/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.expr;

import common.VarType;
import compiler.AstCompiler;
import syntax.analyser.AstNode;
import program.builder.ProgramBuilder;
import virtual.machine.VMCommands;

/**
 *
 * @author Andrey
 */
public class MathExprComplier extends AstCompiler{

    @Override
    public void compileChild(AstNode node, ProgramBuilder programBuilder) {
        switch(node.getToken().getTagName()){
            //TODO: convension of naming
            case "Id":
                //TODO: if Var existed in global variables check
                // It should be fine with order of code execution
                String varName = node.getToken().getValue();
                if(programBuilder.isVarExists(varName)){
                     programBuilder.addInstruction(VMCommands.Var_Load, varName);
                } else {
                    programBuilder.addInstruction(VMCommands.Var_Load_Local, varName);
                }
               
                
                break;
            case "Integer":
                String constValue = node.getToken().getValue();
                programBuilder.addInstruction(VMCommands.Push, constValue, VarType.Integer);
        }
        
    }

    @Override
    public void compileRootPost(AstNode node, ProgramBuilder programBuilder) {
        switch(node.getToken().getName()){
                case "+":
                    programBuilder.addInstruction(VMCommands.Add);
                    break;
                case "*":
                    programBuilder.addInstruction(VMCommands.Mul);
                    break;
        }
    }
    
}
