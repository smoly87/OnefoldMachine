package lexer;

import common.Tag;
import common.TokenInfo;
import common.Token;
import grammar.GrammarInfo;
import grammar.GrammarPart;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.*;
import java.util.regex.Pattern;
import utils.Pair;

public class Lexer {
  protected ArrayList<TokenInfo> tokensInfo;// лучше linkedlist
  protected GrammarInfo grammarInfo;
  protected ArrayList<Token> tokens;
  protected int pos;
  
  public Lexer(GrammarInfo grammarInfo){
     this.grammarInfo = grammarInfo;
     this.getAllTokensInfo();
  }
  
  protected void getAllTokensInfo(){
     this.tokensInfo = new ArrayList<TokenInfo>();
     LinkedHashMap<String, GrammarPart> info = grammarInfo.getFullInfo();
     //Собираем все токены из всех разделов
     //Каждый раздел содержит ещё разные виды токенов - ved, id итд
     for (Map.Entry<String, GrammarPart> entry : info.entrySet()) {
         GrammarPart part = entry.getValue();
         LinkedHashMap<String, ArrayList<TokenInfo>> allPartTokens = part.getFullInfo();
         for (Map.Entry<String, ArrayList<TokenInfo>> partEntry : allPartTokens.entrySet()) {
             tokensInfo.addAll(partEntry.getValue());
         }
     }
        
    
  }
  
  protected Pair<Token, Integer> find(String text, int pos){
      for(TokenInfo tokenInfo: tokensInfo){
          Pair<Integer, String> res = tokenInfo.isSatisfy(text, pos) ;
          if(res != null && res.getObj1() > -1){
              Token token = new Token(tokenInfo.getTagText(), tokenInfo.getTag(), res.getObj2());
              return new Pair(token, res.getObj1());
          }
      }
      return null;
  }
    
  
  public LexerResult parse(String text){
    ArrayList<Token> tokens = new ArrayList<Token>();
    int pos = 0;
    text = text.replace(" ", "");
  
    while(pos < text.length()){
       Pair<Token, Integer> res = this.find(text, pos);
       if(res != null){
           tokens.add(res.getObj1());
           System.out.println(res.getObj1().getTagName()+ " " +  res.getObj1().getValue()+ "  at " + pos);
           pos = res.getObj2();
       } else{
           return null;
       }
    }
    
    return new LexerResult(tokens);
  }
  
 
  
  
  
}
