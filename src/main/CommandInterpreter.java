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

import lexer.LexerResult;
import program.builder.ProgramFileSys;
import syntax.analyser.AstNode;
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
    
    public boolean executeCommand(String commandText) throws ParserException, CompilerException, IOException, Exception{
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
        
        
        
        try {
            switch (command.getCommandName()) {
                case "compile":
                    
                    this.compileStage();
                    break;
                case "compile_run":
                    try {
                        prog = this.compileStage();
                    } catch (ParserException parserException) {
                        throw parserException;
                    } catch (CompilerException compilerException) {
                        throw compilerException;
                    }                    
                    this.runProgram(prog);
                    break;
                case "run":
                    prog = ProgramFileSys.load(command.getOption("path"));
                    this.runProgram(prog);
                    break;
            }
        } 
        catch (CompilerException|ParserException compilerException) {
          throw compilerException;
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
        FullPipeline fullPipe = new FullPipeline();
        String programSrc = fullPipe.getSrcTextProcessed(command.getOption("path_src"));
        this.addReport("show_src", "Program_Src", programSrc);
        LexerResult lexRes = fullPipe.tokenise(programSrc);
        AstNode ast = fullPipe.buildAst(lexRes);
        Program prog = fullPipe.compile(ast);
        return prog;
    }
    
    protected void runProgram(Program prog) throws VmExecutionExeption{
        VM virtMachine = new VM();
        virtMachine.run(prog);   
    }
    
    
    protected void addReport(String flagName, String key, String message){
        if(command.isOptionExists("debug") || command.isOptionExists(flagName)) {
        }
    }
}
