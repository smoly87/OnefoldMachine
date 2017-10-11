/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser.builders;

import compiler.bytecode.LetCompiler;
import java.util.HashMap;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;
import syntax.analyser.parser.ParserChain;
import syntax.analyser.parser.ParserMathExpr;

/**
 *
 * @author Andrey
 */
public class FunctionBuilder extends  ParserChain implements ParserBuilder{
     public Parser build() {
        //Указать нужен ли результат парсера
       return this
            .addKeyword("Function")
            .addTag("Id")
            .addKeyword("(")
            .add(new ParserMathExpr(), "MathExpr")
            .addKeyword(")")
            .addKeyword("{") // Body of function
            .addKeyword("}");
            
    }
    @Override
    public  AstNode processChainResult(HashMap<String, AstNode> result){
        //Reorder operators by calculations
        AstNode rootNode = result.get("Let");
        rootNode.setCompiler(new LetCompiler());
        /*rootNode.setToken(token);
        //Think about gloabl agreement of naming
        rootNode.setName("Let");*/
        rootNode.addChildNode(result.get("MathExpr"));
        rootNode.addChildNode(result.get("Id"));
        
        return rootNode;
    }
}
