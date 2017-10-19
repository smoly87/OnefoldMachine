/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser.parser;

/**
 *
 * @author Andrey
 */
public class ParserProgramBody extends ParserStatement{

    
    @Override
    protected void addPossibleStatements() {
        super.addPossibleStatements();
        this.possibleAlts.add(this.getParser("Function"));
        this.possibleAlts.add(this.getParser("FunctionCall"));
        this.possibleAlts.add(this.getParser("Class"));
        
        this.possibleAlts.add(new ParserRepeated(new ParserStatement()));
    }
    
}
