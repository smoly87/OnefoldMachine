/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.expr;

import common.Token;
import common.VarType;
import compiler.AstCompiler;
import compiler.CompilerSubscriber;
import compiler.exception.CompilerException;
import java.util.LinkedList;
import java.util.StringJoiner;
import compiler.metadata.ClassInfo;
import compiler.metadata.FunctionDescription;
import compiler.metadata.MetaClassesInfo;
import program.builder.ProgramBuilder;
import syntax.analyser.AstNode;
import virtual.machine.VMCommands;

/**
 *
 * @author Andrey
 */
public class ClassCompiler extends AstCompiler implements CompilerSubscriber{

    protected String curFuncName;   
    protected ClassInfo classInfo;
    protected boolean firstStage = true;

    public ClassCompiler(ProgramBuilder programBuilder) {
        super(programBuilder);
    }

    public boolean isFirtStage() {
        return firstStage;
    }

    public void setFirtStage(boolean firtStage) {
        this.firstStage = firtStage;
    }

    public ClassInfo getClassInfo() {
        return classInfo;
    }
    protected int funcStartLine = -1;

    
    @Override
    public void compileChild(AstNode node) throws CompilerException { 
       
        
       Token token = node.getToken();
       
       if(!firstStage) {
            switch(node.getName()){
                case "StartClass":
                    classInfo = MetaClassesInfo.getInstance().getClassInfo(node.getToken().getValue());
                    break;
                case "EndClass":
                    programBuilder.addInstruction(VMCommands.NOP);
                    classInfo.setEndClassLine(programBuilder.commandsSize());
                    break;
            }
          
          
           return; 
       }
       switch(node.getName()){
            case "StartClass":   
                classInfo = new ClassInfo(node.getToken().getValue());
                this.curFuncName = null;
                break;
            case "ExtendsClass":
                classInfo.setParentClass(node.getToken().getValue());
                break;
            case "EndClass":
                
                MetaClassesInfo.getInstance().addClassInfo(classInfo);
                classInfo = new ClassInfo(node.getToken().getValue());
                break;
            
            case "StartFunctionBody":
                FunctionCompiler funcCompiler  = (FunctionCompiler)this.getCompiler("Function");
                classInfo.addMethod(funcCompiler.getCurrentFunction());
                break;
            case "Field":
                classInfo.addField(token.getValue(), token.getVarType());
                break;    
            
        }
    }

  
    
}
