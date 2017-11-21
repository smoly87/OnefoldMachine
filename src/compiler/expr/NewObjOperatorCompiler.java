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
import compiler.metadata.ClassInfo;
import compiler.metadata.MetaClassesInfo;
import program.builder.ProgramBuilder;
import syntax.analyser.AstNode;
import virtual.machine.VMCommands;
import virtual.machine.VMSysFunction;
import virtual.machine.memory.Memory;

/**
 *
 * @author Andrey
 */
public class NewObjOperatorCompiler extends AstCompiler{

    public NewObjOperatorCompiler(ProgramBuilder programBuilder) {
        super(programBuilder);
    }

   
   
   
    
    @Override
    public void compileChild(AstNode node) throws CompilerException{
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
                //Set object flag
                programBuilder.addInstruction(VMCommands.Push, Memory.GC_FLAG_OBJ, VarType.Integer);
                programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.MemAllocPtr), VarType.Integer);
                programBuilder.addInstruction(VMCommands.Dup, 0, VarType.Integer);
                
                //Set ClassID
                addCommandSetFieldValue( 0, classInfo.getCode());
                programBuilder.addInstruction(VMCommands.Dup, 0, VarType.Integer); //Dup for save Ptr
                
                //Set LinksCount
                addCommandSetFieldValue( 1, 1);
               // programBuilder.addInstruction(VMCommands.Dup, 0, VarType.Integer); //Dup for save Ptr


                
             break;
          
        }
    }


   
}
