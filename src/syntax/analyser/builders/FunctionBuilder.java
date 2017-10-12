/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser.builders;

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
        //Todo: special flags for local variables
        //Override indexes?
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
            //.addKeyword(":")   
            .addTag("Type")
            .addTag("Id")
            .addKeyword("(")
            .add(getVarBlockRepeatedParser(), "VarsBlock")
            .addKeyword(")")
            .addKeyword("{") // Body of function
            .add(getFunctionBodyParser(), "FunctionBody") 
            .addKeyword("Return") 
            .add(getReturnStatementParser(), "ReturnStatement")
            .addKeyword("}");
            //.addKeyword(";");
            
    }
    @Override
    public  AstNode processChainResult(HashMap<String, AstNode> result){
        //Reorder operators by calculations
        AstNode rootNode = result.get("Function");
        rootNode.setCompiler(new FunctionCompiler());
        System.out.println("Function parser has been reached");
        rootNode.addChildNode(result.get("Id"), "Id");
        rootNode.addChildNode(result.get("VarsBlock"), "VarsBlock");
        rootNode.addChildNode(result.get("FunctionBody"));
        /*rootNode.setToken(token);
        //Think about gloabl agreement of naming
        rootNode.setName("Let");*/
      ////  rootNode.addChildNode(result.get("MathExpr"));
      //  rootNode.addChildNode(result.get("Id"));
        
        return rootNode;
    }
}
