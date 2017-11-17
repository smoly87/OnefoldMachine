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

    public ClassInfo getClassInfo() {
        return classInfo;
    }
    protected int funcStartLine = -1;
    
    public ClassCompiler(){
         /*this.getCompiler("Function").addSubscriber(this);
         this.getCompiler("Field").addSubscriber(this);*/
    }
    
    @Override
    public void compileChild(AstNode node, ProgramBuilder programBuilder) throws CompilerException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
       
      
       Token token = node.getToken();
       switch(node.getName()){
            case "StartClass":   
                classInfo = new ClassInfo(node.getToken().getValue());
                this.curFuncName = null;
                break;
            case "ExtendsClass":
                classInfo.setParentClass(node.getToken().getValue());
                break;
            case "EndClass":
                //this.commitCurMethod(programBuilder);
                programBuilder.addInstruction(VMCommands.NOP);
                classInfo.setEndClassLine(programBuilder.commandsSize());
                MetaClassesInfo.getInstance().addClassInfo(classInfo);
                classInfo = new ClassInfo(node.getToken().getValue());
                break;
                
            case "StartFunctionBody":
               /* funcStartLine = programBuilder.commandsSize();
                this.curFuncName = token.getValue();
             
                commitCurMethod(programBuilder);*/
                FunctionCompiler funcCompiler  = (FunctionCompiler)this.getCompiler("Function");
                classInfo.addMethod(funcCompiler.getCurrentFunction());
                break;
           /* case "VarDescription":
                VarType type = node.getToken().getVarType();
                argsSignatureBuilder.append(Integer.toString(type.ordinal()));
                break;*/
            case "Field":
                classInfo.addField(token.getValue(), token.getVarType());
                break;    
            
        }
    }
    @Override
    public void compileRootPre(AstNode node, ProgramBuilder programBuilder) {
       
         VarCompiler varCompiler  = (VarCompiler)this.getCompiler("Var");
         //if(node.getName() == "VarsBlock") processVarsBlock(node, programBuilder);
    }
    

    /*protected void commitCurMethod(ProgramBuilder programBuilder){ 
        if (this.curFuncName != null) {
            ///String funcName = this.curFuncName + ":" + argsSignatureBuilder.toString();
            classInfo.addMethod(curFuncName, argsSignatureBuilder.toString());
        }
    }
   */

   /* @Override
    public void nodeProcessEvent(AstNode node, ProgramBuilder programBuilder) {
        Token token = node.getToken();
       
        String nodeName = "";
        if(node.getName() != null) nodeName = node.getName();
        switch(nodeName){
            case "FunctionId":
                funcStartLine = programBuilder.commandsSize();
                this.curFuncName = token.getValue();
                argsSignatureBuilder = new StringBuilder();
                commitCurMethod(programBuilder);
                
                
                break;
            case "VarDescription":
                VarType type = node.getToken().getVarType();
                argsSignatureBuilder.append(Integer.toString(type.ordinal()));
                break;
            case "Field":
                classInfo.addField(token.getValue(), token.getVarType());
                break;
        }
    }*/
    
}
