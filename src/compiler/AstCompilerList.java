/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import compiler.exception.CompilerException;
import java.util.LinkedList;
import program.builder.ProgramBuilder;
import syntax.analyser.AstNode;

/**
 *
 * @author Andrey
 */
public class AstCompilerList extends AstCompiler{
    protected LinkedList<AstCompiler> compilersList;
    
    public AstCompilerList(){
        super(null);
        compilersList = new LinkedList<>();
    }
    
    public void addCompiler(AstCompiler compiler){
        compilersList.add(compiler);
    }
    
    @Override    
    public void compileChild(AstNode node) throws CompilerException{
        for(AstCompiler compiler : compilersList){
            compiler.compileChild(node);
        }
    }
    
    @Override  
    public  void compileRootPost(AstNode node ) throws CompilerException{
       for(AstCompiler compiler : compilersList){
            compiler.compileRootPost(node);
       }
        
    }
    
    @Override  
    public  void compileRootPre(AstNode node) throws CompilerException{
        for(AstCompiler compiler : compilersList){
            compiler.compileRootPre(node);
       }
    }
}
