/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser.builders;

import compiler.expr.LetCompiler;
import java.util.HashMap;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;
import syntax.analyser.parser.ParserAlternative;

import syntax.analyser.parser.ParserChain;
import syntax.analyser.parser.ParserKeyword;
import syntax.analyser.parser.ParserMathExpr;
import compiler.expr.VarCompiler;
/**
 *
 * @author Andrey
 */
public class VarBuilder extends ParserChain implements ParserBuilder{

   
    
    public Parser build() {
        //Указать нужен ли результат парсера
        
       ParserAlternative typesParserAlt = new ParserAlternative();
      
       typesParserAlt.add(new ParserKeyword("Integer"));
       typesParserAlt.add(new ParserKeyword("Boolean"));
       
       return this
            .addKeyword("Var")
            .addTag("Id")
            .addKeyword(":")
            .add(typesParserAlt, "Type")
            .addKeyword(";");
            
    }
    @Override
    public  AstNode processChainResult(HashMap<String, AstNode> result){
        //Reorder operators by calculations
        AstNode rootNode = new AstNode();
        rootNode.setCompiler(this.getCompiler("Var"));
        //Think about gloabl agreement of naming
        rootNode.setName("Var");
        rootNode.addChildNode(result.get("Id"));
        rootNode.addChildNode(result.get("Type"));
        
        return rootNode;
    }
    
}

