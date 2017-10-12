/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package grammar;

import common.Tag;
import common.TokenInfo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import main.Main;

/**
 *
 * @author Andrey
 */
public class GrammarInfoStorage {
    final static String  LEXER_TOKEN = "lexer_token_";
    protected static GrammarInfo grammarInfo;
    
    public static GrammarInfo getInstance(){
        if(grammarInfo == null){
            grammarInfo = readConfig();
        }
        
        return grammarInfo;
    }
    
    protected static GrammarInfo readConfig(){
         //InputStream inputStream = ConfigReader.class.getResourceAsStream("/assets.csv");
         String path = Main.class.getResource("/assets/grammars.conf").getPath();
         GrammarInfo grammarInfo = new GrammarInfo();
         try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String curLine;
            GrammarPart part = null;
            String curPartName = "";
            
            while ((curLine = br.readLine()) != null) {
                if (!curLine.startsWith("  ")) {
                    if (part != null) {
                        grammarInfo.addPart(curPartName, part);

                    }
                    curPartName = curLine.substring(0, curLine.length() - 1);
                    part = new GrammarPart();
                } else{
                    curLine = curLine.substring(2);
                }
                if (curLine.startsWith(LEXER_TOKEN)) {
                    int firstColon = curLine.indexOf(':');
                    boolean useRegExpr = false;
                    
                    String tokenType = curLine.substring(LEXER_TOKEN.length(), firstColon);
                    // # - is flag of regular expression should be cutted from name
                    if(tokenType.startsWith("#")){
                        tokenType = tokenType.substring(1, tokenType.length());
                        useRegExpr = true;
                    }
                    
                    Tag tag = new Tag(tokenType);
                    
                    String tokensStr = curLine.substring(firstColon + 1,  curLine.length());
                    //Обработка #
                    String[] tokens = tokensStr.trim().split(" ");
                    ArrayList<TokenInfo> tokensInfoList = new ArrayList<TokenInfo>();
                    for(String tokenStr: tokens){
                        tokensInfoList.add(new TokenInfo(tokenStr, tag, useRegExpr));
                    }
                    
                    part.addTokens(tokensInfoList, tokenType, true);
                    
                }
            }
            grammarInfo.addPart(curPartName, part);
        } catch (IOException Exception) {

        }   
         
        return grammarInfo; 
    }
}
