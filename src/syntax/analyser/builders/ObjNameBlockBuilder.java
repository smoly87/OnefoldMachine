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
public class ObjNameBlockBuilder extends  ParserChain implements ParserBuilder{    
    public Parser build() {
        //Указать нужен ли результат парсера
        ParserChain chainParser = new ParserChain();
        
        chainParser.addTag("Id", "ObjName")
                   .addKeyword(".");
        
        return new ParserOptional(chainParser);
            
    }
    @Override
    public  AstNode processChainResult(HashMap<String, AstNode> result){
        //Reorder operators by calculations
        AstNode rootNode = result.get("ObjName");
        
        return rootNode;
       
    }
}
