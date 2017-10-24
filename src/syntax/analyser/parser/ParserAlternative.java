/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser.parser;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import lexer.LexerResult;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;
import syntax.analyser.UnexpectedSymbolException;

/**
 *
 * @author Andrey
 */
public class ParserAlternative extends Parser{

    protected LinkedHashMap<String, Parser> parsersMap;
    protected String resultParserName;

    public String getResultParserName() {
        return resultParserName;
    }
    
    public ParserAlternative(){
        parsersMap = new LinkedHashMap<>();
    }
    
    @Override
    protected boolean parseLexerResult(LexerResult lexerResults) throws UnexpectedSymbolException, ParserException {
        for (Map.Entry<String, Parser> entry : parsersMap.entrySet()) {
            Parser parser = entry.getValue();
            if(parser.parse(lexerResults)){
                AstNode resultNode = parser.getParseResult();
               // resultNode.setName(entry.getKey());
                this.setParseResult(resultNode);
                return true;
            }
        }
        return false;
    }
    
    public ParserAlternative add(Parser parser ){
        parsersMap.put(Integer.toString(parsersMap.size()), parser);
        return this;
    }
    
    public ParserAlternative add(Parser parser, String parserName){
        parser.setParserName(parserName);
        parsersMap.put(parserName, parser);
        return this;
    }
}
