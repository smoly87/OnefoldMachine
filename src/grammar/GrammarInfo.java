/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package grammar;

import common.TokenInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import syntax.analyser.parser.ParserTag;

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
    
    public HashSet<String> getTypesList(){
       ArrayList<TokenInfo> typesListTokens = this.getFullInfo().get("Var").getFullInfo().get("Type");
       HashSet<String> typesSet = new HashSet<>();
       
       for(TokenInfo typeToken:typesListTokens){
             String typeName = typeToken.getTagText();
             typesSet.add(typeName);
       }
       
       return typesSet;
    }
    
    
    
}
