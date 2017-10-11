/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package common;

/**
 *
 * @author Andrey
 */
public class Token {
    protected Tag tag;
    protected String name;
    protected String value;
    protected VarType varType;

    public VarType getVarType() {
        return varType;
    }

    public void setVarType(VarType varType) {
        this.varType = varType;
    }

    public String getValue() {
        return value;
    }
    
    public Token(String name, Tag tag, String value){
        this.name = name;
        this.tag = tag;
        this.value = value;

    }
    
    public Tag getTag() {
        return tag;
    }

    public String getName() {
        return name;
    }
    
    public String getTagName(){
        return tag.getName();
    }
    
}
