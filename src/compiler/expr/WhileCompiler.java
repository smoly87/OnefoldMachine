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
public class WhileCompiler extends AstCompiler{
    protected Integer logExprCmdNum = 0;
    protected Integer jmpOutCycleAddr = -1;

    public WhileCompiler(ProgramBuilder programBuilder) {
        super(programBuilder);
    }
    @Override
    public void compileChild(AstNode node) throws CompilerException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.  
        switch(node.getName()){
            case "StartCycle":
                
                logExprCmdNum = programBuilder.addInstruction(VMCommands.NOP);
                break;
            case "Start": 
                jmpOutCycleAddr =  programBuilder.addInstruction(VMCommands.JmpIfNot, 0, VarType.Integer); // PlaceHolder
               
                break;
            case "End":
                Integer startCycle = logExprCmdNum * VM.COMMAND_SIZE;
                programBuilder.addInstruction(VMCommands.Jmp, startCycle.toString() , VarType.Integer, false);
              
               
                //programBuilder.addInstruction(VMCommands.NOP);
                programBuilder.changeCommandArgByNum(jmpOutCycleAddr, programBuilder.commandsSize(), VarType.Integer, false);
                break;
        }
    }
}
