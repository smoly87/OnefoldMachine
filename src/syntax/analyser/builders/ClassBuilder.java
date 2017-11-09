/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser.builders;

import common.Tag;
import common.Token;
import common.VarType;
import compiler.expr.ClassCompiler;
import compiler.expr.FunctionCompiler;
import compiler.expr.LetCompiler;
import java.util.HashMap;
import syntax.analyser.AstNode;
import syntax.analyser.Parser;
import syntax.analyser.parser.ParserAlternative;
import syntax.analyser.parser.ParserChain;
import syntax.analyser.parser.ParserMathExpr;
import syntax.analyser.parser.ParserOptional;
import syntax.analyser.parser.ParserRepeated;
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
    
    protected Parser getExtendsOptBlock(){
        ParserChain parser = new ParserChain();
        parser.addKeyword(":")
              .addKeyword("Extends")
              .addTag("Id", "ExtendsClass");
        
        return new ParserOptional(parser);
    }
         
    public Parser build() {

       return this
            .addKeyword("Class") 
            .addTag("Id")
            .add(getExtendsOptBlock(), "ExtendsBlock")
            .addKeyword("{") // Body of function
            .add(getClassFieldsOrMethodsParser(), "ClassFieldsOrMethods") 
            .addKeyword("}", "EndClass");           
    }
    
    @Override
    public  AstNode processChainResult(HashMap<String, AstNode> result){
        //Reorder operators by calculations
        AstNode rootNode = result.get("Class");
        rootNode.setCompiler(this.getCompiler("Class"));
        System.out.println("Class parser has been reached");
        rootNode.addChildNode(result.get("Id"), "StartClass");
        if(result.get("ExtendsBlock") != null) {
            AstNode extendsBlock = result.get("ExtendsBlock");
            extendsBlock.addCompiler(this.getCompiler("Class"));
            rootNode.addChildNode(extendsBlock, "ExtendsBlock");
        }
        
        
        AstNode fieldsOrMethodsNode = result.get("ClassFieldsOrMethods");
        fieldsOrMethodsNode.addCompiler(this.getCompiler("Class"));
        
        rootNode.addChildNode(fieldsOrMethodsNode, "ClassFieldsOrMethods");
        rootNode.addChildNode(result.get("EndClass"), "EndClass");
        
        

        return rootNode;
    }
    
    
}
