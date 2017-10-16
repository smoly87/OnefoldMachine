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


import common.Token;
import common.VarType;
import compiler.AstCompiler;
import compiler.AstCompiler;
import compiler.exception.CompilerException;
import program.builder.ProgramBuilder;
import syntax.analyser.AstNode;
import types.TypesInfo;
import virtual.machine.VMCommands;
public class VarCompiler extends AstCompiler{

    protected int localVarsCount;
    protected TypesInfo typesInfo;

    
    public VarCompiler(){
        typesInfo = TypesInfo.getInstance();
    }
    
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
    public void compileRootPost(AstNode node, ProgramBuilder programBuilder) throws CompilerException {
        AstNode idNode = node.getChildNodes().get(0);
        AstNode typeNode = node.getChildNodes().get(1);
        String typeName = typeNode.getToken().getValue();
        
        VarType type = VarType.valueOf(typeName);
       
        idNode.getToken().setVarType(type);
       
        if(programBuilder.isIsLocalContext()){
            Token token = idNode.getToken();
            String varName = idNode.getToken().getValue();
            programBuilder.addLocalVar(varName, type);
            int varInd =  programBuilder.getLocalVarCode(varName);
            String typeSize = Integer.toString(typesInfo.getTypeSize(token.getVarType()));
            programBuilder.addInstruction(VMCommands.Push, typeSize, VarType.Integer);
            programBuilder.addInstruction(VMCommands.Var_Declare_Local, Integer.toString(varInd), VarType.Integer);
           
            localVarsCount++;
        } else {
             programBuilder.addVar(idNode.getToken().getValue(), type);
        }
    }
    
}
