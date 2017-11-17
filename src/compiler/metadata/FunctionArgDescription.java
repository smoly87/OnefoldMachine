/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.metadata;

import common.VarType;

/**
 *
 * @author Andrey
 */
public class FunctionArgDescription {
    protected String name;
    protected Integer code;
    protected VarType varType;

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }

    public VarType getVarType() {
        return varType;
    }

    public FunctionArgDescription(String name, Integer code, VarType varType) {
        this.name = name;
        this.code = code;
        this.varType = varType;
    }
}
