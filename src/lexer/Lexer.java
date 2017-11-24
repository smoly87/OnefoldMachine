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
import main.ProgramLogger;
import syntax.analyser.parser.ProgramBuildingStage;
import utils.Pair;

public class Lexer extends ProgramBuildingStage{
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

     //It's needed to collect tokens info from parts
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
           if(hasSubscribers) this.callSubscribers("LEXEM_FIND", 
                String.format("Find lexem %s with value %s at %s", res.getObj1().getTagName(), res.getObj1().getValue(), pos
           ));
           pos = res.getObj2();
       } else{
           return null;
       }
    }
    
    return new LexerResult(tokens);
  }
  
 
  
  
  
}
