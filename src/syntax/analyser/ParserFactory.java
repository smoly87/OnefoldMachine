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
import syntax.analyser.builders.ParserBuilder;


/**
 *
 * @author Andrey
 */
// TODO: Should we implement an singleton interface?
public class ParserFactory {

    protected static ParserFactory instance;
    protected HashMap<String, Parser> elementsStorage;
    protected  String namespace;
    protected  String postfix;
    
    private ParserFactory(){
         super();
         this.namespace = "syntax.analyser.builders";
         this.postfix = "Builder";
         elementsStorage = new HashMap<>();
    }


    public Parser getElement(String name) {
        if(elementsStorage.containsKey(name)){
            return elementsStorage.get(name);
        } else {
            try {
                String className = String.format("%s.%s%s", namespace, name, postfix ) ;
                Constructor constr = Class.forName(className).getConstructor();
                ParserBuilder parserBuilder = (ParserBuilder)constr.newInstance();
                Parser element = parserBuilder.build();
                elementsStorage.put(name, element);
                return element;
            } catch (Exception ex) {
                System.err.println("Dynamic Load error:" + ex.getMessage());
            } 
        }
        return null;
    }
    
    
    public static ParserFactory getInstance(){
        if(instance == null){
            instance = new ParserFactory();
        }
        return instance;
    }

 
}



