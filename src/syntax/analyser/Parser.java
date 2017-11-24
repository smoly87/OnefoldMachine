/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package syntax.analyser;

import common.Token;
import common.TokenInfo;
import compiler.AstCompiler;
import grammar.GrammarInfo;
import grammar.GrammarInfoStorage;
import grammar.GrammarPart;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import lexer.Lexer;
import lexer.LexerResult;
import syntax.analyser.parser.ParserException;
import syntax.analyser.parser.Subscribeable;
import utils.Pair;
import syntax.analyser.parser.IParserSubscriber;
import syntax.analyser.parser.ProgramBuildingStage;

/**
 *
 * @author Andrey
 */
public abstract class Parser extends ProgramBuildingStage{
    AstNode parseResult;
    protected  ParserFactory parsersStorage;
    protected GrammarInfo grammarInfo;
    protected String parserName;
    protected CompilersFactory compilersFactory;
    protected int parserStopPos;


    public int getParserStopPos() {
        return parserStopPos;
    }
    
    
    
    protected AstCompiler getCompiler(String compilerName){
        return compilersFactory.getElement(compilerName);
    }
    
    public Parser(){
       
        grammarInfo = GrammarInfoStorage.getInstance();
        compilersFactory = CompilersFactory.getInstance();
        parsersStorage = ParserFactory.getInstance();
    }
    
    protected Parser getParser(String name) {
       try{ 
         return parsersStorage.getElement(name);
       } catch(Exception e){
           System.err.printf("Cannot build parser with name: %s wit error: %s", name, e.getMessage() );
       } 
       
       return null;
    }
    
    protected boolean isAllowedToken(Token token){
       LinkedHashMap<String, GrammarPart> map = grammarInfo.getFullInfo();
       if(!map.containsKey(this.getParserName())){
           return false;
       } else{
           GrammarPart gPart = map.get(this.getParserName());
           LinkedHashMap<String, ArrayList<TokenInfo>> tokensInfo = gPart.getFullInfo();
           return tokensInfo.containsKey(token.getName());
       }
    }
    
       
    public boolean parse(LexerResult lexerResults) throws UnexpectedSymbolException, ParserException{
        //If parsing is failed, we should restore LexerResult to pass it to other parser in chain.
        int pos = lexerResults.getCurPos();
        parseResult = null;
        boolean res = this.parseLexerResult(lexerResults);
        if(!res){
            parserStopPos = lexerResults.getCurPos();
            lexerResults.setCurPos(pos);
        }
        
        if(hasSubscribers) this.callSubscribers("PARSER_REACHED", String.format("Parser %s has been reached", this.getClass().getCanonicalName()));
        
        return res;
    }

    public abstract boolean parseLexerResult(LexerResult lexerResults) throws UnexpectedSymbolException,  ParserException;
    
    
    protected void setParseResult(AstNode res){
        this.parseResult = res;
    }
    
    public AstNode getParseResult(){
        return this.parseResult;
    }

    /**
     * @return the parserName
     */
    public String getParserName() {
        return parserName;
    }

    /**
     * @param parserName the parserName to set
     */
    public void setParserName(String parserName) {
        this.parserName = parserName;
    }
}
