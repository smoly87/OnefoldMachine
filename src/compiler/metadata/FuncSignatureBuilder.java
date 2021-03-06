/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.metadata;

import common.VarType;
import compiler.exception.CompilerException;
import program.builder.ProgramBuilder;
import syntax.analyser.AstNode;

/**
 *
 * @author Andrey
 */
public class FuncSignatureBuilder {
    protected StringBuilder argsSignatureBuilder;
     
    public FuncSignatureBuilder() {
        this.argsSignatureBuilder = new StringBuilder();
          //TODO: Figure out if it's possible to auto count such params

    }
    
    public void addArgFromNode(AstNode argNode, ProgramBuilder programBuilder) throws CompilerException{
        
           String argTypeStr = argNode.getToken().getTag().getName();
           switch(argTypeStr){
               case "Id":
                   VarDescription varDesc = programBuilder.getVarDescription(argNode.getToken().getValue());
                   this.addArgType(varDesc.getType());
                   break;
               default:
                   this.addArgType(VarType.valueOf(argTypeStr));
           }
       
    }
    
    public void addArgType(VarType type){
        argsSignatureBuilder.append(Integer.toString(type.ordinal()));
    }   
    
    public String getSignature(){
        return argsSignatureBuilder.toString();
    }
}
