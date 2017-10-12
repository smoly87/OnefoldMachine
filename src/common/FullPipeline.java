/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package common;

import grammar.GrammarInfoStorage;
import grammar.GrammarInfo;
import lexer.Lexer;
import syntax.analyser.parser.ParserStatement;
import lexer.LexerResult;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;
import syntax.analyser.UnexpectedSymbolException;
import syntax.analyser.parser.ParserException;
import syntax.analyser.parser.ParserMathExpr;
import syntax.analyser.parser.ParserProgramBody;

/**
 *
 * @author Andrey
 */
//Это шаблон стратегия
public class FullPipeline {
    
    public LexerResult tokenise(String programSrc){
        Lexer lex = new Lexer( GrammarInfoStorage.getInstance());
        return lex.parse(programSrc);
    }
    
    public AstNode buildAst(LexerResult lexerResult){
        try {
            Parser rootParser = new ParserProgramBody();
            rootParser.parse(lexerResult);
            return rootParser.getParseResult();
        } catch(ParserException e){
            System.out.println("Error:" + e.getMessage());
        }
        
        return null;
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
