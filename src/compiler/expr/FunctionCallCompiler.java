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
import program.builder.FuncSignatureBuilder;
import program.builder.FunctionDescription;
import program.builder.MetaClassesInfo;
import program.builder.ProgramBuilder;
import program.builder.VarDescription;
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
    protected String methodName;
    
    protected Integer retLineNum;
    protected Integer startArgsLineNum;
    protected  String argSignature ;
  
    /*At the begin of frame table is
    Link to caller(this)|Stack Position out of Frame|Return address
    */
    public FunctionCallCompiler(){
        typesInfo = TypesInfo.getInstance();

    }
    
   
    protected void addCallParamValue(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        Token token =  node.getToken();
        String tokName = token.getTagName();
        
        
      //funcDescr.getArgDescr(varNum).getVarType();
        //signBuilder.addArgType(argType);
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
                 String strType = node.getToken().getTagName() ;
                 VarType argType = VarType.valueOf(strType);
                 programBuilder.addInstruction(VMCommands.Push, token.getValue(), argType);
                 //programBuilder.addInstruction(VMCommands.Push, 0 , VarType.Integer);
                 //TODO: How to set value?
                 break;
            
        }
        //programBuilder.addInstruction(VMCommands.Push, varNum, VarType.Integer);
         
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
    
    /***
     * This function Restore to Params of Stack, which are StackHead And StackFrameTableStart
     * @param programBuilder
     * @throws CompilerException 
     */
    
    protected void addCommandsSaveState(ProgramBuilder programBuilder) throws CompilerException{
          
        programBuilder.addInstruction(VMCommands.Push, regToStr(VmSysRegister.T3), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.GetRegister), VarType.Integer);

        programBuilder.addInstruction(VMCommands.Push, regToStr(VmSysRegister.T5), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.GetRegister), VarType.Integer);
        /* programBuilder.addInstruction(VMCommands.Push, VmSysRegister.StackHeadPos.ordinal(), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.GetRegister), VarType.Integer);*/
        //programBuilder.addInstruction(VMCommands.Push, 0 , VarType.Integer);
       // programBuilder.addInstruction(VMCommands.Var_Put_Local, Integer.toString(varNum), VarType.Integer);
         varNum++;
        //programBuilder.addInstruction(VMCommands.Var_Put_Local, "__ReturnAddress", VarType.Integer);
        //programBuilder.addInstruction(VMCommands.Push, getSy, VarType.Integer, false);
    }
    
    protected void addRetPlaceHolder(ProgramBuilder programBuilder) throws CompilerException{
        //retLineNum = programBuilder.addInstruction(VMCommands.NOP, "0", VarType.Integer, false);
        
        retLineNum = programBuilder.addInstruction(VMCommands.Push, 0, VarType.Integer);
       // programBuilder.addComment("Ret placeholder");
        varNum++;
        /*programBuilder.addInstruction(VMCommands.Push, VmSysRegister.ProgOffsetAddr.ordinal(), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.GetRegister), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Add);*/
       // varNum++;        
        //programBuilder.addInstruction(VMCommands.Var_Put_Local,"0", VarType.Integer);

    }
    
    public void addCommandGetVirtualMethodAddr(VM.METHOD_ADDR_TYPE addrType, ProgramBuilder programBuilder) throws CompilerException{
        programBuilder.addInstruction(VMCommands.Push, addrType.ordinal(), VarType.Integer);

        Integer methodCode = funcDescr.getCode();//MetaClassesInfo.getInstance().getMethodCode(funcDescr.getFuncName());

        programBuilder.addInstruction(VMCommands.Push, methodCode.toString(), VarType.Integer);
        this.addVarLoadCommand(objName, programBuilder);

        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.GetVirtualFuncAddr), VarType.Integer);
               
    }
    
    protected void addThisValue(String varName, ProgramBuilder programBuilder) throws CompilerException{

       /* Integer ind = programBuilder.getVarCode(varName);
        programBuilder.addInstruction(VMCommands.Push,ind ,VarType.Integer);*/
       if(!objName.equals("this")){
             this.addVarLoadCommand(objName, programBuilder);
             programBuilder.addInstruction(VMCommands.Dup);
             programBuilder.addInstruction(VMCommands.Push, regToStr(VmSysRegister.T4), VarType.Integer);
             programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.SetRegister), VarType.Integer);
       } else{
             programBuilder.addInstruction(VMCommands.Push, regToStr(VmSysRegister.T4), VarType.Integer);
             programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.GetRegister), VarType.Integer);

       }
   
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
              //funcDescr = MetaClassesInfo.getInstance().getFuncDescr(token.getValue());
              methodName = token.getValue();
                    programBuilder.addInstruction(VMCommands.Push, regToStr(VmSysRegister.StackHeadPos), VarType.Integer);
                  programBuilder.addInstruction(VMCommands.Mov, VmSysRegister.T3.ordinal(), VarType.Integer);

              
              break;
            case "ObjName":
                objMethod = true;
                objName = token.getValue();
                
                  programBuilder.addInstruction(VMCommands.Push, regToStr(VmSysRegister.StackHeadPos), VarType.Integer);
                  programBuilder.addInstruction(VMCommands.Mov, VmSysRegister.T3.ordinal(), VarType.Integer);

                  programBuilder.addInstruction(VMCommands.Push, regToStr(VmSysRegister.FrameStackTableStart), VarType.Integer);
                  programBuilder.addInstruction(VMCommands.Mov, VmSysRegister.T5.ordinal(), VarType.Integer);

                //programBuilder.addComment("Puth $this");
               // 
              //  addCallParamValue(node, programBuilder);// Local var with index 2 is always link to this
                 //addCallParamValue(node, programBuilder);
                //programBuilder.addComment("Puth En");
                ClassInfo classInfo;
                String className;
                if(!objName.equals("this") ){
                    VarDescription varDescr =  programBuilder.getVarDescription(objName);
                    classInfo = MetaClassesInfo.getInstance().getClassInfo(varDescr.getClassName()) ;
                    className = varDescr.getClassName();
                } else{
                    ClassCompiler classCompiler = (ClassCompiler)this.getCompiler("Class");
                    
                    classInfo = classCompiler.getClassInfo();
                    className = classInfo.getClassName();
                }
              
                if(!classInfo.isMethodExists(methodName, argSignature)){
                    throw new CompilerException(String.format("There is no realization for method %s in class %s with signature %s", methodName, className, argSignature));
                } else{
                    funcDescr = classInfo.getMethodDescription(methodName, argSignature);
                }
                //TODO: Load signature 
                
                
                break;
            case "EndCall":
               
                //Add number of line to return after function
               Integer retLineToFill = programBuilder.addInstruction(VMCommands.Push, 0, VarType.Integer); 
               /* int commandRet = programBuilder.commandsSize() - VM.COMMAND_SIZE;
                
                programBuilder.addInstruction(VMCommands.Push, VmSysRegister.F1.ordinal(), VarType.Integer);
                programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.SetRegister), VarType.Integer);*/
                
                if(objMethod){
                    //String varClass = programBuilder.getVarDescription(objName).getClassName();
                    programBuilder.addComment("Add toFunctStart");
                    //Set address type - entry point to function
                    addCommandGetVirtualMethodAddr(VM.METHOD_ADDR_TYPE.Start, programBuilder);
                    //addRetPlaceHolder(programBuilder);
                    programBuilder.addInstruction(VMCommands.Jmp, "0" , VarType.Integer, false);
                } else{
                    //addRetPlaceHolder(programBuilder);
                    programBuilder.addInstruction(VMCommands.Jmp, Integer.toString(funcDescr.getLineNumber()) , VarType.Integer, true);
                }
                
                int  commandsSize = programBuilder.addInstruction(VMCommands.NOP);
                //programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.DeleteFrame), VarType.Integer);
               // programBuilder.changeCommandArgByNum(retLineNum, programBuilder.commandsSize(), VarType.Integer, true);
               programBuilder.changeCommandArgByNum(retLineToFill, programBuilder.commandsSize(), VarType.Integer, true);
              // programBuilder.addComment("After ret first part");
                //programBuilder.clearLocalVars();
               programBuilder.addComment("Local var fill");
               //    addRetPlaceHolder(programBuilder); // Local var with index 0 is always Return address
              retLineNum = programBuilder.addInstruction(VMCommands.Push, 0, VarType.Integer);
                //  programBuilder.addInstruction(VMCommands.Push, programBuilder.commandsSize(), VarType.Integer);
            
               addCommandsSaveState(programBuilder);// Local var with index 1 is always FramePosition
              

                 programBuilder.addComment("Local This fill");
               addThisValue(objName, programBuilder);
                
                break;
            case "StartArgs":
                startArgsLineNum = programBuilder.addInstruction(VMCommands.NOP);
                break;
            case "Arg":
              addCallParamValue(node, programBuilder);
              break;
            case "AfterArgsBlock":
                //For ArrangeFunc params function.
                programBuilder.addInstruction(VMCommands.Push, funcDescr.getArgsCount(), VarType.Integer);
                if(objMethod){
                  addCommandGetVirtualMethodAddr(VM.METHOD_ADDR_TYPE.StartBody, programBuilder);
                  programBuilder.addInstruction(VMCommands.Jmp, "0" , VarType.Integer, false);
                  programBuilder.addInstruction(VMCommands.NOP);
               //programBuilder.changeCommandArgByNum(retLineNum, programBuilder.commandsSize(), VarType.Integer, true);
                }else{
                    throw new CompilerException("Funtion call in global context not releazed yet.");
                }  
                programBuilder.changeCommandArgByNum(retLineNum, programBuilder.commandsSize(), VarType.Integer, true);
                programBuilder.addInstruction(VMCommands.NOP);
                break;
        }
        
        
        
       
    }

    @Override
    public void compileRootPre(AstNode node, ProgramBuilder programBuilder) throws CompilerException {
       AstNode argsNode = node.findChild("ArgsBlock");
       if(argsNode == null) return;
       FuncSignatureBuilder signBuilder = new FuncSignatureBuilder();
       
       //TODO: Figure out if it's possible to auto count such params
       signBuilder.addArgType(VarType.Integer); // AutoGenerated Return Address
       signBuilder.addArgType(VarType.Integer); // AutoGenerated Stack Position Address
       signBuilder.addArgType(VarType.Integer); 
       signBuilder.addArgType(VarType.Integer); // AutoGenerated link to this
       
       for(AstNode curNode: argsNode.getChildNodes()){
           AstNode argNode = curNode.findChild("Arg");
           String argTypeStr = argNode.getToken().getTag().getName();
           //TODO: Load info about variable from global or local context
           switch(argTypeStr){
               case "Id":
                   //throw new CompilerException("Need to releaze Loading info about variable from global or local context");
                   VarDescription varDesc = programBuilder.getVarDescription(argNode.getToken().getValue());
                   signBuilder.addArgType(varDesc.getType());
                   break;
               default:
                   signBuilder.addArgType(VarType.valueOf(argTypeStr));
           }
       }
       argSignature = signBuilder.getSignature();
      
    }

    @Override
    public void compileRootPost(AstNode node, ProgramBuilder programBuilder) throws CompilerException {
      // programBuilder.addInstruction(VMCommands.Jmp, programBuilder.getLineCount().toString(), VarType.Integer);
    }
    
 
   
}
