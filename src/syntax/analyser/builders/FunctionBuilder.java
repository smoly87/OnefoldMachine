/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser.builders;

import common.Tag;
import common.Token;
import common.VarType;
import compiler.expr.FunctionCompiler;
import compiler.expr.LetCompiler;
import java.util.HashMap;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;
import syntax.analyser.parser.ParserAlternative;
import syntax.analyser.parser.ParserChain;
import syntax.analyser.parser.ParserMathExpr;
import syntax.analyser.parser.ParserRepeated;
import syntax.analyser.parser.ParserStatement;
import syntax.analyser.parser.ParserTag;

/**
 *
 * @author Andrey
 */
public class FunctionBuilder extends  ParserChain implements ParserBuilder{
    
    protected Parser getVarBlockRepeatedParser(){
        return new ParserRepeated(this.getParser("Var"));
    }
    
    protected Parser getFunctionBodyParser(){
        return new ParserRepeated(new ParserStatement());
    }
    
    protected Parser getReturnStatementParser(){
        ParserAlternative altParser = new ParserAlternative();
        altParser.add(new ParserTag("Id"));
        altParser.add(new ParserTag("Integer"));
        altParser.add(new ParserTag("String"));
        altParser.add(new ParserTag("Float"));
        
        return altParser;
    }
    
    
    
    public Parser build() {
        //Указать нужен ли результат парсера
       return this
            .addKeyword("Function") 
            .addTag("Type")
            .addTag("Id")
            .addKeyword("(")
            .add(getVarBlockRepeatedParser(), "VarsBlock")
            .addKeyword(")")
            .addKeyword("{") // Body of function
            .add(getFunctionBodyParser(), "FunctionBody") 
            .addKeyword("Return") 
            .add(getReturnStatementParser(), "ReturnStatement")
            .addKeyword("}", "EndFunction");
            //.addKeyword(";");
            
    }
    @Override
    public  AstNode processChainResult(HashMap<String, AstNode> result){
        //Reorder operators by calculations
        AstNode rootNode = result
                          .get("Function")
                          .setCompiler(this.getCompiler("Function"))
                          .setName("FunctionHeader")
                          .addChildNode(result.get("Id"), "FunctionId");
        
        transformVarsNode(result.get("VarsBlock"), rootNode);
        
        rootNode.addChildNode(result.get("FunctionBody"), "FunctionBody")
                .addChildNode(result.get("ReturnStatement"), "ReturnStatement")
                .addChildNode(result.get("EndFunction"), "EndFunction");
        
        System.out.println("Function parser has been reached");
        return rootNode;
    }
    
    protected AstNode transformVarsNode(AstNode varsNode, AstNode rootNode){
      for(AstNode node : varsNode.getChildNodes()){
         AstNode idNode = node.getChildNodes().get(0);
         AstNode typeNode = node.getChildNodes().get(1);
         
         String typeName = typeNode.getToken().getValue();
         VarType type = VarType.valueOf(typeName);
         
         
         Token token  = new Token();
         token.setTag(new Tag("VarDescription"));
         token.setValue(idNode.getToken().getValue());
         token.setVarType(type);
         idNode.setToken(token);
         idNode.setName("VarDescription");
         
         rootNode.addChildNode(idNode, "VarDescription");
      }
      return rootNode; 
    }
}
