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
public class IfBuilder extends  ParserChain implements ParserBuilder{
    
   
    protected Parser getElseParser(){
        ParserChain elseParser = new ParserChain();
        elseParser.addKeyword("Else")
                  .addKeyword("{", "StartElseExpr")   
                  .add(this.getParser("ParserStatementRepeated"), "StatementElse")
                  .addKeyword("}", "EndElse"); ;
        
        return new ParserOptional(elseParser);
    }
    
    public Parser build() {
        //Указать нужен ли результат парсера
       return this
            .addKeyword("If") 
            .add(this.getParser("LogicExprElem"), "LogExpr")
            .addKeyword("{", "StartExpr")   
            .add(this.getParser("ParserStatementRepeated"), "Statement")
            .addKeyword("}", "End")
            .add(getElseParser(), "ElseBlock");
            //.addKeyword(";");
            
    }
    
    @Override
    public  AstNode processChainResult(HashMap<String, AstNode> result){
        //Reorder operators by calculations
        AstNode rootNode = result.get("If");
        rootNode.setCompiler(this.getCompiler("If"));
                
        rootNode.addChildNode(new AstNode(), "StartIf");
        rootNode.addChildNode(result.get("LogExpr"), "LogExpr");
        rootNode.addChildNode(result.get("StartExpr"), "StartBody");
        
        
        rootNode.addChildNode(result.get("Statement"), "Statement");
        rootNode.addChildNode(result.get("End"), "End");
        
         if(result.get("ElseBlock") != null){
              rootNode.addChildNode(result.get("ElseBlock"), "ElseBlock");
         }
     
        
        System.out.println("While parser has been reached");
        
        return rootNode;
    }
    
    
}
