/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.Collections;
import syntax.analyser.AstNode;

/**
 *
 * @author Andrey
 */
public abstract class TreeWalkerDST {
    public TreeWalkerDST(){
        
    }
    
    protected  abstract void callback(AstNode node, int level);
      
    public void walkTree(AstNode rootNode){
         callback(rootNode, 0);
         walk(rootNode, 0);
    }
    
    protected void walk(AstNode node, int level){
       
        if(node.hasChildNodes()){
            for(AstNode curNode : node.getChildNodes()){
                if(curNode == null) continue;
                callback(curNode, level  + 1);
                if(curNode.hasChildNodes()) walk(curNode, level + 2);
            }
        }
    }
}
