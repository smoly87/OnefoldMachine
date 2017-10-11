/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package compiler;

import syntax.analyser.AstNode;
import program.builder.ProgramBuilder;

/**
 *
 * @author Andrey
 */
public abstract class AstCompiler {
    public abstract void compileChild(AstNode node, ProgramBuilder programBuilder);
    
    public abstract void compileRoot(AstNode node, ProgramBuilder programBuilder);
    
}
