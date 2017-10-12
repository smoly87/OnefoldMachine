/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import syntax.analyser.AstNode;
import virtual.machine.Instructions;
import program.builder.ProgramBuilder;

/**
 *
 * @author Andrey
 */
public class TreeWalkerASTCompiler  {

    protected ProgramBuilder programBuilder;
    
    public TreeWalkerASTCompiler(){
        this.programBuilder = new ProgramBuilder();
    }
    
    public ProgramBuilder walkTree(AstNode rootNode){
        
         walk(rootNode, rootNode.getCompiler());
         return programBuilder;
    }
    protected void walk(AstNode node, AstCompiler compiler){
        
        if(node.getCompiler() != null){
            compiler = node.getCompiler();
        }
        
        if(node.getToken()!= null && node.getToken().getName() == "Function"){
            programBuilder.setIsLocalContext(true);
        }
        
        if(node.getToken()!= null && node.getToken().getName() == "EndFunction"){
            programBuilder.setIsLocalContext(false);
        }
        if(compiler != null) compiler.compileRootPre(node, programBuilder);
        if(node.hasChildNodes()){
            for(AstNode curNode : node.getChildNodes()){
                if(curNode.hasChildNodes()){
                    AstCompiler compilerL = compiler;
                    if(curNode.getCompiler()!=null){
                        compilerL = curNode.getCompiler();
                    }
                    walk(curNode, compilerL);
                    
                } else{
                    if(compiler != null)  compiler.compileChild(curNode, programBuilder);
                }
            }
           if(compiler != null) compiler.compileRootPost(node, programBuilder);
           
        }
    }
}
