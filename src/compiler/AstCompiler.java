/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package compiler;

import common.VarType;
import compiler.exception.CompilerException;
import java.util.LinkedList;
import syntax.analyser.AstNode;
import program.builder.ProgramBuilder;
import compiler.metadata.VarDescription;
import syntax.analyser.CompilersFactory;
import virtual.machine.VMCommands;
import virtual.machine.VMSysFunction;
import virtual.machine.memory.VmSysRegister;

/**
 *
 * @author Andrey
 */
public abstract class AstCompiler {
    protected CompilersFactory compilersFactory;
    protected LinkedList<CompilerSubscriber> subscribers;
    protected boolean enabled = true;
    protected ProgramBuilder programBuilder;
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
 
    public AstCompiler(ProgramBuilder programBuilder){
        this.programBuilder = programBuilder;
        subscribers = new LinkedList<>();
        compilersFactory = CompilersFactory.getInstance();
        setEnabled(true);
    }
    
    protected AstCompiler getCompiler(String compilerName){
        return compilersFactory.getElement(compilerName);
    }
    
    public void compileChild(AstNode node) throws CompilerException{
        
    }
    
    public  void compileRootPost(AstNode node) throws CompilerException{
      
        
    }
    public  void compileRootPre(AstNode node) throws CompilerException{
       
    }
    
    protected String regToStr(VmSysRegister reg){
        return  Integer.toString(reg.ordinal());
    }
    
    protected String sysFuncToStr(VMSysFunction sysFunc){
        return Integer.toString(sysFunc.ordinal());
    }
    
    protected void addVarLoadCommand(String varName) throws CompilerException{
        
        
        if (programBuilder.isLocalVariableExists(varName)) {
            if(varName.equals("this")){
                programBuilder.addInstruction(VMCommands.Push, regToStr(VmSysRegister.T4), VarType.Integer);
                programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.GetRegister), VarType.Integer);
            } else{
                programBuilder.addInstructionVarArg(VMCommands.Var_Load_Local, varName, true);
            }
            
      
            return;
        }
        if (programBuilder.isVarExists(varName)) {
           /* VarDescription verDescr = programBuilder.getVarDescription(varName);
            verDescr.getType() == VarType.ClassPtr ? VMCommands.Push_Addr:*/
            programBuilder.addInstructionVarArg( VMCommands.Var_Load, varName, false);
            return;
        }

        throw new CompilerException(">>Undeclared variable: " + varName);
    }
    
    protected void addCommandSetFieldValue(Integer fieldNum, Integer value) throws CompilerException{
        programBuilder.addInstruction(VMCommands.Push, value, VarType.Integer);// fieldValue
        programBuilder.addInstruction(VMCommands.Push, fieldNum, VarType.Integer); // fieldNum
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.SetPtrField), VarType.Integer);
    }
    
     protected void addCommandChangeFieldValue( String varName,Integer fieldNum, Integer step) throws CompilerException{
        
        addVarLoadCommand(varName);
        
        programBuilder.addInstruction(VMCommands.Dup);
        programBuilder.addInstruction(VMCommands.Push, fieldNum, VarType.Integer); 
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.GetPtrField), VarType.Integer);
        
        //Increment pass ptr field value as argument of addition operation
        programBuilder.addInstruction(VMCommands.Push, step, VarType.Integer);
        programBuilder.addInstruction(VMCommands.IAdd, 0, VarType.Integer);
        
        programBuilder.addInstruction(VMCommands.Push, fieldNum, VarType.Integer); 
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.SetPtrField), VarType.Integer);
    }
 
 
}
