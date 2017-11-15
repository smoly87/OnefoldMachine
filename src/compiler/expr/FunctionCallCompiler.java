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
    protected Boolean isFirstArg = true;
    protected int totalVarsCount;
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
        //Integer totalVarsCount = funcDescr.getArgsCount() + funcDescr.getLocalVarsCount() ; totalVarsCount * VM.INT_SIZE
        int mockVal = funcDescr.getTotalVarsCount();
        programBuilder.addInstruction(VMCommands.Push, mockVal * VM.INT_SIZE, VarType.Integer); 
        //Create table of local variables addresses by index
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.MemAllocStack), VarType.Integer);
        
        
        //programBuilder.addInstruction(VMCommands.Push, VmSysRegister.FrameStackTableStart.ordinal(), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Push, VmSysRegister.C1.ordinal(), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.SetRegister), VarType.Integer);

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
          
        addCommandGetRegistrerLoad(VmSysRegister.T3, programBuilder);
        addCommandGetRegistrerLoad(VmSysRegister.T5, programBuilder);
        /*programBuilder.addInstruction(VMCommands.Push, regToStr(VmSysRegister.T3), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.GetRegister), VarType.Integer);

        programBuilder.addInstruction(VMCommands.Push, regToStr(VmSysRegister.T5), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.GetRegister), VarType.Integer);*/
        
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
    
    protected void addCommandGetRegistrerLoad(VmSysRegister src, ProgramBuilder programBuilder) throws CompilerException{
        programBuilder.addInstruction(VMCommands.Push, regToStr(src), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.GetRegister), VarType.Integer);

    }
    
    protected void addCommandSaveRegister(VmSysRegister src, VmSysRegister dst, ProgramBuilder programBuilder) throws CompilerException{
        programBuilder.addInstruction(VMCommands.Push, regToStr(src), VarType.Integer);
        programBuilder.addInstruction(VMCommands.Mov, dst.ordinal(), VarType.Integer);
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
                 /*   programBuilder.addInstruction(VMCommands.Push, regToStr(VmSysRegister.StackHeadPos), VarType.Integer);
                  programBuilder.addInstruction(VMCommands.Mov, VmSysRegister.T3.ordinal(), VarType.Integer);*/
              isFirstArg = true;
              programBuilder.addComment("Save proc registres");
              addCommandSaveRegister(VmSysRegister.StackHeadPos, VmSysRegister.T3, programBuilder);
              addCommandSaveRegister(VmSysRegister.FrameStackTableStart, VmSysRegister.T5, programBuilder);
              
              
              
              break;
            case "ObjName":
                objMethod = true;
                objName = token.getValue();
                
                 /* programBuilder.addInstruction(VMCommands.Push, regToStr(VmSysRegister.StackHeadPos), VarType.Integer);
                  programBuilder.addInstruction(VMCommands.Mov, VmSysRegister.T3.ordinal(), VarType.Integer);

                  programBuilder.addInstruction(VMCommands.Push, regToStr(VmSysRegister.FrameStackTableStart), VarType.Integer);
                  programBuilder.addInstruction(VMCommands.Mov, VmSysRegister.T5.ordinal(), VarType.Integer);*/
                
             
                
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
                programBuilder.addComment("Create stack in new way");
                createFrameStack(programBuilder);
                addRetPlaceHolder(programBuilder);
                addCommandsSaveState(programBuilder);// Local var with index 1 is always FramePosition
                addThisValue(objName, programBuilder);
             
                
                break;
          

            case "StartArgs":
                startArgsLineNum = programBuilder.addInstruction(VMCommands.NOP);
                
             
                
                break;
            case "Arg":
             //   programBuilder.addComment("Add call param");
             /* if(isFirstArg){
                   isFirstArg = false;

              }  */
              addCallParamValue(node, programBuilder);
              break;
            case "EndCall":
                //For ArrangeFunc params function.
                //TODO: Here ought be restore
               // addCommandSaveRegister(VmSysRegister.FrameStackTableStart, VmSysRegister.C2, programBuilder);
               // addCommandSaveRegister(VmSysRegister.FrameStackTableStart, VmSysRegister.C2, programBuilder);
                programBuilder.addInstruction(VMCommands.Push, funcDescr.getArgsCount(), VarType.Integer);
                addCommandSaveRegister(VmSysRegister.C1, VmSysRegister.FrameStackTableStart, programBuilder);
                if(objMethod){
                 /* addCommandSaveRegister(VmSysRegister.C1, VmSysRegister.StackHeadPos, programBuilder);*/
                  ;
                  
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
