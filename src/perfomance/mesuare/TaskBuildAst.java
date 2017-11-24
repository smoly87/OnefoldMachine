/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package perfomance.mesuare;

import lexer.LexerResult;
import main.FullPipeline;
import syntax.analyser.AstNode;
import syntax.analyser.parser.ParserException;

/**
 *
 * @author Andrey
 */
public class TaskBuildAst extends EstimateTask{

    public TaskBuildAst(String taskName, FullPipeline fullPipe) {
        super(taskName, fullPipe);
    }

    @Override
    public Object compute(Object prevStageRes) throws ParserException{
        LexerResult lexRes = (LexerResult) prevStageRes;
        AstNode ast = fullPipe.buildAst(lexRes);
        return ast;
    }
    
}
