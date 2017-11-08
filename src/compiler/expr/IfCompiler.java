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
    protected Boolean hasElseBlock = false;
    protected Integer jmpOutCmdNum = -1;
    protected Integer jmpOutIf = -1;
    
    protected void addJumpOutCmd(int commandNum, ProgramBuilder programBuilder) throws CompilerException{
        programBuilder.addInstruction(VMCommands.NOP);
        int commandsSize = programBuilder.commandsSize();
        programBuilder.changeCommandArgByNum(commandNum, commandsSize, VarType.Integer, false);
    }
    
    @Override
    public void compileChild(AstNode node, ProgramBuilder programBuilder) throws CompilerException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.  
        switch(node.getName()){
            case "StartIf":
                
                //this.hasElseBlock = false;
                break;
            case "StartBody": 
 
                jmpOutCmdNum =  programBuilder.addInstruction(VMCommands.JmpIfNot, 0, VarType.Integer); // PlaceHolder
                
                   
                
                break;
            
            case "End":
               if(!hasElseBlock){
                   addJumpOutCmd(jmpOutCmdNum, programBuilder);
               } else{
                   jmpOutIf = programBuilder.addInstruction(VMCommands.Jmp, 0, VarType.Integer); 
               }
              
                //  jmpOutCmdNum =  programBuilder.addInstruction(VMCommands.Jmp, 0, VarType.Integer); // PlaceHolder
                
              
                  
              
              break;
            case "EndElse":
                addJumpOutCmd(jmpOutIf, programBuilder);
                break;
            case "StartElseExpr":
                addJumpOutCmd(jmpOutCmdNum, programBuilder);
                //jmpOutCmdNum =  programBuilder.addInstruction(VMCommands.JmpIfNot, 0, VarType.Integer);
                break;
        }
    }
    
    @Override
    public  void compileRootPre(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        if(node.findChild("StartIf")!=null){
            this.hasElseBlock = (node.findChild("ElseBlock") != null);
        }
        
        super.compileRootPre(node, programBuilder);
    }
}
