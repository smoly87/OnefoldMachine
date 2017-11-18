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
public class ParserRepeated extends Parser{
    protected Parser parser;

    protected void setParser(Parser parser) {
        this.parser = parser;
    }
    
    public ParserRepeated (Parser parser){
        this.parser = parser;
    }
    
    public  ParserRepeated(){
    }
    
    @Override
    public boolean parseLexerResult(LexerResult lexerResults) throws UnexpectedSymbolException, ParserException {
        AstNode rootNode = new AstNode();
        //How should it been marked ?
        boolean flag = false;
        
        while (parser.parse(lexerResults)){
            rootNode.addChildNode(parser.getParseResult());
            flag = true;
        }
        
        if(flag){
            this.setParseResult(rootNode);
        } else{
            parserStopPos = lexerResults.getCurPos();
        }
        
        return flag;

    }
    
}
