/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package syntax.analyser.builders;

import compiler.expr.LetCompiler;
import compiler.expr.NewObjOperatorCompiler;
import java.util.HashMap;
import java.util.LinkedList;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;
import syntax.analyser.parser.ParserAlternative;
import syntax.analyser.parser.ParserChain;
import syntax.analyser.parser.ParserKeyword;
import syntax.analyser.parser.ParserMathExpr;
import syntax.analyser.parser.ParserTag;

/**
 *
 * @author Andrey
 */
public class NewObjOperatorBuilder extends ParserChain implements ParserBuilder{
    
    public Parser build() {
       return this
            
            .addKeyword("New")
            .addTag("Id")
            .addKeyword("(").addKeyword(")");   
    }
    
    @Override
    public  AstNode processChainResult(HashMap<String, AstNode> result){

        AstNode rootNode = result.get("New");
        rootNode.setCompiler(this.getCompiler("NewObjOperator"));

        
        rootNode.addChildNode(result.get("Id"), "ClassName");
        System.out.println("New obj has been reached");
        return rootNode;
    }
    
}
