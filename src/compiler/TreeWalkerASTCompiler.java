/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import compiler.exception.CompilerException;
import java.util.logging.Level;
import java.util.logging.Logger;
import syntax.analyser.AstNode;
import virtual.machine.Instructions;
import program.builder.ProgramBuilder;

/**
 *
 * @author Andrey
 */
public class TreeWalkerASTCompiler  {

    protected ProgramBuilder programBuilder;
    
    public TreeWalkerASTCompiler() throws CompilerException{
        this.programBuilder =  ProgramBuilder.getInstance();
    }
    
    public ProgramBuilder walkTree(AstNode rootNode) throws CompilerException{
        
         walk(rootNode, rootNode.getCompiler());
         return programBuilder;
    }
    
    protected void walk(AstNode node, AstCompilerList compiler) throws CompilerException{
        
        try {
            if(node.getCompiler() != null){
                compiler = node.getCompiler();
            }
            
            
            if(compiler != null) compiler.compileRootPre(node);
            if(node.hasChildNodes()){
                for(AstNode curNode : node.getChildNodes()){
                    if(curNode == null) continue;
                    if(curNode.hasChildNodes()){
                        AstCompilerList compilerL = compiler;
                        if(curNode.getCompiler()!=null){
                            compilerL = curNode.getCompiler();
                        }
                        walk(curNode, compilerL);
                        
                    } else{
                        if(compiler != null)  compiler.compileChild(curNode);
                    }
                }
                if(compiler != null) compiler.compileRootPost(node);
                
            }
        } catch (CompilerException ex) {
           throw ex;
        }
    }
}
