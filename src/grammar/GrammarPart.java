/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package grammar;

import common.TokenInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 * @author Andrey
 */
public class GrammarPart {
    protected LinkedHashMap<String, ArrayList<TokenInfo>> tokensInfo;
    
    public GrammarPart(){
        this.tokensInfo = new LinkedHashMap<>();
    }
    
    public void addTokens(ArrayList<TokenInfo> tokensDescr, String tokenType, boolean useRegExpr){
        tokensInfo.put(tokenType, tokensDescr);
    } 
    
    public LinkedHashMap<String, ArrayList<TokenInfo>> getFullInfo(){
        return tokensInfo;
    }
}
