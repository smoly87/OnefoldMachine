/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser;

import compiler.AstCompiler;
import java.util.HashMap;
import static syntax.analyser.ParserFactory.instance;

/**
 *
 * @author Andrey
 */
public class CompilersFactory extends AnalyserElements<AstCompiler>{
    protected static CompilersFactory instance;
  
    
    private CompilersFactory(){
         super();
         this.namespace = "compiler.expr";
         this.postfix = "Compiler";
    }
    
    public static CompilersFactory getInstance(){
        if(instance == null){
            instance = new CompilersFactory();
        }
        return instance;
    }
}
