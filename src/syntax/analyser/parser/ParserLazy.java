/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser.parser;

import lexer.LexerResult;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;
import syntax.analyser.UnexpectedSymbolException;

/**
 *
 * @author Andrey
 */
public class ParserLazy extends Parser{

    protected String wrappedParserName;
    public ParserLazy(String wrappedParserName){
        this.wrappedParserName = wrappedParserName;
    }
    
    @Override
    public boolean parseLexerResult(LexerResult lexerResults) throws UnexpectedSymbolException, ParserException {
        return this.getParser(wrappedParserName).parseLexerResult(lexerResults);
    }
    
    @Override
    public AstNode getParseResult(){
        return this.getParser(wrappedParserName).getParseResult();
    }
}
