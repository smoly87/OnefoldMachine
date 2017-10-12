/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package syntax.analyser;

import common.Token;
import compiler.AstCompiler;
import java.util.ArrayList;

/**
 *
 * @author Andrey
 */
public class AstNode {
    protected ArrayList<AstNode> childNodes;
    protected Token token;
    protected AstCompiler compiler;
    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AstCompiler getCompiler() {
        return compiler;
    }

    public void setCompiler(AstCompiler compiler) {
        this.compiler = compiler;
    }

    
    public AstNode(){
        this.childNodes = new ArrayList<>();
    }
    public void addChildNode(AstNode childNode){
        childNodes.add(childNode);
    }
    public void addChildNode(AstNode childNode, String nodeName){
        childNode.setName(nodeName);
        childNodes.add(childNode);
    }
    public ArrayList<AstNode> getChildNodes() {
        return childNodes;
    }

    public Token getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(Token token) {
        this.token = token;
    }
    
    public boolean hasChildNodes(){
        return childNodes.size() > 0;
    }
}
