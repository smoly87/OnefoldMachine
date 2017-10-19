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
public class ParserOptional extends Parser{

    protected Parser parser;
    
    public ParserOptional(Parser parser){
        this.parser = parser;
    }
    
    @Override
    protected boolean parseLexerResult(LexerResult lexerResults) throws UnexpectedSymbolException, ParserException {
        parser.parse(lexerResults);
        return true;
    }
    
    public AstNode getParseResult(){
        return this.parser.getParseResult();
    }
    
}
