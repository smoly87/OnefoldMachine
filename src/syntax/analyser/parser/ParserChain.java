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
    protected String compilerName;
    public enum SET_COMPILER_MODE  {NONE, SET, ADD};
    //LinkedHashMap<String, Parser> parsersChain = new LinkedHashMap<>();
    public ParserChain(){
        parsersList = new LinkedHashMap<>();
    }
    
    public ParserChain addTag(String tag){
        parsersList.put(tag, new ParserTag(tag));
        return this;
    }
    
    public ParserChain addTag(String tag, String key){
        parsersList.put(key, new ParserTag(tag));
        return this;
    }
    
    public ParserChain addKeyword(String keyWord){
        parsersList.put(keyWord, new ParserKeyword(keyWord));
        return this;
    }
    
    public ParserChain addKeyword(String keyWord, String key){
        parsersList.put(key, new ParserKeyword(keyWord));
        return this;
    }
    
    public ParserChain add(Parser parser, String name){
        parsersList.put(name, parser);
        return this;
    }
    
      
    public  AstNode processChainResult(HashMap<String, AstNode> result){
         AstNode rootNode = new AstNode();
         for(Map.Entry<String, AstNode> entry: result.entrySet()){
             if(entry.getValue() != null){
                 rootNode.addChildNode(entry.getValue(), entry.getKey());
             }
             
         }
         
         return rootNode;
    }
    
    @Override
    public boolean parseLexerResult(LexerResult lexerResults) throws UnexpectedSymbolException, ParserException{
        LinkedHashMap<String, AstNode> resultMap = new LinkedHashMap<>();
        for(Map.Entry<String, Parser> entry: parsersList.entrySet()){
            Parser parser = entry.getValue();
            if(!parser.parse(lexerResults)){
                //System.err.println("Broken  " + entry.getKey() + " on token: " + lexerResults.getCurToken().getValue());
                parserStopPos = lexerResults.getCurPos();
                return false;
            } else {
                resultMap.put(entry.getKey(), parser.getParseResult());
            }
        }
        
        this.setParseResult(this.processChainResult(resultMap)); 
        //System.out.println("Chain success");
        return true;
    }
    
    
    protected AstNode addBlockIfExists(AstNode node, String blockName, HashMap<String, AstNode> result, SET_COMPILER_MODE mode, String compilerName){
        if(result.get(blockName) != null){
            AstNode resNode = result.get(blockName);
            if(compilerName != null){   
               switch(mode){
                  case SET:
                     resNode.setCompiler(this.getCompiler(compilerName));
                     break;
                  case ADD:
                     resNode.addCompiler(this.getCompiler(compilerName));
                     break;
              }
            } 
           node.addChildNode(resNode, blockName);
        }
        return node;
    }
}
