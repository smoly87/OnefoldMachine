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
import grammar.GrammarInfo;
import grammar.GrammarInfoStorage;
import compiler.metadata.MetaClassesInfo;
import program.builder.ProgramBuilder;
import syntax.analyser.AstNode;
import types.TypesInfo;
import virtual.machine.VMCommands;
public class VarCompiler extends AstCompiler{

    protected int localVarsCount;

    public void setLocalVarsCount(int localVarsCount) {
        this.localVarsCount = localVarsCount;
    }
    protected TypesInfo typesInfo;

    
    public VarCompiler(ProgramBuilder programBuilder){
        super(programBuilder);
        typesInfo = TypesInfo.getInstance();
    }
    
    public int getLocalVarsCount() {
        return localVarsCount;
    }
    
    public void clearLocalVarsCount(){
        localVarsCount = 0;
    }
    
    @Override
    public void compileChild(AstNode node) {
      
    }

    
    
    @Override
    public void compileRootPost(AstNode node ) throws CompilerException {
        AstNode idNode = node.findChild("Id");
        AstNode typeNode = node.findChild("Type");  
        String varName = idNode.getToken().getValue();
        
        String typeName = typeNode.getToken().getValue();
        GrammarInfo grInfo = GrammarInfoStorage.getInstance();
        
        VarType type;
        boolean classFlag = false;
        
        if(grInfo.getTypesList().contains(typeName)){
            type = VarType.valueOf(typeName); 
        } else {
             MetaClassesInfo metaInfo = MetaClassesInfo.getInstance();
             if(!metaInfo.isClassExists(typeName)){
               throw new CompilerException(String.format("Variable %s declared with type class %s. But such class have not been declared before.", varName, typeName));
             }
             type = VarType.ClassPtr;
             classFlag = true;
        }
        

        idNode.getToken().setVarType(type);
       
        if(programBuilder.isIsLocalContext()){
            Token token = idNode.getToken();
            
            if(classFlag){
                programBuilder.addLocalVar(varName, typeName);
            } else {
                programBuilder.addLocalVar(varName, type);
            }
            
            int varInd =  programBuilder.getLocalVarCode(varName);
            String typeSize = Integer.toString(typesInfo.getTypeSize(token.getVarType()));
            programBuilder.addInstruction(VMCommands.Push, typeSize, VarType.Integer);
            programBuilder.addInstruction(VMCommands.Push, classFlag ? 1: 0, VarType.Integer);
            programBuilder.addInstruction(VMCommands.Var_Declare_Local_Def_value, Integer.toString(varInd), VarType.Integer);
           
            localVarsCount++;
        } else {
            if(classFlag){
                programBuilder.addVar(varName, typeName);
            } else {
                programBuilder.addVar(varName, type);
            }
             
        }
    }
    
}
