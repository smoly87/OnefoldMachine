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
public class FunctionCompiler extends AstCompiler{

    protected String funcName;
    protected int declaredVarsCount;
    protected FunctionDescription funcDescr ;
    protected int funStartCommandNum;
    protected TypesInfo typesInfo;
    protected int totalVarsTableSizeInstr;
    
    public FunctionCompiler(){
        typesInfo = TypesInfo.getInstance();
    }
    
    protected void createFrameStack(ProgramBuilder programBuilder) throws CompilerException{
        programBuilder.addInstruction(VMCommands.Push, regToStr(VmSysRegister.StackHeadPos), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Mov, VmSysRegister.FrameStackPos.ordinal(), VarType.Integer);
        
       /* programBuilder.addInstruction(VMCommands.Push, regToStr(VmSysRegister.StackHeadPos), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Mov, VmSysRegister.F1.ordinal(), VarType.Integer);*/

     
        //Headeres vars are local variables by theire essence
        //Integer totalVarsCount = funcDescr.getArgsCount() + funcDescr.getLocalVarsCount() ; totalVarsCount * VM.INT_SIZE
        totalVarsTableSizeInstr = programBuilder.addInstruction(VMCommands.Push,0, VarType.Integer); 
        //Create table of local variables addresses by index
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.MemAllocStack), VarType.Integer);
        
        
        programBuilder.addInstruction(VMCommands.Push, VmSysRegister.FrameStackTableStart.ordinal(), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.SetRegister), VarType.Integer);

    }
     
    protected void processVarDescription(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        Token token = node.getToken();
        declaredVarsCount++;
        funcDescr.addArgDecription(token.getValue(), token.getVarType());
        
        programBuilder.addLocalVar(token.getValue(), token.getVarType());
       /* int varInd = programBuilder.getLocalVarCode(token.getValue());
        
        int typeSize = typesInfo.getTypeSize(token.getVarType());
        String typeSizeStr = Integer.toString(typeSize);
        programBuilder.addInstruction(VMCommands.Push, typeSizeStr, VarType.Integer);
        programBuilder.addInstruction(VMCommands.Var_Declare_Local, Integer.toString(varInd), VarType.Integer);*/
        
       
    }
    
    protected void addThisVar(ProgramBuilder programBuilder) throws CompilerException {
        
        
        funcDescr.addArgDecription("this", VarType.Integer);

        programBuilder.addLocalVar("this", VarType.Integer);
        declaredVarsCount++;
    }
    
    protected void processReturnStatement(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        Token token = node.getToken(); 
        switch(token.getTagName()){
            case "Id":
                addVarLoadCommand(token.getValue(), programBuilder);
                programBuilder.addInstruction(VMCommands.Push, VmSysRegister.T2.ordinal(), VarType.Integer);
                programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.SetRegister), VarType.Integer);
                break;
            //All other types Int, Float..    
            default:
                VarType type =  VarType.valueOf(token.getTagName()) ;
                programBuilder.addInstruction(VMCommands.Push, token.getValue(), type);
                programBuilder.addInstruction(VMCommands.Push, VmSysRegister.T2.ordinal(), VarType.Integer);
                programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.SetRegister), VarType.Integer);
        }
    }
    
    
 
    
   
    
    @Override
    public void compileChild(AstNode node, ProgramBuilder programBuilder) throws CompilerException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.  
        switch(node.getName()){
            case "FunctionId":  
                Token token = node.getToken();
                
                programBuilder.setIsLocalContext(true);
                programBuilder.clearLocalVars();
                declaredVarsCount = 0;
                this.funcName = token.getValue();
                /*/Start point is nop, this is protection from empty functions 
                and it relieves from recount numeration of first Line*/
                programBuilder.addInstruction(VMCommands.NOP, 0, VarType.Integer);
                
                funcDescr = new FunctionDescription(this.funcName, programBuilder.commandsSize());
                createFrameStack(programBuilder);
                //addThisVar(programBuilder);

                
                break;
            case "VarDescription":
                processVarDescription(node, programBuilder);
                break;  
            case "ReturnStatement":

               
                
                processReturnStatement(node, programBuilder);
                
                
                //Return to call address
                //programBuilder.addInstruction(VMCommands.Push, VmSysRegister.F1.ordinal(), VarType.Integer);
               // programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.GetRegister), VarType.Integer);
                programBuilder.addInstructionVarArg(VMCommands.Var_Load_Local, "__ReturnAddress",  true);
                programBuilder.addInstruction(VMCommands.Push, VmSysRegister.T1.ordinal(), VarType.Integer);
                programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.SetRegister), VarType.Integer);
                
                /*programBuilder.addInstruction(VMCommands.Push, VmSysRegister.ProgOffsetAddr.ordinal(), VarType.Integer);
                programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.GetRegister), VarType.Integer);             
                programBuilder.addInstruction(VMCommands.Add, 0, VarType.Integer);*/
                
                //If address is null it will be got from stack  
                programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.DeleteFrame), VarType.Integer);
                
                programBuilder.addInstruction(VMCommands.Push, VmSysRegister.T2.ordinal(), VarType.Integer);
                programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.GetRegister), VarType.Integer);
               
               
                programBuilder.addInstruction(VMCommands.Push, VmSysRegister.T1.ordinal(), VarType.Integer);
                programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.GetRegister), VarType.Integer);
                programBuilder.addInstruction(VMCommands.Jmp, 0, VarType.Integer);
                
                programBuilder.addInstruction(VMCommands.NOP, 0, VarType.Integer);
                
                //TODO: Is it realy need to convert constatnt in similay cases ?
              //  programBuilder.changeCommandArgByNum(totalVarsTableSizeInstr, funcDescr.getTotalVarsCount() * VM.INT_SIZE, VarType.Integer, true);
                
                funcDescr.setEndLineNumber(programBuilder.commandsSize() );
                processVariables(programBuilder);
                MetaClassesInfo.getInstance().addFunction(funcName, funcDescr);
                
                break;
        }
        //this.callSubscribers(node, programBuilder);
    }
    @Override
    public void compileRootPre(AstNode node, ProgramBuilder programBuilder) {
       
         VarCompiler varCompiler  = (VarCompiler)this.getCompiler("Var");
         //if(node.getName() == "VarsBlock") processVarsBlock(node, programBuilder);
    }
    

    public void processVariables( ProgramBuilder programBuilder) {
         programBuilder.setIsLocalContext(false);
         programBuilder.clearLocalVars();
         VarCompiler varCompiler = (VarCompiler)this.getCompiler("Var");
         //int totalVarsCount = declaredVarsCount + varCompiler.getLocalVarsCount();
         funcDescr.setLocalVarsCount(varCompiler.getLocalVarsCount());
         varCompiler.clearLocalVarsCount();
         
        
        
    }
    
}
