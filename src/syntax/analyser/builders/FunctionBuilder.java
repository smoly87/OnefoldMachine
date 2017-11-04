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
import grammar.GrammarInfo;
import grammar.GrammarInfoStorage;
import java.util.HashMap;
import java.util.HashSet;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;
import syntax.analyser.parser.ParserAlternative;
import syntax.analyser.parser.ParserChain;
import syntax.analyser.parser.ParserMathExpr;
import syntax.analyser.parser.ParserOptional;
import syntax.analyser.parser.ParserRepeated;
import syntax.analyser.parser.ParserTag;

/**
 *
 * @author Andrey
 */
public class FunctionBuilder extends  ParserChain implements ParserBuilder{
    
    protected Parser getVarBlockRepeatedParser(){
        return new ParserOptional( new ParserRepeated(this.getParser("Var")));
    }
    
    protected Parser getFunctionBodyParser(){
        return new ParserRepeated(getParser("ParserStatement"));
    }
    
    protected Parser getReturnStatementParser(){
        ParserAlternative altParser = new ParserAlternative();
        altParser.add(this.getParser("TypesList"))
                 .add(new ParserTag("Id"));
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
            .addKeyword("Return").add(getReturnStatementParser(), "ReturnStatement").addKeyword(";")
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
        
        AstNode thisNode = new AstNode();
        thisNode = processVarDescriptionNode(thisNode, "this", VarType.Integer);
        rootNode.addChildNode(thisNode, "VarDescription");
        
        transformVarsNode(result.get("VarsBlock"), rootNode);
        
        rootNode.addChildNode(result.get("FunctionBody"), "FunctionBody")
                .addChildNode(result.get("ReturnStatement"), "ReturnStatement")
                .addChildNode(result.get("EndFunction"), "EndFunction");
        
        System.out.println("Function parser has been reached");
        return rootNode;
    }
    
  
    
    protected AstNode processVarDescriptionNode(AstNode idNode, String varName, VarType type){
         Token token  = new Token();
         token.setTag(new Tag("VarDescription"));
         token.setValue(varName);
         token.setVarType(type);
         idNode.setToken(token);
         idNode.setName("VarDescription");
         
         return idNode;
    }
    
    protected AstNode transformVarsNode(AstNode varsNode, AstNode rootNode){
      GrammarInfo gs = GrammarInfoStorage.getInstance();
      HashSet<String> typesSet = gs.getTypesList();  
        
      for(AstNode node : varsNode.getChildNodes()){
         AstNode idNode = node.getChildNodes().get(0);
         AstNode typeNode = node.getChildNodes().get(1);
         
         String typeName = typeNode.getToken().getValue();
        
         VarType type = null;
         if(typesSet.contains(typeName) ){
             type = VarType.valueOf(typeName);
         } else{
             type = VarType.ClassPtr;
         }
         
         
         idNode = processVarDescriptionNode(idNode, idNode.getToken().getValue(), type);
         
        /* Token token  = new Token();
         token.setTag(new Tag("VarDescription"));
         token.setValue(idNode.getToken().getValue());
         token.setVarType(type);
         idNode.setToken(token);
         idNode.setName("VarDescription");*/
         
         rootNode.addChildNode(idNode, "VarDescription");
      }
      return rootNode; 
    }
}
