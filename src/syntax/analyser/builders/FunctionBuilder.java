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
    
    protected String compilerName = "Function";
    
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
            .addKeyword("{", "StartFunctionBody") // Body of function
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
                          
                          .setName("FunctionHeader")
                          .addChildNode(result.get("Id"), "FunctionId")
                
                          .addChildNode(addAutoDecalared( "__ReturnAddress"), "VarDescription")
                          .addChildNode(addAutoDecalared( "__FrameStackRegister"), "VarDescription")
                          .addChildNode(addAutoDecalared( "__FrameTableStart"), "VarDescription")
                          .addChildNode(addAutoDecalared( "this"), "VarDescription");
        transformVarsNode(result.get("VarsBlock"), rootNode);    
        
                          //.addChildNode(varsNode, "VarsBlock")
                  rootNode.addChildNode(result.get("StartFunctionBody"), "StartFunctionBody")
                          .addChildNode(result.get("FunctionBody"), "FunctionBody")
                          .addChildNode(result.get("ReturnStatement"), "ReturnStatement")
                          .addChildNode(result.get("EndFunction"), "EndFunction")
                          .setCompiler(this.getCompiler(this.compilerName));
        System.out.println("Function parser has been reached");
        return rootNode;
    }
    
    protected AstNode addAutoDecalared( String varName){
        AstNode node = new AstNode();
        
        /*AstNode idNode = new AstNode();
        idNode.setToken(new Token("Id", new Tag("Id"), varName));
        
        AstNode typeNode = new AstNode();
        typeNode.setToken(new Token("Type", new Tag("Type"), "Integer"));
        
        node.setCompiler(this.getCompiler("Var"));
        
        node.addChildNode(idNode, "Id");
        node.addChildNode(typeNode, "Type");*/
        
        node = processVarDescriptionNode(node, varName, VarType.Integer);
        node.addCompiler(this.getCompiler(compilerName));
        return node;
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
