/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.expr;

import common.VarType;
import compiler.AstCompiler;
import compiler.exception.CompilerException;
import syntax.analyser.AstNode;
import program.builder.ProgramBuilder;
import syntax.analyser.CompilerUndeclaredVariableException;
import virtual.machine.VMCommands;

/**
 *
 * @author Andrey
 */
public class MathExprComplier extends AstCompiler{

    @Override
    public void compileChild(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        switch(node.getToken().getTagName()){
            //TODO: convension of naming
            case "Id":
                //TODO: if Var existed in global variables check
                // It should be fine with order of code execution
                String varName = node.getToken().getValue();
                this.addVarLoadCommand(varName, programBuilder);
                
                break;
            case "Integer": case "Float":
                String constValue = node.getToken().getValue();
                VarType type = VarType.valueOf(node.getToken().getTagName());
                programBuilder.addInstruction(VMCommands.Push, constValue, type );
                break;
            default:
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
