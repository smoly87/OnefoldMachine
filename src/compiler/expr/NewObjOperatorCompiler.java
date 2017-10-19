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

   
   
    
    @Override
    public void compileChild(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        Token token =  node.getToken();
        String tokName = token.getTagName();
        switch(tokName){

             case "Id":
                 //ToDO: Check if class exists
                 MetaClassesInfo metaInfo = MetaClassesInfo.getInstance();
                 String className = token.getValue();
                 ClassInfo classInfo = null; 
                
                 if(classInfo == null){
                     throw new CompilerException(String.format("Class %s not found", className));
                 }
                
                
                //Class|LinksCount
                Integer fieldsSize = classInfo.getFieldsSize() + 2 * VM.INT_SIZE;
                 
                programBuilder.addInstruction(VMCommands.Push, fieldsSize, VarType.Integer);
                programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.MemAllocPtr), VarType.Integer);
                
                programBuilder.addInstruction(VMCommands.Push,classInfo.getCode(), VarType.Integer);// fieldValue
                programBuilder.addInstruction(VMCommands.Push, 0, VarType.Integer); // fieldNum
                programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.SetPtrField), VarType.Integer);


                
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
