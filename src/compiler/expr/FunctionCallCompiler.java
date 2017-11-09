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
    protected boolean objMethod = false;
    protected String objName;
    
    protected int retLineNum;
    /*At the begin of frame table is
    Link to caller(this)|Stack Position out of Frame|Return address
    */
   
    public FunctionCallCompiler(){
        typesInfo = TypesInfo.getInstance();
    }
    
   
    
   /* protected void decalareReturnAddressVariable(String callerName, ProgramBuilder programBuilder) throws CompilerException{
        programBuilder.addInstruction(VMCommands.Push, TypesInfo.getInstance().getTypeSize(VarType.Integer), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Var_Declare_Local_Def_value, Integer.toString(varNum ), VarType.Integer);

        varNum++;
    }
    
    protected void decalareThisVariable(String callerName, ProgramBuilder programBuilder) throws CompilerException{
        //programBuilder.addInstruction(VMCommands.Push, TypesInfo.getInstance().getTypeSize(VarType.Integer), VarType.Integer);
        this.addVarLoadCommand(callerName, programBuilder);
        //programBuilder.addInstruction(VMCommands.Push, varNum, VarType.Integer);
        programBuilder.addInstruction(VMCommands.Push, 1, VarType.Integer);
        programBuilder.addInstruction(VMCommands.Var_Declare_Local, Integer.toString(varNum ), VarType.Integer);
        //this.addVarLoadCommand(callerName, programBuilder);
       //programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.DeferPtrValue), VarType.Integer);
        varNum++;
    }
    */
    protected void addCallParamValue(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        Token token =  node.getToken();
        String tokName = token.getTagName();
        
        
       VarType argType = funcDescr.getArgDescr(varNum).getVarType();
                
        switch(tokName){
             //Do Nothing with mathExpr
             //In math expr loadvar
             case "Id":
                 String varName = node.getToken().getValue();
                this.addVarLoadCommand(varName, programBuilder);
                //String classFlag = argType == VarType.ClassPtr ? "True" : "False";
                //programBuilder.addInstruction(VMCommands.Push, argType == VarType.ClassPtr ? 1:0 , VarType.Integer);
                
                
                break;
             default:
                 
                 programBuilder.addInstruction(VMCommands.Push, token.getValue(), argType);
                 //programBuilder.addInstruction(VMCommands.Push, 0 , VarType.Integer);
                 //TODO: How to set value?
                 break;
            
        }
        //programBuilder.addInstruction(VMCommands.Push, varNum, VarType.Integer);
         programBuilder.addInstruction(VMCommands.Var_Declare_Local, Integer.toString(varNum ), VarType.Integer);
        
        
      varNum++;

    }
    /*
    protected void decalareReturnAddress(ProgramBuilder programBuilder) throws CompilerException{
        //Line to Return
        //retLineNum = programBuilder.addInstruction(VMCommands.Push, 0, VarType.Integer);
        //Add object flag :0, hense not object
        programBuilder.addLocalVar("__ReturnAddress", VarType.Integer);
        programBuilder.addInstruction(VMCommands.Push, TypesInfo.getInstance().getTypeSize(VarType.Integer), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Var_Declare_Local_Def_value, Integer.toString(varNum ), VarType.Integer);
        varNum++;
    }*/
    
    protected void addCommandsSaveState(ProgramBuilder programBuilder) throws CompilerException{
          
              
        programBuilder.addInstruction(VMCommands.Push, VmSysRegister.StackHeadPos.ordinal(), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.GetRegister), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Push, 0 , VarType.Integer);
        programBuilder.addInstruction(VMCommands.Var_Put_Local, Integer.toString(varNum), VarType.Integer);
         varNum++;
        //programBuilder.addInstruction(VMCommands.Var_Put_Local, "__ReturnAddress", VarType.Integer);
        //programBuilder.addInstruction(VMCommands.Push, getSy, VarType.Integer, false);
    }
    
    protected void addRetPlaceHolder(ProgramBuilder programBuilder) throws CompilerException{
        retLineNum = programBuilder.addInstruction(VMCommands.Push, "0", VarType.Integer, false);
        
        /*programBuilder.addInstruction(VMCommands.Push, VmSysRegister.ProgOffsetAddr.ordinal(), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.GetRegister), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Add);*/
                
        programBuilder.addInstruction(VMCommands.Var_Put_Local, Integer.toString(varNum), VarType.Integer);
        varNum++;
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
              objMethod = false;
              objName = null;
              funcDescr = MetaClassesInfo.getInstance().getFuncDescr(token.getValue());
              

              addRetPlaceHolder(programBuilder); // Local var with index 0 is always Return address
              addCommandsSaveState(programBuilder);// Local var with index 1 is always FramePosition
              
              break;
            case "ObjName":
                objMethod = true;
                objName = token.getValue();
                addCallParamValue(node, programBuilder);// Local var with index 2 is always link to this
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
               /* int commandRet = programBuilder.commandsSize() - VM.COMMAND_SIZE;
                
                programBuilder.addInstruction(VMCommands.Push, VmSysRegister.F1.ordinal(), VarType.Integer);
                programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.SetRegister), VarType.Integer);*/
                
                if(objMethod){
                    //String varClass = programBuilder.getVarDescription(objName).getClassName();
                    Integer methodCode = MetaClassesInfo.getInstance().getMethodCode(funcDescr.getFuncName());
                    
                    programBuilder.addInstruction(VMCommands.Push, methodCode.toString() , VarType.Integer);
                    this.addVarLoadCommand(objName, programBuilder);
              
                    programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.GetVirtualFuncAddr), VarType.Integer);
                    
                    addRetPlaceHolder(programBuilder);
                    programBuilder.addInstruction(VMCommands.Jmp, "0" , VarType.Integer, false);
                } else{
                    addRetPlaceHolder(programBuilder);
                    programBuilder.addInstruction(VMCommands.Jmp, Integer.toString(funcDescr.getLineNumber()) , VarType.Integer, false);
                }
                
                int  commandsSize = programBuilder.addInstruction(VMCommands.NOP);
                //programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.DeleteFrame), VarType.Integer);
                //programBuilder.changeCommandArg(retLineNum, programBuilder.commandsSize(), VarType.Integer);
                programBuilder.changeCommandArgByNum(retLineNum, commandsSize * VM.COMMAND_SIZE, VarType.Integer, true);
                //programBuilder.clearLocalVars();
                break;
            case "Arg":
              addCallParamValue(node, programBuilder);
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
