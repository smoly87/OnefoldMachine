/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser.builders;

import compiler.expr.FunctionCallCompiler;
import compiler.expr.FunctionCompiler;
import compiler.expr.LetCompiler;
import java.util.HashMap;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;
import syntax.analyser.parser.ParserAlternative;
import syntax.analyser.parser.ParserChain;
import syntax.analyser.parser.ParserMathExpr;
import syntax.analyser.parser.ParserOptional;
import syntax.analyser.parser.ParserRepeated;
import syntax.analyser.parser.ParserStatement;
import syntax.analyser.parser.ParserTag;

/**
 *
 * @author Andrey
 */
public class FunctionCallBuilder extends  ParserChain implements ParserBuilder{
    
    protected Parser getTypesListParser(){
      
        return   this.getParser("TypesList");
    }
    
    protected Parser getArgBlockRepeatedParser(){
        
        
        ParserChain chainParser = new ParserChain();
        chainParser.add(getTypesListParser(), "Arg")
                   .addKeyword(","); 
        
        return new ParserOptional(new ParserRepeated(chainParser));
    }
    
   
    protected Parser getLastArgParser(){
        return new ParserOptional(getTypesListParser());
    }
    
    protected Parser optClassNameParser(){
        ParserChain chainParser = new ParserChain();
        
        chainParser.addTag("Id", "ClassName")
                   .addKeyword(".");
        
        return new ParserOptional(chainParser);
    }
    
    public Parser build() {
        //Указать нужен ли результат парсера
       return this
            .addKeyword("Call")
            //.addKeyword(":")  
            .add(this.getParser("ObjNameBlock"), "ObjName")   
            .addTag("Id")
            .addKeyword("(")
            .add(getArgBlockRepeatedParser(), "ArgsBlock")
            .add(getLastArgParser(), "LastArg")
            .addKeyword(")", "EndCall");
            
            //.addKeyword(";");
            
    }
    @Override
    public  AstNode processChainResult(HashMap<String, AstNode> result){
        //Reorder operators by calculations
        AstNode rootNode = result.get("Call");
        rootNode.setCompiler(new FunctionCallCompiler());

        if(result.get("ObjName") != null){
           rootNode.addChildNode(result.get("ObjName"), "ObjName");
        }
        
        rootNode.addChildNode(result.get("Id"), "FunctionId");

        AstNode argNode = result.get("ArgsBlock");
        if(result.get("LastArg") != null){
            if(argNode == null) argNode = new AstNode();
            argNode.addChildNode(result.get("LastArg"), "Arg");
        }
        if(argNode != null){
            rootNode.addChildNode(argNode, "ArgsBlock");
        }
        
          
        rootNode.addChildNode(result.get("EndCall"), "EndCall");
        System.out.println("FunctionCall parser has been reached");
        
        return rootNode;
    }
}
