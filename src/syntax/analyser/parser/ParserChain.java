/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package syntax.analyser.parser;

import java.security.KeyStore;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import lexer.LexerResult;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;
import syntax.analyser.UnexpectedSymbolException;
import syntax.analyser.builders.ParserBuilder;

/**
 *
 * @author Andrey
 */
public  class ParserChain extends Parser{
    protected LinkedHashMap<String, Parser> parsersList;
    //LinkedHashMap<String, Parser> parsersChain = new LinkedHashMap<>();
    public ParserChain(){
        parsersList = new LinkedHashMap<>();
    }
    
    public ParserChain addTag(String tag){
        parsersList.put(tag, new ParserTag(tag));
        return this;
    }
    
    public ParserChain addKeyword(String keyWord){
        parsersList.put(keyWord, new ParserKeyword(keyWord));
        return this;
    }
    public ParserChain add(Parser parser, String name){
        parsersList.put(name, parser);
        return this;
    }
    
      
    public  AstNode processChainResult(HashMap<String, AstNode> result){
         AstNode rootNode = new AstNode();
         for(Map.Entry<String, AstNode> entry: result.entrySet()){
             rootNode.addChildNode(entry.getValue());
         }
         
         return rootNode;
    }
    
    @Override
    public boolean parseLexerResult(LexerResult lexerResults) throws UnexpectedSymbolException, ParserException{
        HashMap<String, AstNode> resultMap = new HashMap<>();
        for(Map.Entry<String, Parser> entry: parsersList.entrySet()){
            Parser parser = entry.getValue();
            if(!parser.parse(lexerResults)){
                //System.out.println("Broken on " + entry.getKey());
                return false;
            } else {
                resultMap.put(entry.getKey(), parser.getParseResult());
            }
        }
        
        this.setParseResult(this.processChainResult(resultMap)); 
        //System.out.println("Chain success");
        return true;
    }
    
}
