/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package syntax.analyser.parser;

import common.Token;
import compiler.expr.MathExprCompiler;
import java.util.HashMap;
import java.util.HashSet;
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
public class ParserMathExpr extends ParserExprReversePolish{


    
    public ParserMathExpr(){
       super();
       allowedOperandTags.add("Id");
       allowedOperandTags.add("Integer");
       allowedOperandTags.add("Float"); 
    }
    
    protected void setPriorsTable(){
        priors.put("*", 1);
        priors.put("/", 1);
        
        priors.put("+", 2);
        priors.put("-", 2);
    }    
    

}
