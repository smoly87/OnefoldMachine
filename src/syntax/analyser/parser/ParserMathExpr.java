/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package syntax.analyser.parser;

import common.Token;
import compiler.expr.MathExprComplier;
import java.util.HashMap;
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
public class ParserMathExpr extends Parser{

    protected HashMap<String, Integer> priors;
    
    public ParserMathExpr(){
        priors = new HashMap<>();
        setPriorsTable();
        
    }
    
    protected void setPriorsTable(){
        priors.put("*", 1);
        priors.put("/", 1);
        
        priors.put("+", 2);
        priors.put("-", 2);
    }
    
    protected int getOperatorPrior(String operator){
        return -priors.get(operator);
    }
    
    protected  LinkedList<Token> buildReversePolishRecord(LexerResult lexerResults){
        Stack<Token> stack = new Stack<>();
        LinkedList<Token> out = new LinkedList<>();

        Token token = lexerResults.getCurToken();
        boolean lexFlag = true;
        while(lexFlag ){//&& isAllowedToken(token)
            switch(token.getTagName()){
                case "Operator":
                    if(stack.size() > 0){
                       int curPrior = getOperatorPrior(token.getName());
                       boolean flag = true;
                       while(flag){
                           String headOper = stack.firstElement().getName();
                           int headOperPrior = getOperatorPrior(headOper);
                           if(headOperPrior > curPrior){
                               out.add(stack.pop());
                           } else {
                               flag = false;
                           }  
                       }     
                    }
                    
                    stack.push(token);   
                    break;
                case "Integer": case "Id":
                    out.add(token);
                    break;
                //Как только попадается не оператор, не переменная или число и не скобки
                // Можно и по списку разреш
                default:
                    lexFlag = false;
                         
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
    
    @Override
    public boolean parseLexerResult(LexerResult lexerResults) throws ParserException{
        //Пока допустимый токен парсим как математическое выражение
       
       LinkedList<Token> out = this.buildReversePolishRecord(lexerResults);
       
       //Если в выходной строке ничего нет, то это и не мат. выражение
       if(out.size() == 0) return false;
       
       Stack<AstNode> stack = new Stack<>(); 
       for(Token token : out){
            switch(token.getTagName()){
                case "Operator":
                    //Проверка, если аргументов не достаёт
                    // Как лучше механизм проверки ошибок сделать?
                    // LogExpr наследуется почти полностью от mathExpr
                    AstNode arg1 = stack.pop();
                    AstNode arg2 = stack.pop();
                    
                    AstNode operNode = new AstNode();
                    operNode.setToken(token);
                    operNode.addChildNode(arg1);
                    operNode.addChildNode(arg2);
                    
                    stack.push(operNode);
                    
                    break;
                case "Integer": case "Id":
                    //Обратный порядок извлечения
                    AstNode varNode = new AstNode();
                    varNode.setToken(token);
                    stack.push(varNode);
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
