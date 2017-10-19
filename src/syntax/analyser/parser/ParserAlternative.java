/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser.parser;

import java.util.LinkedList;
import lexer.LexerResult;
import syntax.analyser.Parser;
import syntax.analyser.UnexpectedSymbolException;

/**
 *
 * @author Andrey
 */
public class ParserAlternative extends Parser{

    protected LinkedList<Parser> parsers;
    
    public ParserAlternative(){
        parsers = new LinkedList<>();
    }
    
    @Override
    protected boolean parseLexerResult(LexerResult lexerResults) throws UnexpectedSymbolException, ParserException {
        for (Parser parser : parsers) {
            if(parser.parse(lexerResults)){
                this.setParseResult(parser.getParseResult());
                return true;
            }
        }
        return false;
    }
    
    public ParserAlternative add(Parser parser ){
        parsers.add(parser);
        return this;
    }
    
}
