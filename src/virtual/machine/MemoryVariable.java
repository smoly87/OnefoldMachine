/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine;

import common.VarType;

/**
 *
 * @author Andrey
 */
public class MemoryVariable {
    protected int addr;
    protected VarType varType;

    public MemoryVariable(int addr, VarType varType) {
        this.addr = addr;
        this.varType = varType;
    }

    public int getAddr() {
        return addr;
    }


    public VarType getVarType() {
        return varType;
    }

}
