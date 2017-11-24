/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import compiler.exception.CompilerException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lexer.LexerResult;
import perfomance.mesuare.EstimateResult;
import perfomance.mesuare.EstimatorChainedTasks;
import perfomance.mesuare.TaskBuildAst;
import perfomance.mesuare.TaskCompile;
import perfomance.mesuare.TaskExecution;
import perfomance.mesuare.TaskTokenise;
import program.builder.ProgramFileSys;
import syntax.analyser.AstNode;
import syntax.analyser.CompilationException;
import syntax.analyser.parser.ParserException;
import virtual.machine.Program;
import virtual.machine.VM;
import virtual.machine.exception.VmExecutionExeption;

/**
 *
 * @author Andrey
 */
public class CommandInterpreter {

   
    protected ShellCommand command;
    protected Program prog;
    protected String error;
    protected HashMap<String, CommandParam> commandsParams; 

    protected void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
    
    public CommandInterpreter() throws ConfigLoadException {
      
        try {
            commandsParams = CommandsParamsLoader.readConfig();
        } catch (IOException ex) {
            throw new ConfigLoadException("Config Error: commands.conf: " + ex.getMessage());
        }
    }
    
    public boolean isValidCommand(String commandText){
        command = new ShellCommand(commandText);
        
        if(!commandsParams.containsKey(command.getCommandName())){
             this.setError("Unknown command: " + command.getCommandName());
             return false;
        }
        
        CommandParam cmdParams = commandsParams.get(command.getCommandName());
        
        if(!cmdParams.checkMandatoryParams(command) || !cmdParams.checkValidAllParams(command) ){
            this.setError(cmdParams.getError());
            return false;
        }
        
        return true;
    }
    
    
    
    public boolean executeCommand(String commandText) throws  Exception{
        try {
            switch (command.getCommandName()) {
                case "compile":
                    this.compileStage();
                    break;
                case "compile_run": 
                    if(command.isOptionExists("estimate_compilation")){
                        estimateCompilationStages();
                        return true;
                    }
                    
                    if(command.isOptionExists("estimate_execution")){
                        estimateExecutionStages();
                        return true;
                    }
                    
                    
                    prog = this.compileStage();
                    if(prog != null) this.runProgram(prog);
                    break;
                case "run":
                    prog = ProgramFileSys.load(command.getOption("path"));
                    this.runProgram(prog);
                    break;
            }
        } 
        catch (CompilerException ex) {
            this.setError("Compilation error: " + ex.getMessage());
            return false;
        }
        catch(ParserException ex){
            this.setError("Parsing error: " + ex.getMessage());
            return false;
        } catch(VmExecutionExeption ex){
            this.setError("VM execution error: " + ex.getMessage());
            return false;
        }  
        return true;
    }
    
    
    protected Program compileStage() throws FileNotFoundException, ParserException, CompilerException, IOException{
     
        Program prog = this.compile();
        ProgramFileSys programFileSys = new ProgramFileSys();
        
        if(command.isOptionExists("path_dst")){
            programFileSys.save(prog, command.getOption("path_dst"));
        }
        
        return prog;
    }
    
    protected Program compile() throws FileNotFoundException, ParserException, CompilerException{
        FullPipeline fullPipe = new FullPipeline(command);
        String programSrc = fullPipe.getSrcTextProcessed(command.getOption("path_src"));
        this.addReport("show_src", "Program_Src", programSrc);
        LexerResult lexRes = fullPipe.tokenise(programSrc);
        AstNode ast = fullPipe.buildAst(lexRes);
        Program prog = fullPipe.compile(ast);
        return prog;
    }
    
    
    protected EstimatorChainedTasks getCompilationEstimator(int repeatTimes, boolean forExecution) throws FileNotFoundException, ParserException, CompilerException, CompilationException, VmExecutionExeption{
        FullPipeline fullPipe = new FullPipeline(command);
        String programSrc = fullPipe.getSrcTextProcessed(command.getOption("path_src"));
        
        EstimatorChainedTasks estimator = new EstimatorChainedTasks(programSrc, repeatTimes);
        estimator.add(new TaskTokenise("Tokenize", fullPipe), forExecution)
                 .add(new TaskBuildAst("Build AST", fullPipe), forExecution)
                 .add(new TaskCompile("Compilation", fullPipe), forExecution);
        
        return estimator;
    }
    
    protected void showEstimatorResults(HashMap<String, EstimateResult> res){
        for(Map.Entry<String, EstimateResult> entry : res.entrySet()){
            System.out.println(String.format("Average time of %s is %s", entry.getKey(), entry.getValue().getAverageTime()));
        }
    }
    
    protected void estimateCompilationStages()throws FileNotFoundException, ParserException, CompilerException, CompilationException, VmExecutionExeption{
        int repeatTimes =  Integer.valueOf(command.getOption("estimate_compilation"));
        EstimatorChainedTasks estimator = getCompilationEstimator(repeatTimes, false);
        HashMap<String, EstimateResult> res = estimator.getResults();
        showEstimatorResults(res);
    }
        
    protected void estimateExecutionStages()throws FileNotFoundException, ParserException, CompilerException, CompilationException, VmExecutionExeption{
        FullPipeline fullPipe = new FullPipeline(command);
        int repeatTimes =  Integer.valueOf(command.getOption("estimate_execution"));
        EstimatorChainedTasks estimator = getCompilationEstimator(repeatTimes, false);
        VM virtualMachine = new VM(command);
        estimator.add(new TaskExecution("Execution", fullPipe, virtualMachine));
        
        HashMap<String, EstimateResult> res = estimator.getResults();
        showEstimatorResults(res);    

    }
    
    protected void runProgram(Program prog) throws VmExecutionExeption{
        VM virtMachine = new VM(command);
        virtMachine.allocateProgram(prog);
        virtMachine.run();   
    }
    
    
    protected void addReport(String flagName, String key, String message){
        if(command.isOptionExists("debug") || command.isOptionExists(flagName)) {
        }
    }
}
