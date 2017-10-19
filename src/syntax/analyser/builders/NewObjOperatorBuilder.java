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
import syntax.analyser.parser.ParserTag;

/**
 *
 * @author Andrey
 */
public class NewObjOperatorBuilder extends ParserChain implements ParserBuilder{

   
    protected Parser getRightPartParser(){
        ParserAlternative altParser = new ParserAlternative();
        altParser.add(new ParserMathExpr());
        altParser.add(this.getParser("FunctionCall"));
        
        return altParser;
    }
    
    public Parser build() {
        //Указать нужен ли результат парсера
       return this
            
            .addKeyword("New")
            .addTag("Id");   
    }
    
    @Override
    public  AstNode processChainResult(HashMap<String, AstNode> result){

        AstNode rootNode = result.get("Let");
        rootNode.setCompiler(new LetCompiler());

        rootNode.addChildNode(result.get("RightPartExpr"));
        rootNode.addChildNode(result.get("Id"));
        System.out.println("Let has been reached");
        return rootNode;
    }
    
}
