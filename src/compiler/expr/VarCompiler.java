/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.expr;

/**
 *
 * @author Andrey
 */


import common.VarType;
import compiler.AstCompiler;
import compiler.AstCompiler;
import program.builder.ProgramBuilder;
import syntax.analyser.AstNode;
import virtual.machine.VMCommands;
public class VarCompiler extends AstCompiler{

    protected int localVarsCount;

    public int getLocalVarsCount() {
        return localVarsCount;
    }
    public void clearLocalVarsCount(){
        localVarsCount = 0;
    }
    
    @Override
    public void compileChild(AstNode node, ProgramBuilder programBuilder) {
      
    }

    @Override
    public void compileRootPost(AstNode node, ProgramBuilder programBuilder) {
        AstNode idNode = node.getChildNodes().get(0);
        AstNode typeNode = node.getChildNodes().get(1);
        String typeName = typeNode.getToken().getValue();
        
        VarType type = VarType.valueOf(typeName);
       
        idNode.getToken().setVarType(type);
        programBuilder.addVar(idNode.getToken().getValue(), type);
          if(programBuilder.isIsLocalContext()){
            localVarsCount++;
        }
    }
    
}
