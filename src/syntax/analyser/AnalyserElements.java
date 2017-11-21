/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import syntax.analyser.builders.ParserBuilder;

/**
 *
 * @author Andrey
 */
public abstract class AnalyserElements<ElementType> {
    protected HashMap<String, ElementType> elementsStorage;
    protected  String namespace;
    protected  String postfix;
    
    protected AnalyserElements(){
        elementsStorage = new HashMap<>();
    }
    
     
    
 
    
}
