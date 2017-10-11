/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package syntax.analyser.builders;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import syntax.analyser.Parser;
import syntax.analyser.parser.ParserChain;
import syntax.analyser.parser.ParserKeyword;
import syntax.analyser.parser.ParserTag;

/**
 *
 * @author Andrey
 */
public interface ParserBuilder {
     
    public  abstract Parser build();
    
   
    
    
   
}
