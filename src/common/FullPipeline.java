/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package common;

import compiler.TreeWalkerASTCompiler;
import compiler.exception.CompilerException;
import grammar.GrammarInfoStorage;
import grammar.GrammarInfo;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import lexer.Lexer;
import syntax.analyser.parser.ParserStatement;
import lexer.LexerResult;
import main.Main;
import program.builder.ProgramBuilder;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;
import syntax.analyser.UnexpectedSymbolException;
import syntax.analyser.parser.ParserException;
import syntax.analyser.parser.ParserMathExpr;
import syntax.analyser.parser.ParserProgramBody;
import utils.TreeWalkerDST;
import utils.TreeWalkerDSTPrint;
import virtual.machine.Program;
import virtual.machine.VMCommands;

/**
 *
 * @author Andrey
 */
//Это шаблон стратегия
public class FullPipeline {
    protected String stageDebugText;

    public void setStageDebugText(String stageDebugText) {
        this.stageDebugText = stageDebugText;
    }
    
    public LexerResult tokenise(String programSrc){
        Lexer lex = new Lexer( GrammarInfoStorage.getInstance());
        return lex.parse(programSrc);
    }
    
    public AstNode buildAst(LexerResult lexerResult) throws ParserException{   
        Parser rootParser = new ParserProgramBody();
        rootParser.parse(lexerResult);
        return rootParser.getParseResult();
    }
    
    public Program compile(AstNode astTree) throws CompilerException{
        TreeWalkerASTCompiler tw = new TreeWalkerASTCompiler();
        ProgramBuilder progBuilder = tw.walkTree(astTree);
        this.stageDebugText = progBuilder.getAsmText();
        TreeWalkerDST walker = new TreeWalkerDSTPrint();
        walker.walkTree(astTree);

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
    /*public AstNode buildMathAst(LexerResult lexerResult){
        try {
            ParserMathExpr rootParser = new ParserMathExpr();
            rootParser.parse(lexerResult);
            return rootParser.getParseResult();
        } catch(UnexpectedSymbolException e){
            System.out.println("Error:" + e.getMessage());
        }
        
        return null;
    }*/
    /*public compile(AstNode rootNode, String fileName){
    }*/
}
