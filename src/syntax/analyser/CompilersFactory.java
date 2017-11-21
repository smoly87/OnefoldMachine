/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser;

import compiler.AstCompiler;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import program.builder.ProgramBuilder;
import static syntax.analyser.ParserFactory.instance;

/**
 *
 * @author Andrey
 */
public class CompilersFactory {
    protected static CompilersFactory instance;
    protected HashMap<String, AstCompiler> elementsStorage;
    protected  String namespace;
    protected  String postfix;
    
    private CompilersFactory(){
         super();
         this.namespace = "compiler.expr";
         this.postfix = "Compiler";
         elementsStorage = new HashMap<>();
    }
    
    public AstCompiler getElement(String name)   {
        if(elementsStorage.containsKey(name)){
            return elementsStorage.get(name);
        } else {
            try {
                String className = String.format("%s.%s%s", namespace, name, postfix ) ;
                Constructor constr = Class.forName(className).getConstructor(ProgramBuilder.class);
                AstCompiler element = (AstCompiler)constr.newInstance(ProgramBuilder.getInstance());
                elementsStorage.put(name, element);
                return element;
            } catch (Exception ex) {
                System.err.println("Dynamic Load error:" + ex.getMessage());
            } 
        }
        return null;
    }
    
    public static CompilersFactory getInstance(){
        if(instance == null){
            instance = new CompilersFactory();
        }
        return instance;
    }
}
