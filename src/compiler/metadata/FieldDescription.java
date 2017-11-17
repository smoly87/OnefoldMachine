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
public class FieldDescription extends ClassMemeberDescription{
    protected VarType fieldType;
    protected String fieldName;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    
    public VarType getFieldType() {
        return fieldType;
    }

    public void setFieldType(VarType fieldType) {
        this.fieldType = fieldType;
    }
    
    
}
