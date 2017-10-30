/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package syntax.analyser.builders;

import com.sun.xml.internal.ws.util.StringUtils;
import common.Token;
import java.util.HashMap;
import java.util.HashSet;
import lexer.LexerResult;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;
import syntax.analyser.UnexpectedSymbolException;
import syntax.analyser.parser.ParserAlternative;
import syntax.analyser.parser.ParserChain;
import syntax.analyser.parser.ParserException;
import syntax.analyser.parser.ParserLazy;
import syntax.analyser.parser.ParserRepeated;

/**
 *
 * @author Andrey
 */
public class ParserStatementRepeatedBuilder extends ParserChain  implements ParserBuilder{    
    @Override
    public Parser build() { 
        return new ParserRepeated(new ParserLazy("ParserStatement"));
    }
     
   
   
     
}
