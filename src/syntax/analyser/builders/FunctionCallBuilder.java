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
import syntax.analyser.parser.ParserRepeated;
import syntax.analyser.parser.ParserStatement;
import syntax.analyser.parser.ParserTag;

/**
 *
 * @author Andrey
 */
public class FunctionCallBuilder extends  ParserChain implements ParserBuilder{
    
    protected Parser getArgBlockRepeatedParser(){
        ParserAlternative argTypesAltParser = new ParserAlternative();
        argTypesAltParser.add(new ParserTag("Float"));
        argTypesAltParser.add(new ParserTag("Int"));
        argTypesAltParser.add(new ParserTag("String"));
        
        ParserChain chainParser = new ParserChain();
        chainParser.add(argTypesAltParser, "arg")
                   .addKeyword(","); 
        
        return new ParserRepeated(chainParser);
    }
    
   
    
    public Parser build() {
        //Указать нужен ли результат парсера
       return this
            .addKeyword("Call")
            //.addKeyword(":")   
            .addTag("Id")
            .addKeyword("(")
            .add(getArgBlockRepeatedParser(), "ArgsBlock")
            .addKeyword(")");
            
            //.addKeyword(";");
            
    }
    @Override
    public  AstNode processChainResult(HashMap<String, AstNode> result){
        //Reorder operators by calculations
        AstNode rootNode = result.get("Call");
        rootNode.setCompiler(new FunctionCallCompiler());
        /*rootNode.setToken(token);
        //Think about gloabl agreement of naming
        rootNode.setName("Let");*/
        rootNode.addChildNode(result.get("ArgsBlock"));
        rootNode.addChildNode(result.get("Id"));
        
        return rootNode;
    }
}
