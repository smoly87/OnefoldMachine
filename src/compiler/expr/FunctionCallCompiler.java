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
import program.builder.FunctionDescription;
import program.builder.MetaClassesInfo;
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
public class FunctionCallCompiler extends AstCompiler{

    protected FunctionDescription funcDescr;
    //Used to return control to line whoes call function
    protected Integer callFromLineNum;
    protected TypesInfo typesInfo;
    protected int varNum ;
    /*At the begin of frame table is
    Link to caller(this)|Stack Position out of Frame|Return address
    */
   
    public FunctionCallCompiler(){
        typesInfo = TypesInfo.getInstance();
    }
    
    protected void createFrameStack(ProgramBuilder programBuilder) throws CompilerException{
        programBuilder.addInstruction(VMCommands.Push, regToStr(VmSysRegister.StackHeadPos), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Mov, VmSysRegister.FrameStackPos.ordinal(), VarType.Integer);
        
       /* programBuilder.addInstruction(VMCommands.Push, regToStr(VmSysRegister.StackHeadPos), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Mov, VmSysRegister.F1.ordinal(), VarType.Integer);*/

     
        //Headeres vars are local variables by theire essence
        Integer totalVarsCount = funcDescr.getArgsCount() + funcDescr.getLocalVarsCount() ;
        programBuilder.addInstruction(VMCommands.Push, totalVarsCount * VM.INT_SIZE, VarType.Integer); 
        //Create table of local variables addresses by index
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.MemAllocStack), VarType.Integer);

    }
    
    protected void declareAndSetHeadVar(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        Token token =  node.getToken();
        String tokName = token.getTagName();
        
        VarType argType = funcDescr.getArgDescr(varNum).getVarType();
        int typeSize = typesInfo.getTypeSize(argType);
        String typeSizeStr = Integer.toString(typeSize);

        programBuilder.addInstruction(VMCommands.Push, typeSizeStr, VarType.Integer);
        programBuilder.addInstruction(VMCommands.Var_Declare_Local, Integer.toString(varNum ), VarType.Integer);
        varNum++;
        
        switch(tokName){
             //Do Nothing with mathExpr
             //In math expr loadvar
             case "Id":
                 String varName = node.getToken().getValue();
                this.addVarLoadCommand(varName, programBuilder);
                
                break;
             case "Integer": case "Float":
                 programBuilder.addInstruction(VMCommands.Push, token.getValue(), argType);
                 //TODO: How to set value?
                 break;
        }
      

    }
    
    @Override
    public void compileChild(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        Token token =  node.getToken();
        String nodeName = "";
        if(node.getName() != null) nodeName = node.getName();
        switch(nodeName){
            case  "FunctionId":
             //ToDO: check in global context
              varNum = 0;
              funcDescr = MetaClassesInfo.getInstance().getFuncDescr(token.getValue());
              createFrameStack(programBuilder);
              break;
            case "EndCall":
                if(varNum < funcDescr.getArgsCount()) {
                    throw new CompilerException(String.format(
                            "It should be exactly %s params in call function %s. %s given", 
                            funcDescr.getArgsCount(), funcDescr.getFuncName(), varNum
                    ));
                }
                //Add number of line to return after function
                programBuilder.addInstruction(VMCommands.Push, 0, VarType.Integer); 
                int commandRet = programBuilder.getLineCount() - VM.COMMAND_SIZE;
                
                programBuilder.addInstruction(VMCommands.Push, VmSysRegister.F1.ordinal(), VarType.Integer);
                programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.SetRegister), VarType.Integer);
                
                
                programBuilder.addInstruction(VMCommands.Jmp, Integer.toString(funcDescr.getLineNumber()) , VarType.Integer, false);
                programBuilder.changeCommandArg(commandRet, programBuilder.getLineCount(), VarType.Integer); 
                break;
            case "Arg":
              declareAndSetHeadVar(node, programBuilder);
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
