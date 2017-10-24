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
    
    public void addSubscriber(CompilerSubscriber subscriber){
        subscribers.add(subscriber);
    }
    
    public void removeSubscriber(){
    }
    
    protected void callSubscribers(AstNode node, ProgramBuilder programBuilder){
        for (CompilerSubscriber subscriber : subscribers) {
            subscriber.nodeProcessEvent(node, programBuilder);
        }
    }
    
    public AstCompiler(){
        subscribers = new LinkedList<>();
        compilersFactory = CompilersFactory.getInstance();
    }
    
    protected AstCompiler getCompiler(String compilerName){
        return compilersFactory.getElement(compilerName);
    }
    
    public void compileChild(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        if(subscribers.size() > 0) callSubscribers(node, programBuilder);
    }
    
    public  void compileRootPost(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        if(subscribers.size() > 0) callSubscribers(node, programBuilder);
        
    }
    public  void compileRootPre(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        if(subscribers.size() > 0) callSubscribers(node, programBuilder);
    }
    
    protected String regToStr(VmSysRegister reg){
        return  Integer.toString(reg.ordinal());
    }
    
    protected String sysFuncToStr(VMSysFunction sysFunc){
        return Integer.toString(sysFunc.ordinal());
    }
    
    protected void addVarLoadCommand(String varName,  ProgramBuilder programBuilder) throws CompilerException{
        if (programBuilder.isVarExists(varName)) {
            programBuilder.addInstructionVarArg(VMCommands.Var_Load, varName, programBuilder.isIsLocalContext());
            return;
        }
        
        if (programBuilder.isLocalVariableExists(varName)) {
            programBuilder.addInstructionVarArg(VMCommands.Var_Load, varName, programBuilder.isIsLocalContext());
            return;
        }

        throw new CompilerException("Undeclared variable: " + varName);
    }
    
     
 
}
