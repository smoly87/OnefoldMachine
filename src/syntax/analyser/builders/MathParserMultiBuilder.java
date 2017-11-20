/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser.builders;

import java.util.HashMap;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;
import syntax.analyser.parser.ParserAlternative;
import syntax.analyser.parser.ParserChain;
import syntax.analyser.parser.ParserLazy;
import syntax.analyser.parser.ParserTag;

/**
 *
 * @author Andrey
 */
public class MathParserMultiBuilder extends ParserChain implements ParserBuilder{

    public Parser getArgParser(){
        ParserAlternative altParser = new ParserAlternative();
        altParser.add(new ParserTag("Integer"));
        altParser.add(new ParserTag("Id"));
        altParser.add(new ParserLazy("MathExprBuilder"));
        return altParser;
    }
    
    public Parser getOperParser(){
       /* ParserAlternative altParser = new ParserAlternative();
        altParser.add(new ParserTag("Operator_Math0"));*/
        return new ParserTag("Operator_Math0");
    }
    
    @Override
    public Parser build() {
        this.add(getArgParser(), "Arg1");
        this.add(getOperParser(), "Oper");
        this.add(getArgParser(), "Arg2");
        return this;
    }
    @Override
    public  AstNode processChainResult(HashMap<String, AstNode> result){
        AstNode rootNode = result.get("Oper");
        rootNode.setCompiler(this.getCompiler("MathPrecedence"));
        rootNode.addChildNode(result.get("Arg1"), "Arg1");
        rootNode.addChildNode(result.get("Arg2"), "Arg2");
        return rootNode;
    }
}
