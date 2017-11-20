/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package syntax.analyser.parser;

import common.Token;
import compiler.expr.MathExprComplier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;
import javax.xml.bind.annotation.XmlElement;
import lexer.LexerResult;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;

/**
 *
 * @author Andrey
 */
public class ParserLogExpr extends ParserExprReversePolish{

    public ParserLogExpr() {
       super();
       allowedOperandTags.add("Id");
       allowedOperandTags.add("Boolean");
    }


    
    protected void setPriorsTable(){
        priors.put("*", 1);
        priors.put("/", 1);
        
        priors.put("+", 2);
        priors.put("-", 2);
    }
    
   
    
}
