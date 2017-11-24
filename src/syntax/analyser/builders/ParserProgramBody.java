/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser.builders;

import java.util.Map;
import lexer.LexerResult;
import syntax.analyser.AstNode;
import syntax.analyser.builders.ParserStatementBuilder;
import syntax.analyser.Parser;
import syntax.analyser.UnexpectedSymbolException;
import syntax.analyser.parser.ParserAlternative;
import syntax.analyser.parser.ParserException;
import syntax.analyser.parser.ParserLazy;
import syntax.analyser.parser.ParserRepeated;

/**
 *
 * @author Andrey
 */
public class ParserProgramBody extends ParserStatementBuilder{
   
    @Override
    public Parser build() {
        super.build();
        this.add(this.getParser("Function"));

        this.add(this.getParser("Class"));
    
        return new ParserRepeated(this) ;
    }
    
      @Override
    public boolean parseLexerResult(LexerResult lexerResults) throws UnexpectedSymbolException, ParserException {
        for (Map.Entry<String, Parser> entry : parsersMap.entrySet()) {
            Parser parser = entry.getValue();
            if(parser.parse(lexerResults)){
                AstNode resultNode = parser.getParseResult();
                if(resultNode == null) return false;
                this.setParseResult(resultNode);
                return true;
            }
        }
        parserStopPos = lexerResults.getCurPos();
        if(lexerResults.hasNext()){
            throw new ParserException("Error while parsing src near: " + lexerResults.getLexerPosDescription());
        } 
        
        return false;
    }
    
     
}
