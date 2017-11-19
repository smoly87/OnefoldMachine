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


    public void compileOperChild(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        switch(node.getToken().getTagName()){
            case "Id":
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
    
    protected void processOperatorNode(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        for(AstNode childNode: node.getChildNodes()){
            if(childNode.getToken().getTagName().equals("Operator")){
                processOperatorNode(childNode, programBuilder);
            } else{
                compileOperChild(childNode, programBuilder);
            }
        }
        switch(node.getToken().getName()){
                case "+":
                    programBuilder.addInstruction(VMCommands.Add);
                    break;
                case "-":
                    programBuilder.addInstruction(VMCommands.Sub);
                    break;
                case "*":
                    programBuilder.addInstruction(VMCommands.Mul);
                    break;
        }
    }
    
    @Override
    public void compileRootPre(AstNode node, ProgramBuilder programBuilder) throws CompilerException {
        processOperatorNode(node, programBuilder);
    }
    
}
