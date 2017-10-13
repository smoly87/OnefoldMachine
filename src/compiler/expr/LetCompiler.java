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
public class LetCompiler extends AstCompiler{

    @Override
    public void compileChild(AstNode node, ProgramBuilder programBuilder) {
         String tokName = node.getToken().getTagName();
        switch(tokName){
             //Do Nothing with mathExpr
             //In math expr loadvar
             case "Integer":            
                 programBuilder.addInstruction(VMCommands.Push, node.getToken().getValue(), VarType.Integer);
                 break;
             case "Id":
                 String varName = node.getToken().getValue();
                 // TODO: Context of variable also!!!
                 //So we can make protection of modify global vars
                 // And what about Load Var Global and local
                 if(!programBuilder.isIsLocalContext()){
                     programBuilder.addInstruction(VMCommands.Var_Put, varName);
                 } else {
                     programBuilder.addInstruction(VMCommands.Var_Put_Local, varName);
                 }
                 
                 
                
                 break;
         }
    }

   
    
}
