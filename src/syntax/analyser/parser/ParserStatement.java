/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package syntax.analyser.parser;

import com.sun.xml.internal.ws.util.StringUtils;
import common.Token;
import java.util.HashMap;
import java.util.HashSet;
import lexer.LexerResult;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;
import syntax.analyser.UnexpectedSymbolException;

/**
 *
 * @author Andrey
 */
public class ParserStatement extends ParserRepeated{
     
    protected ParserAlternative possibleAlts; 
    protected void addPossibleStatements(){
         
         possibleAlts.add(this.getParser("Let"));
         possibleAlts.add(this.getParser("Var"));
    } 
    
    public ParserStatement(){

        possibleAlts = new ParserAlternative();
        this.addPossibleStatements();
        this.setParser(possibleAlts);
    }
   
     
}
