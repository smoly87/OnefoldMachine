/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package perfomance.mesuare;

import lexer.LexerResult;
import main.FullPipeline;
import syntax.analyser.parser.ParserException;

/**
 *
 * @author Andrey
 */
public class TaskTokenise extends EstimateTask{

    public TaskTokenise(String taskName, FullPipeline fullPipe) {
        super(taskName, fullPipe);
    }

    @Override
    public Object compute(Object prevStageRes) throws ParserException{
        String programSrc = (String) prevStageRes;
        return fullPipe.tokenise(programSrc);
    }
    
}
