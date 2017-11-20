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
public class MathExprBuilder extends ParserAlternative implements ParserBuilder{

    
    
    @Override
    public Parser build() {
        this.add(new ParserLazy("MathParserMulti"));
        
        
        
        return this;
    }
    
}
