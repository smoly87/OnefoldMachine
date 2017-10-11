/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package syntax.analyser.parser;

import common.Tag;
import common.Token;
import lexer.LexerResult;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;

/**
 *
 * @author Andrey
 */
public class ParserTag extends Parser{
    
    protected String keyword;
    protected String tagName;
    
    public ParserTag(String tagName){
        this.tagName = this.keyword = tagName;
        
    }
    public ParserTag(String keyword, String tagName){
        this.tagName = tagName;
        this.keyword = keyword;
    }
    @Override
    public boolean parseLexerResult(LexerResult lexerResults) {
        Token token = lexerResults.getCurToken();
        //System.out.println(token.getTagName());
        if(token.getName().equals(this.keyword)   || token.getTagName().equals(tagName)) {
            AstNode resNode = new AstNode();
            
            resNode.setToken(token);
            setParseResult(resNode);
            
            lexerResults.next();
            return true;
        }
        return false;
    }
}
