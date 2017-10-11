/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package grammar;

import java.util.LinkedHashMap;

/**
 *
 * @author Andrey
 */
public class GrammarInfo {
    protected LinkedHashMap<String, GrammarPart> info;
    public GrammarInfo(){
        this.info = new LinkedHashMap<>();
    }
    
    public void addPart(String name, GrammarPart part){
        info.put(name, part);
    }
    
    public LinkedHashMap<String, GrammarPart> getFullInfo(){
        return info;
    }
    
    
    
}
