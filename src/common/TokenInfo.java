package common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import utils.Pair;

public class TokenInfo {
  protected String tagText;
  protected Tag tag;
  protected boolean useRegExpr;
  protected Pattern regex;

  public boolean isUseRegExpr() {
        return useRegExpr;
  }
  
  public TokenInfo(String tagText, Tag tag, boolean useRegExpr){
    this.tagText = tagText;
    this.tag = tag;
    this.useRegExpr = useRegExpr;
    if(this.useRegExpr){
        this.regex = Pattern.compile(tagText);
    }
  }
  
  public String getTagText(){
    return tagText;
  }
  
  public Tag getTag(){
    return tag;
  }
  
  /**
   * Method return true, if given text is satisfy regexpr of tag.
   * Return pair - position and value of tag if it's varible: ID, INT
   * Or if tag doesn't use regular expression then return true if text is equal to tag
   * @param text
   * @return 
   */
  
  public Pair<Integer, String> isSatisfy(String text, int start){
      if(!useRegExpr){
         if(start + tagText.length() <= text.length()){ 
            String part = text.substring(start, start + tagText.length()); 
            if(part.equals(tagText)){
               return new Pair(start + tagText.length(), tagText) ;
            }
         }
      } else{
          Matcher m = regex.matcher(text);
          
          if(m.find(start ) && m.start() == start){
              int endI = m.end();
              return new Pair(endI, m.group()) ;
          }
    
      }
      
      return null;
  }
  
}
