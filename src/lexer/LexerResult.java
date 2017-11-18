/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lexer;

import common.Token;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringJoiner;

/**
 *
 * @author Andrey
 */
public class LexerResult implements Iterator<Token>{
    protected ArrayList<Token> tokens;
    protected int pos;

    public LexerResult(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public Token getCurToken() {
        return this.tokens.get(pos);
    }

    public int getCurPos() {
        return this.pos;
    }

    public void setCurPos(int pos) {
        this.pos = pos;
    }

    @Override
    public boolean hasNext() {
        return (this.pos < tokens.size() - 1);
    }

    @Override
    public Token next() {
        this.pos++;
        return getCurToken();
    }
    
    public String getLexerPosDescription(){
        StringJoiner sJoiner = new StringJoiner(" ");
        if(getCurPos() > 0){
            int endToken = getCurPos() + 3;
            if(endToken > tokens.size()) endToken = tokens.size();
            for(int i = getCurPos(); i < endToken; i++){
                Token token = this.tokens.get(i);
                sJoiner.add(token.getValue());
            }
        }
        
        return sJoiner.toString() + " at " + getCurPos();
        
    }
}
