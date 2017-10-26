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
    protected String name ="";

    public String getName() {
        return name;
    }

    public AstNode setName(String name) {
        this.name = name;
        return this;
    }

    public AstCompiler getCompiler() {
        return compiler;
    }

    public AstNode setCompiler(AstCompiler compiler) {
        this.compiler = compiler;
        return this;
    }

    
    public AstNode(){
        this.childNodes = new ArrayList<>();
    }
    public AstNode addChildNode(AstNode childNode){
        childNodes.add(childNode);
           return this;
    }
    public AstNode addChildNode(AstNode childNode, String nodeName){
        childNode.setName(nodeName);
        childNodes.add(childNode);
           return this;
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
    public AstNode setToken(Token token) {
        this.token = token;
        return this;
    }
    
    public boolean hasChildNodes(){
        return childNodes.size() > 0;
    }
    
    public AstNode findChild(String nodeName){
        for(AstNode childNode: this.getChildNodes()){
            if(childNode.getName().equals(nodeName)) return childNode;
        }
        
        return null;
    }
}
