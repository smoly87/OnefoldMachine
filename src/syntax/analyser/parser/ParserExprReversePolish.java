/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package syntax.analyser.parser;

import common.Token;
import compiler.expr.MathExprComplier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;
import javax.xml.bind.annotation.XmlElement;
import lexer.LexerResult;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;

/**
 *
 * @author Andrey
 */
public abstract class ParserExprReversePolish extends Parser{

    protected HashMap<String, Integer> priors;
    protected HashSet<String> allowedOperandTags;
    protected   String OperatorTag = "Operator";
    
    public ParserExprReversePolish(){
        priors = new HashMap<>();
        allowedOperandTags = new HashSet<String>();
        setPriorsTable();
        
    }
    protected abstract void setPriorsTable();
   
    
    protected int getOperatorPrior(String operator){
        return -priors.get(operator);
    }
    
    protected void reorderOperators(Token token, Stack<Token> stack, LinkedList<Token> out ){
        if (stack.size() > 0) {
            int curPrior = getOperatorPrior(token.getName());
            boolean flag = true;
            while (flag) {
                if(stack.size() == 0) break;
                String headOper = stack.firstElement().getName();
                int headOperPrior = getOperatorPrior(headOper);
                if (headOperPrior > curPrior) {
                    out.add(stack.pop());
                } else {
                    flag = false;
                }
            }
        }
        stack.push(token);  
    }
    
    protected  LinkedList<Token> buildReversePolishRecord(LexerResult lexerResults) throws ParserException{
        Stack<Token> stack = new Stack<>();
        LinkedList<Token> out = new LinkedList<>();

        Token token = lexerResults.getCurToken();
        boolean lexFlag = true;
        while(lexFlag ){//&& isAllowedToken(token)
            switch(token.getTagName()){
               
                default:
                    if(token.getTagName().equals(OperatorTag)){
                        reorderOperators(token, stack, out);
                    } else {
                        if (allowedOperandTags.contains(token.getTagName())) {
                            out.add(token);
                        } else {
                            lexFlag = false;
                        }
                    }
                    
                    
                    
                         
            }
            if(lexFlag && lexerResults.hasNext()){
                token = lexerResults.next();
            } else{
                lexFlag = false;
                break;
            }
            
        }       
        //Проверка согласованности
        
        if(stack.size() > 0){
            while(stack.size() > 0){
                out.add(stack.pop());
            }
        }
        return out;
    }
    
    
    protected AstNode getOperationNode(Token token, AstNode arg1, AstNode arg2){
        AstNode operNode = new AstNode();
        operNode.setToken(token);
        operNode.addChildNode(arg1);
        operNode.addChildNode(arg2);
        return operNode;
    }
    
    @Override
    public boolean parseLexerResult(LexerResult lexerResults) throws ParserException{
        //Пока допустимый токен парсим как математическое выражение
       
       LinkedList<Token> out = this.buildReversePolishRecord(lexerResults);
       
       //Если в выходной строке ничего нет, то это и не мат. выражение
       if(out.size() == 0) return false;
       
       Stack<AstNode> stack = new Stack<>(); 
       for(Token token : out){
            switch(token.getTagName()){
                default:
                    //If operator, then form full operation
                    //By adding 2 arguments
                    if(token.getTagName().equals(OperatorTag)){
                       AstNode operNode = getOperationNode(token, stack.pop(), stack.pop());
                       stack.push(operNode);
                    }else{
                        //Обратный порядок извлечения
                        if (!allowedOperandTags.contains(token.getTagName())) {
                            throw new ParserException(String.format("Unknown tag in expr:", token.getTagName()));
                        }
                        AstNode varNode = new AstNode();
                        varNode.setToken(token);
                        stack.push(varNode);
                    }
                   
                    break;
              
                   
            }
           
       }
       if(stack.size() > 0){
          AstNode resNode = stack.pop(); 
          resNode.setCompiler(new MathExprComplier());
          this.setParseResult(resNode);
          return true;
       } else{
          throw new ParserException("Unbalanced math expr:"  );
       }
       
    }
    
}
