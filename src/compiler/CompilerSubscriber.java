/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import compiler.exception.CompilerException;
import program.builder.ProgramBuilder;
import syntax.analyser.AstNode;

/**
 *
 * @author Andrey
 */
public interface CompilerSubscriber {
    public void compileChild(AstNode node, ProgramBuilder programBuilder)  throws CompilerException;
}
