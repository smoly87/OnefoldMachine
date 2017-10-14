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
public class FieldDescription extends ClassMemeberDescription{
    protected VarType fieldType;

    public VarType getFieldType() {
        return fieldType;
    }

    public void setFieldType(VarType fieldType) {
        this.fieldType = fieldType;
    }
}
