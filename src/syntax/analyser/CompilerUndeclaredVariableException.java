/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser;

/**
 *
 * @author Andrey
 */
public class CompilerUndeclaredVariableException extends CompilationException{
    
    public CompilerUndeclaredVariableException(String varName) {
        super("Undeclared variable: " + varName);
    }
    
}
