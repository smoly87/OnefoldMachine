/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main;


import compiler.TreeWalkerASTCompiler;
import compiler.exception.CompilerException;
import compiler.expr.ClassCompiler;
import compiler.expr.FunctionCompiler;
import grammar.GrammarInfoStorage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import lexer.Lexer;
import lexer.LexerResult;
import program.builder.ProgramBuilder;
import syntax.analyser.AstNode;
import syntax.analyser.CompilersFactory;
import syntax.analyser.Parser;
import syntax.analyser.ParserFactory;
import syntax.analyser.parser.ParserException;
import syntax.analyser.builders.ParserProgramBody;
import syntax.analyser.parser.IProgramBuildingSubscriber;
import utils.TreeWalkerDST;
import utils.TreeWalkerDSTPrint;
import virtual.machine.Program;
import virtual.machine.VMCommands;

/**
 *
 * @author Andrey
 */

public class FullPipeline implements IProgramBuildingSubscriber{
    protected String stageDebugText;
    protected ShellCommand command;

    
    public void setStageDebugText(String stageDebugText) {
        this.stageDebugText = stageDebugText;
    }

    public FullPipeline(ShellCommand command) {
        this.command = command;
    }
    
    protected ProgramLogger createLogger(String flagName, String header){
      
       return new ProgramLogger(getDebugOptionEnabled(flagName), header);
              
        
    }
    
    protected boolean getDebugOptionEnabled(String flagName){
       boolean enabled = false;  
       if(command.isOptionExists("debug") || command.isOptionExists(flagName)){
          enabled = true;
       }
       return enabled;
    }
    
    
    public LexerResult tokenise(String programSrc){
        Lexer lex = new Lexer(GrammarInfoStorage.getInstance());
        if(getDebugOptionEnabled("debug_show_lexer")){
            lex.addSubscriber(this);
        }
        return lex.parse(programSrc);
    }
    
   
    
    public AstNode buildAst(LexerResult lexerResult) throws ParserException{
       /* ParserFactory parserFactory = ParserFactory.getInstance();
        parserFactory.getElement("Class").addSubscriber(this);*/
        
        Parser rootParser = new ParserProgramBody().build();
        if(!rootParser.parse(lexerResult) && lexerResult.hasNext()){
            String nearTokens = lexerResult.getLexerPosDescription();
            throw new ParserException(String.format("Error at token %s", nearTokens));
        }
        return rootParser.getParseResult();
    }
    
    public Program compile(AstNode astTree) throws CompilerException{
        TreeWalkerASTCompiler tw = new TreeWalkerASTCompiler();
        
        //Two stage compilation
        CompilersFactory compilerFactory = CompilersFactory.getInstance();
        ClassCompiler classCompiler = (ClassCompiler)compilerFactory.getElement("Class");
        FunctionCompiler funcCompiler = (FunctionCompiler)compilerFactory.getElement("Function");
        
        //First stage
        compilerFactory.setEnabledAll(false);
        classCompiler.setEnabled(true);
        funcCompiler.setEnabled(true);
        tw.walkTree(astTree);
        
        //Second stage
        compilerFactory.setEnabledAll(true);
        classCompiler.setFirtStage(false);
        funcCompiler.setFirtStage(false);
        tw.walkTree(astTree);
        
        
        ProgramBuilder progBuilder = ProgramBuilder.getInstance();
        
        if(getDebugOptionEnabled("debug_ast")){
              TreeWalkerDST walker = new TreeWalkerDSTPrint(createLogger("debug_ast", "Abstract syntax tree"));
              walker.walkTree(astTree);
        }
      

        progBuilder.addInstruction(VMCommands.Halt);
        Program prog = progBuilder.getResult();
        return prog;
    }
    
    public String getSrcTextProcessed(String srcFile) throws FileNotFoundException{
        String path = Main.class.getResource("/assets/" + srcFile).getPath();
        Scanner scanner = new Scanner(new File(path), "UTF-8");
        String programSrc = scanner.useDelimiter("\\A").next();
        scanner.close();

        programSrc = programSrc.replace(System.getProperty("line.separator"), "");
        programSrc = programSrc.replace(" ", "");
        programSrc = programSrc.replaceAll("/\\*.*?\\*/", "");
        
        return programSrc;
    }

    @Override
    public void programEvent(Class<?> callerClass, String caption, String text) {
        System.err.println("Event fired: " + text);
    }

}
