/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package compiler;

import compiler.exception.CompilerException;
import syntax.analyser.AstNode;
import program.builder.ProgramBuilder;

/**
 *
 * @author Andrey
 */
public abstract class AstCompiler {
    public void compileChild(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
    }
    
    public  void compileRootPost(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        
    }
    public  void compileRootPre(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        
    }
}
