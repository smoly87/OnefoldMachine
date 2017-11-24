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
import syntax.analyser.parser.ParserTag;

/**
 *
 * @author Andrey
 */
public class LogicExprElemBuilder extends  ParserChain implements ParserBuilder{    
    public Parser build() {
        
        this.add(new ParserMathExpr(), "Arg1")//new ParserMathExpr()
            .addTag("LogicOperator", "LogicOperator")
            .add(new ParserMathExpr(), "Arg2");
        
        return this;
            
    }
    @Override
    public  AstNode processChainResult(HashMap<String, AstNode> result){
        //Reorder operators by calculations
        AstNode rootNode = result.get("LogicOperator");
        rootNode.addChildNode(result.get("Arg1"));
        rootNode.addChildNode(result.get("Arg2"));
        rootNode.setCompiler(this.getCompiler("LogicExprElem"));

        return rootNode;
       
    }
}
