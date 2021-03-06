/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package syntax.analyser.builders;

import compiler.expr.LetCompiler;
import java.util.HashMap;
import java.util.LinkedList;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;
import syntax.analyser.parser.ParserAlternative;
import syntax.analyser.parser.ParserChain;
import syntax.analyser.parser.ParserKeyword;
import syntax.analyser.parser.ParserMathExpr;
import syntax.analyser.parser.ParserOptional;
import syntax.analyser.parser.ParserTag;

/**
 *
 * @author Andrey
 */
public class LetBuilder extends ParserChain implements ParserBuilder{

   
    protected Parser getRightPartObjField(){
        ParserChain chainParser = new ParserChain();
        
        chainParser.addTag("Id", "RightPartObjName")
                   .addKeyword(".")
                   .addTag("Id", "RightPartFieldName");
        
        return new ParserOptional(chainParser);
    }
    
    protected Parser getObjNameLeftParser(){
         ParserChain chainParser = new ParserChain();
        
        chainParser.addTag("Id", "LeftObjName")
                   .addKeyword(".");
        
        return new ParserOptional(chainParser);
    }
    
    protected Parser getRightPartParser(){
        ParserAlternative altParser = new ParserAlternative();
        altParser//.add(this.getParser("MathExpr"));
                 .add(new ParserMathExpr())
                 .add(new ParserTag("String"))
                 .add(new ParserTag("Boolean"))
                 .add(new ParserTag("Id"))
                 .add(new ParserTag("Null"))
                 .add(this.getParser("FunctionCall"), "FunctionCall")
                 .add(this.getParser("NewObjOperator"))
                 .add(this.getRightPartObjField());
        
        return altParser;
    }
    
    public Parser build() {
        //Указать нужен ли результат парсера
       return this
            .addKeyword("Let", "LetStart")
            .add(this.getObjNameLeftParser(), "LeftObjName")
            .addTag("Id", "LeftVarName")
            .addKeyword("=")
            .add(getRightPartParser(), "RightPartExpr")
            .addKeyword(";");
            
    }
    
    @Override
    public  AstNode processChainResult(HashMap<String, AstNode> result){

        AstNode rootNode = new AstNode();
       
        rootNode.addChildNode(result.get("LetStart"), "LetStart");
        AstNode rightExprNode = result.get("RightPartExpr");
        if(rightExprNode.getToken().getTagName().equals("Operator")){
            rightExprNode.addCompiler(this.getCompiler("Let"));
        }
        rootNode.addChildNode(rightExprNode, "RightPartExpr");
        
        if(result.get("LeftObjName") != null){
            AstNode leftObjNode = result.get("LeftObjName");
            leftObjNode.setCompiler(this.getCompiler("Let"));
            rootNode.addChildNode(leftObjNode, "LeftObjName");        
        } 
        
           
        rootNode.addChildNode(result.get("LeftVarName"), "LeftVarName");
        
         rootNode.setCompiler(this.getCompiler("Let"));
         
        return rootNode;
        
    }
    
}
