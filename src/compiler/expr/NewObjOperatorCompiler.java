/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.expr;

import common.Token;
import common.VarType;
import compiler.AstCompiler;
import compiler.exception.CompilerException;
import java.awt.geom.Line2D;
import java.util.LinkedList;
import program.builder.ClassInfo;
import program.builder.FunctionDescription;
import program.builder.MetaClassesInfo;
import program.builder.ProgramBuilder;
import syntax.analyser.AstNode;
import virtual.machine.VM;
import virtual.machine.VMCommands;
import virtual.machine.VMSysFunction;

/**
 *
 * @author Andrey
 */
public class NewObjOperatorCompiler extends AstCompiler{

   
   
    protected void addCommandSetFieldValue(ProgramBuilder programBuilder, Integer fieldNum, Integer value) throws CompilerException{
        programBuilder.addInstruction(VMCommands.Push, value, VarType.Integer);// fieldValue
        programBuilder.addInstruction(VMCommands.Push, fieldNum, VarType.Integer); // fieldNum
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.SetPtrField), VarType.Integer);
    }
    
    @Override
    public void compileChild(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        Token token =  node.getToken();
        String tokName = token.getTagName();
        switch(tokName){

             case "Id":
                 //ToDO: Check if class exists
                 MetaClassesInfo metaInfo = MetaClassesInfo.getInstance();
                 String className = token.getValue();
                 ClassInfo classInfo = metaInfo.getClassInfo(className); 
                
                 if(classInfo == null){
                     throw new CompilerException(String.format("Class %s not found", className));
                 }
               
           
                Integer fieldsSize = classInfo.getFieldsSize();
                 
                programBuilder.addInstruction(VMCommands.Push, fieldsSize, VarType.Integer);
                programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.MemAllocPtr), VarType.Integer);
                programBuilder.addInstruction(VMCommands.Dup, 0, VarType.Integer);
                
                //Set ClassID
                addCommandSetFieldValue(programBuilder, 0, classInfo.getCode());
                programBuilder.addInstruction(VMCommands.Dup, 0, VarType.Integer); //Dup for save Ptr
                
                //Set LinksCount
                addCommandSetFieldValue(programBuilder, 1, 0);
                programBuilder.addInstruction(VMCommands.Dup, 0, VarType.Integer); //Dup for save Ptr


                
             break;
          
        }
    }

    @Override
    public void compileRootPre(AstNode node, ProgramBuilder programBuilder) throws CompilerException {
       /* callFromLineNum = programBuilder.getLineCount();
        programBuilder.addInstruction(VMCommands.Jmp, callFromLineNum.toString());*/
    }

    @Override
    public void compileRootPost(AstNode node, ProgramBuilder programBuilder) throws CompilerException {
      // programBuilder.addInstruction(VMCommands.Jmp, programBuilder.getLineCount().toString(), VarType.Integer);
    }
    

   
}
