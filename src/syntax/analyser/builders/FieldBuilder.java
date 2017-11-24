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
      

       AccessLevelParserAlt.add(new ParserKeyword("Protected"));
       AccessLevelParserAlt.add(new ParserKeyword("Public"));
       
       return this
            .add(AccessLevelParserAlt, "AccessLevel")
            .addTag("Id")
            .addKeyword(":")  
            .add(this.getParser("TypesList"), "Type").addKeyword(";");
            
            
    }
    @Override
    public  AstNode processChainResult(HashMap<String, AstNode> result){
        //Composite result
        AstNode rootNode = new AstNode();
        
        rootNode.setName("FieldRoot");
                //.setCompiler(this.getCompiler("Field"));
               
        
        String fieldName = result.get("Id").getToken().getValue();
        String fieldTypeName = result.get("Type").getToken().getValue();
        
        Token token = new Token();
        
        AstNode resNode = new AstNode();
        resNode.setToken(token);
        
        if(grammarInfo.getTypesList().contains(fieldTypeName)){
           token.setVarType(VarType.valueOf(fieldTypeName));
        } else{
           token.setVarType(VarType.Pointer);
           AstNode classNode = new AstNode();
           Token classToken = new Token("FieldClassName", new Tag("Type"), fieldTypeName);
           classNode.setToken(classToken);
           resNode.addChildNode(classNode, "FieldClassName");
        }
        
        
        token.setValue(fieldName);
        
        token.setTag(new Tag("Field"));
        
        
        rootNode.addChildNode(resNode, "Field");

        
        return rootNode;
    }
    
}

