/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser.builders;

import common.Tag;
import common.Token;
import common.VarType;
import compiler.expr.FunctionCompiler;
import compiler.expr.LetCompiler;
import java.util.HashMap;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;
import syntax.analyser.parser.ParserAlternative;
import syntax.analyser.parser.ParserChain;
import syntax.analyser.parser.ParserMathExpr;
import syntax.analyser.parser.ParserRepeated;
import syntax.analyser.parser.ParserStatement;
import syntax.analyser.parser.ParserTag;

/**
 *
 * @author Andrey
 */
public class ClassBuilder extends  ParserChain implements ParserBuilder{
    
       
    protected Parser getClassFieldsOrMethodsParser(){
        
        ParserAlternative classEntriesAlts = new ParserAlternative();
        classEntriesAlts.add(this.getParser("Function"));
        classEntriesAlts.add(this.getParser("Field"));
        
        
        return new ParserRepeated(classEntriesAlts);
    }
    
         
    public Parser build() {

       return this
            .addKeyword("Class") 
            .addTag("Id")
            .addKeyword("{") // Body of function
            .add(getClassFieldsOrMethodsParser(), "ClassFieldsOrMethods") 
            .addKeyword("}");            
    }
    @Override
    public  AstNode processChainResult(HashMap<String, AstNode> result){
        //Reorder operators by calculations
        AstNode rootNode = result.get("Class");
        rootNode.setCompiler(new FunctionCompiler());
        System.out.println("Class parser has been reached");
        rootNode.addChildNode(result.get("Id"), "Id");
        rootNode.addChildNode(result.get("ClassFieldsOrMethods"), "ClassFieldsOrMethods");

        return rootNode;
    }
    
    
}
