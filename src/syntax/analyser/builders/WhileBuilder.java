/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser.builders;

import compiler.expr.FunctionCallCompiler;
import compiler.expr.FunctionCompiler;
import compiler.expr.LetCompiler;
import java.util.HashMap;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;
import syntax.analyser.parser.ParserAlternative;
import syntax.analyser.parser.ParserChain;
import syntax.analyser.parser.ParserKeyword;
import syntax.analyser.parser.ParserMathExpr;
import syntax.analyser.parser.ParserOptional;
import syntax.analyser.parser.ParserRepeated;
import syntax.analyser.parser.ParserTag;

/**
 *
 * @author Andrey
 */
public class WhileBuilder extends  ParserChain implements ParserBuilder{
    
   
    
    public Parser build() {
        //Указать нужен ли результат парсера
       return this
            .addKeyword("While") 
            .add(this.getParser("LogicExprElem"), "LogExpr")
            .addKeyword("{", "StartExpr")   
            .add(this.getParser("ParserStatementRepeated"), "Statement")
            .addKeyword("}", "End");
            //.addKeyword(";");
            
    }
    
    @Override
    public  AstNode processChainResult(HashMap<String, AstNode> result){
        //Reorder operators by calculations
        AstNode rootNode = result.get("While");
        
        rootNode.addChildNode(new AstNode(), "StartCycle");
        rootNode.addChildNode(result.get("LogExpr"), "LogExpr");
        rootNode.addChildNode(result.get("StartExpr"), "Start");
        
        
        rootNode.addChildNode(result.get("Statement"), "Statement");
        rootNode.addChildNode(result.get("End"), "End");
     
        rootNode.setCompiler(this.getCompiler("While"));
        System.out.println("While parser has been reached");
        
        return rootNode;
    }
}
