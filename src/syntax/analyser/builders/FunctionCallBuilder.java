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
import syntax.analyser.parser.ParserKeyword;
import syntax.analyser.parser.ParserMathExpr;
import syntax.analyser.parser.ParserOptional;
import syntax.analyser.parser.ParserRepeated;
import syntax.analyser.parser.ParserTag;

/**
 *
 * @author Andrey
 */
public class FunctionCallBuilder extends  ParserChain implements ParserBuilder{
    protected final String compilerName = "FunctionCall";
    protected Parser getTypesListParser(){
      
        return   this.getParser("TypesList");
    }
    
    protected Parser getArgBlockRepeatedParser(){
        
        
        ParserChain chainParser = new ParserChain();
        chainParser.add(getTypesListParser(), "Arg")
                    .add(new ParserOptional(new ParserKeyword(",")), "comma"); 
        
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
            .addTag("Id", "FunctionId")
            .addKeyword("(", "StartArgs")
            .add(getArgBlockRepeatedParser(), "ArgsBlock")
            .addKeyword(")", "EndCall");
            //.addKeyword(";");
            
    }
    
    @Override
    public  AstNode processChainResult(HashMap<String, AstNode> result){
        //Reorder operators by calculations
        AstNode rootNode = result.get("Call");
       
        rootNode.setCompiler(this.getCompiler(this.compilerName));
        
        rootNode.addChildNode(result.get("FunctionId"), "FunctionId");
        
        rootNode = addBlockIfExists(rootNode, "ObjName", result, SET_COMPILER_MODE.SET, compilerName );
        rootNode.addChildNode(result.get("StartArgs"), "StartArgs");
        rootNode.addChildNode(result.get("EndCall"), "EndCall");
        rootNode = addBlockIfExists(rootNode, "ArgsBlock", result, SET_COMPILER_MODE.ADD, compilerName );
        rootNode.addChildNode(new AstNode(), "AfterArgsBlock");
        
          
        
        System.out.println("FunctionCall parser has been reached");
        

        return rootNode;
    }
}
