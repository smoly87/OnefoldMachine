/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser.builders;

import common.Tag;
import common.Token;
import common.TokenInfo;
import common.VarType;
import compiler.expr.FunctionCompiler;
import compiler.expr.LetCompiler;
import grammar.GrammarInfo;
import grammar.GrammarInfoStorage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;
import syntax.analyser.parser.ParserAlternative;
import syntax.analyser.parser.ParserChain;
import syntax.analyser.parser.ParserMathExpr;
import syntax.analyser.parser.ParserRepeated;
import syntax.analyser.parser.ParserTag;

/**
 *
 * @author Andrey
 */
public class TypesListBuilder extends  ParserChain implements ParserBuilder{
    public Parser build() {
        //Указать нужен ли результат парсера
        
        GrammarInfo gs = GrammarInfoStorage.getInstance();
        HashSet<String> typesSet = gs.getTypesList();
        
        ParserAlternative altParser = new ParserAlternative();
        for(String typeName:typesSet){
             altParser.add(new ParserTag(typeName));
        }
        
         
        altParser.add(new ParserTag("Id"));
        return altParser;   
    }
   
}
