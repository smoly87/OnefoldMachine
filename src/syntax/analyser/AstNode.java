/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package syntax.analyser;

import common.Token;
import compiler.AstCompiler;
import compiler.AstCompilerList;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author Andrey
 */
public class AstNode {
    protected ArrayList<AstNode> childNodes;
    protected AstCompilerList compilersList;
    
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

    public AstCompilerList getCompiler() {
        return compilersList;
    }

    public AstNode setCompiler(AstCompiler compiler) {
        //this.compiler = compiler;
        this.compilersList.addCompiler(compiler);
        return this;
    }
    
    public AstNode addCompiler(AstCompiler compiler) {
        //this.compiler = compiler;
        this.compilersList.addCompiler(compiler);
        if(this.getChildNodes().size() > 0){
            for(AstNode childNode : this.getChildNodes()){
                childNode.addCompiler(compiler);
            }
        }
        return this;
    }
     

    
    public AstNode(){
        this.childNodes = new ArrayList<>();
        this.compilersList = new AstCompilerList();
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
    
    public LinkedList<AstNode> findChilds(String nodeName){
        LinkedList<AstNode> res = new LinkedList<>();
        for(AstNode childNode: this.getChildNodes()){
            if(childNode.getName().equals(nodeName)) res.add(childNode) ;
        }
        
        return res;
    }
}
