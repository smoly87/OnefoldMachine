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
public class FieldBuilder extends ParserChain implements ParserBuilder{

   
    
    public Parser build() {
        //Указать нужен ли результат парсера
        
       ParserAlternative AccessLevelParserAlt = new ParserAlternative();
      
       AccessLevelParserAlt.add(new ParserKeyword("Private"));
       AccessLevelParserAlt.add(new ParserKeyword("Protected"));
       AccessLevelParserAlt.add(new ParserKeyword("Public"));
       
       return this
            .add(AccessLevelParserAlt, "AccessLevel")
            .addTag("Id")
            .addKeyword(":")  
            .add(this.getParser("TypesList"), "Type");
            
            
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

