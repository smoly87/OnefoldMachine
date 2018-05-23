/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package perfomance.measure;

import compiler.exception.CompilerException;
import main.FullPipeline;
import syntax.analyser.parser.ParserException;
import virtual.machine.Program;
import virtual.machine.VM;
import virtual.machine.exception.VmExecutionExeption;

/**
 *
 * @author Andrey
 */
public class TaskExecution extends EstimateTask{
    protected VM virtualMachine;
    public TaskExecution(String taskName, FullPipeline fullPipe, VM virtualMachine) {
        super(taskName, fullPipe);
        this.virtualMachine = virtualMachine;
       
    }

    @Override
    public Object compute(Object prevStageRes) throws ParserException, CompilerException, VmExecutionExeption{
        Program prog = (Program) prevStageRes;
        virtualMachine.allocateProgram(prog);
        virtualMachine.run();
        return null;
    }
    
}
