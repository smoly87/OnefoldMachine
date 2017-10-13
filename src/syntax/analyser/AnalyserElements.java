/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import syntax.analyser.builders.ParserBuilder;

/**
 *
 * @author Andrey
 */
public class AnalyserElements<ElementType> {
    protected HashMap<String, ElementType> elementsStorage;
    protected  String namespace;
    protected  String postfix;
    
    protected AnalyserElements(){
        elementsStorage = new HashMap<>();
    }
    
    
    public ElementType getElement(String name) {
        if(elementsStorage.containsKey(name)){
            return elementsStorage.get(name);
        } else {
            try {
                String className = String.format("%s.%s%s", namespace, name, postfix ) ;
                ElementType element = (ElementType)Class.forName(className).newInstance();
                
                elementsStorage.put(name, element);
                return element;
            } catch (Exception ex) {
                System.err.println("Dynamic Load error:" + ex.getMessage());
            } 
        }
        return null;
    }
    
}
