/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.expr;

import common.VarType;
import compiler.AstCompiler;
import compiler.exception.CompilerException;
import program.builder.ProgramBuilder;
import syntax.analyser.AstNode;
import virtual.machine.VMCommands;

/**
 *
 * @author Andrey
 */
public class MathPrecedenceCompiler extends AstCompiler{

    public MathPrecedenceCompiler(ProgramBuilder programBuilder) {
        super(programBuilder);
    }
    
    @Override
    public void compileChild(AstNode node) throws CompilerException {
        switch(node.getToken().getTagName()){
            //TODO: convension of naming
            case "Id":
                String varName = node.getToken().getValue();
                this.addVarLoadCommand(varName);
                
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
    public  void compileRootPost(AstNode node) throws CompilerException{
       switch(node.getToken().getValue()){
           case "*":
               programBuilder.addInstruction(VMCommands.IMul);
               break;
       }
        
    }
}
