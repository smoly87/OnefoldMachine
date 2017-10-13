/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package syntax.analyser;

import java.util.HashMap;
import sun.security.jca.GetInstance;
import syntax.analyser.Parser;
import syntax.analyser.builders.ParserBuilder;

/**
 *
 * @author Andrey
 */
// TODO: Should we implement an singleton interface?
public class ParserFactory {
    protected HashMap<String, Parser> parsersStorage;
    protected static ParserFactory instance;
    
    private ParserFactory(){
        this.parsersStorage = new HashMap();
    }
    
    public Parser getParser(String name) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
        if(parsersStorage.containsKey(name)){
            return parsersStorage.get(name);
        } else {
            String className = "syntax.analyser.builders." + name +  "Builder";
            ParserBuilder parserBuilder = (ParserBuilder)Class.forName(className).newInstance();
            Parser parser = parserBuilder.build();
            parsersStorage.put(name, parser);
            return parser;
        }
    }
    
    public static ParserFactory getInstance(){
        if(instance == null){
            instance = new ParserFactory();
        }
        return instance;
    }
}



