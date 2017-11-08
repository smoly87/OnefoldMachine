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
import virtual.machine.VM;
import virtual.machine.VMCommands;

/**
 *
 * @author Andrey
 */
public class IfCompiler extends AstCompiler{
    protected Integer logExprCmdNum = 0;
    protected Integer jmpOutCmdNum = -1;
    @Override
    public void compileChild(AstNode node, ProgramBuilder programBuilder) throws CompilerException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.  
        switch(node.getName()){
            case "StartIf":
                
                logExprCmdNum = programBuilder.addInstruction(VMCommands.NOP);
                break;
            case "StartBody": 
                jmpOutCmdNum =  programBuilder.addInstruction(VMCommands.JmpIfNot, 0, VarType.Integer); // PlaceHolder
               
                break;
            case "End":
                
                programBuilder.addInstruction(VMCommands.NOP);
                int commandsSize = programBuilder.commandsSize(); 
                
                programBuilder.changeCommandArgByNum(jmpOutCmdNum, commandsSize, VarType.Integer, false);
                break;
        }
    }
}
