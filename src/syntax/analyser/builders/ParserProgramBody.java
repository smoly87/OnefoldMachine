/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser.builders;

import syntax.analyser.builders.ParserStatementBuilder;
import syntax.analyser.Parser;
import syntax.analyser.parser.ParserAlternative;
import syntax.analyser.parser.ParserLazy;
import syntax.analyser.parser.ParserRepeated;

/**
 *
 * @author Andrey
 */
public class ParserProgramBody extends ParserStatementBuilder{

    
    @Override
    public Parser build() {
        super.build();
        this.add(this.getParser("Function"));

        this.add(this.getParser("Class"));
        
        //possibleAlts.add(this.getParser("ParserStatement"));
        
        return new ParserRepeated(this) ;
    }
    
}
