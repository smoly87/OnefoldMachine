/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.expr.utils;

import syntax.analyser.AstNode;

/**
 *
 * @author Andrey
 */
public class LetCompilerParams {
    protected boolean objLeftPart =false;
    protected String leftObjName = "";
    protected String className = "";
    protected String varName = "";
    protected AstNode rightPartNode = null;
    protected boolean rightPartIsNull = false;
    
    public boolean isObjLeftPart() {
        return objLeftPart;
    }

    public void setIsObjLeftPart(boolean value) {
        this.objLeftPart = value;
    }

    public String getLeftObjName() {
        return leftObjName;
    }

    public void setLeftObjName(String leftObjName) {
        this.leftObjName = leftObjName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public AstNode getRightPartNode() {
        return rightPartNode;
    }

    public void setRightPartNode(AstNode rightPartNode) {
        this.rightPartNode = rightPartNode;
    }

    public boolean getRightPartIsNull() {
        return rightPartIsNull;
    }

    public void setRightPartIsNull() {
        this.rightPartIsNull = true;
    }
   
}
