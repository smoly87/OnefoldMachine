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
public class VarDescription {
    protected int code;
    protected int classId;

    
    protected VarType type;

    public VarDescription( VarType type, int code) {
        this.code = code;
        this.type = type;
    }

    
    
    public int getCode() {
        return code;
    }

    public VarType getType() {
        return type;
    }
    
    
}
