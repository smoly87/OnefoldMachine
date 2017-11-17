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
import compiler.metadata.FunctionDescription;
import compiler.metadata.MetaClassesInfo;
import program.builder.ProgramBuilder;
import syntax.analyser.AstNode;
import types.TypesInfo;
import virtual.machine.VM;
import virtual.machine.VMCommands;
import virtual.machine.VMSysFunction;
import virtual.machine.memory.VmSysRegister;

/**
 *
 * @author Andrey
 */
public class SysFunctionCallCompiler extends AstCompiler{

    protected FunctionDescription funcDescr;
    //Used to return control to line whoes call function
    protected Integer callFromLineNum;
    protected TypesInfo typesInfo;
    protected int varNum ;
    /*At the begin of frame table is
    Link to caller(this)|Stack Position out of Frame|Return address
    */
   
    public SysFunctionCallCompiler(){
        typesInfo = TypesInfo.getInstance();
    }

    
    @Override
    public void compileChild(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        Token token =  node.getToken();
        String nodeName = "";
        if(node.getName() != null) nodeName = node.getName();
        switch(nodeName){
            case "Arg":
              if(token.getTagName().equals("Id")){
                   programBuilder.addInstructionVarArg(VMCommands.Var_Load, token.getValue(), false);
              } else{
                   programBuilder.addInstruction(VMCommands.Push, token.getValue(), VarType.valueOf(token.getTagName()));
              }  
                System.err.println(String.format("-> %s, %s", token.getValue(), token.getTagName()));
              break; 
            case  "FunctionId":
              try{   
                  programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.valueOf(token.getValue())), VarType.Integer);
              }catch(IllegalArgumentException exception ){
                  throw new CompilerException("Call unknown function: " + token.getValue());
              }
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
