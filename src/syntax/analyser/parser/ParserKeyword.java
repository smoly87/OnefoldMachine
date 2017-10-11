/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package syntax.analyser.parser;

import common.Token;
import lexer.LexerResult;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;

/**
 *
 * @author Andrey
 */
public class ParserKeyword extends Parser{
    protected String keyword;
    public ParserKeyword(String keyword){
        this.keyword = keyword;
    }
    
   
    @Override
    public boolean parseLexerResult(LexerResult lexerResults) {
        Token token = lexerResults.getCurToken();
        String name = lexerResults.getCurToken().getName();
        System.out.println(name);
        if( name.equals(this.keyword) ) {
            AstNode resNode = new AstNode();
            
            resNode.setToken(token);
            setParseResult(resNode);
            lexerResults.next();
            return true;
        }
        return false;
    }
   
}
