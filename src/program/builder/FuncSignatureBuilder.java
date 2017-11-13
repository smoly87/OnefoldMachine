/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.builder;

import common.VarType;

/**
 *
 * @author Andrey
 */
public class FuncSignatureBuilder {
    protected StringBuilder argsSignatureBuilder;
    public FuncSignatureBuilder() {
        this.argsSignatureBuilder = new StringBuilder();
    }
    
    
    public void addArgType(VarType type){
        argsSignatureBuilder.append(Integer.toString(type.ordinal()));
    }   
    
    public String getSignature(){
        return argsSignatureBuilder.toString();
    }
}
