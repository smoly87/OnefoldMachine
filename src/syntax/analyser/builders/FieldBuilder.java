/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser.builders;

import common.Tag;
import common.Token;
import common.VarType;
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
        //Composite result
        AstNode rootNode = new AstNode();
        
        rootNode.setName("FieldRoot")
                .setCompiler(this.getCompiler("Field"));
               
        
        String fieldName = result.get("Id").getToken().getValue();
        String fieldTypeName = result.get("Type").getToken().getValue();
        
        Token token = new Token();
        token.setVarType(VarType.valueOf(fieldTypeName));
        token.setValue(fieldName);
        token.setTag(new Tag("Field"));
        
        AstNode resNode = new AstNode();
        resNode.setToken(token);
        rootNode.addChildNode(resNode, "Field");

        
        return rootNode;
    }
    
}

