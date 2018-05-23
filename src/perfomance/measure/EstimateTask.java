/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package perfomance.measure;

import compiler.exception.CompilerException;
import main.FullPipeline;
import syntax.analyser.CompilationException;
import syntax.analyser.parser.ParserException;
import virtual.machine.exception.VmExecutionExeption;

/**
 *
 * @author Andrey
 */
public abstract class EstimateTask {
    protected String taskName;
    protected boolean once;
    protected FullPipeline fullPipe;

    public boolean isOnce() {
        return once;
    }

    public void setOnce(boolean once) {
        this.once = once;
    }

    public EstimateTask(String taskName, FullPipeline fullPipe) {
        this.taskName = taskName;
        this.fullPipe = fullPipe;
       
    }

    public String getTaskName() {
        return taskName;
    }
    
    public abstract Object compute(Object prevStageRes) throws CompilationException, CompilerException, VmExecutionExeption, ParserException;
}
