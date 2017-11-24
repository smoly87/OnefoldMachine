/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package perfomance.mesuare;

import compiler.exception.CompilerException;
import lexer.LexerResult;
import main.FullPipeline;
import syntax.analyser.AstNode;
import syntax.analyser.parser.ParserException;
import virtual.machine.Program;

/**
 *
 * @author Andrey
 */
public class TaskCompile extends EstimateTask{

    public TaskCompile(String taskName, FullPipeline fullPipe) {
        super(taskName, fullPipe);
    }

    @Override
    public Object compute(Object prevStageRes) throws ParserException, CompilerException{
        AstNode ast = (AstNode) prevStageRes;
        return fullPipe.compile(ast);
    }
    
}
