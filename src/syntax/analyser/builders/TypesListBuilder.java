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
public class TypesListBuilder extends  ParserChain implements ParserBuilder{
    public Parser build() {
        //Указать нужен ли результат парсера
        ParserAlternative altParser = new ParserAlternative();
        altParser.add(new ParserTag("Id"));
        altParser.add(new ParserTag("Integer"));
        altParser.add(new ParserTag("String"));
        altParser.add(new ParserTag("Float"));
        
        return altParser;   
    }
   
}
