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
import program.builder.MetaClassesInfo;
import syntax.analyser.AstNode;
import program.builder.ProgramBuilder;
import virtual.machine.VMCommands;
import virtual.machine.VMSysFunction;

/**
 *
 * @author Andrey
 */
public class LetCompiler extends AstCompiler{
    
    protected boolean objLeftPart;
    protected String leftObjName;
    protected String className;
    protected String varName;
    protected AstNode rightPartNode;
    protected boolean RightPartIsNull;
    
    public void addRightPartCommands(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        String tokName = node.getToken().getTagName();
         String varName = node.getToken().getValue();
        switch(tokName){
             //Do Nothing with mathExpr
             //In math expr loadvar
            case  "Id":
                if (!programBuilder.isIsLocalContext()) {
                    programBuilder.addInstructionVarArg(VMCommands.Var_Load, varName, programBuilder.isIsLocalContext());
                } else {
                    programBuilder.addInstructionVarArg(VMCommands.Var_Load_Local, varName, programBuilder.isIsLocalContext());
                }
                
                //if(objLeftPart){
                String varClass = programBuilder.getVarDescription(varName).getClassName();
                if(varClass != null && varClass != ""){
                       this.addVarLoadCommand(varName, programBuilder); 
                     this.addCommandChangeFieldValue(programBuilder, varName, 1, 1 );
                }
                  
                //}
                
                break;
            case "Null":
                
                //Decrease links count to object
                this.addVarLoadCommand(this.varName, programBuilder); 
                this.addCommandChangeFieldValue(programBuilder, this.varName, 1, -1 );
                this.RightPartIsNull = true;
               
                
                break;
            case "Integer":            
                 programBuilder.addInstruction(VMCommands.Push, node.getToken().getValue(), VarType.Integer);
                 break;
            case "String":
                  programBuilder.addInstruction(VMCommands.Push_Addr, node.getToken().getValue(), VarType.String);
                 break;
            
             
         
         }
        rightPartNode = null;
    }
    protected void addCommandsLeftPartObj(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        String varName = node.getToken().getValue();
         //Put obj pointer to stack
        if (!programBuilder.isIsLocalContext()) {
            programBuilder.addInstructionVarArg(VMCommands.Var_Load, varName, programBuilder.isIsLocalContext());
        } else {
            programBuilder.addInstructionVarArg(VMCommands.Var_Load_Local, varName, programBuilder.isIsLocalContext());
        }
    }
    
    protected void addCommandsLeftPartObjField(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
       
        String fieldName = node.getToken().getValue();
    
        MetaClassesInfo metaInfo = MetaClassesInfo.getInstance();
        String objClass = programBuilder.getVarDescription(this.leftObjName).getClassName();
        ClassInfo classInfo =  metaInfo.getClassInfo(objClass);
        
        if(!classInfo.isFieldExists(fieldName)){
            throw new CompilerException(String.format("Object of type %s doesn't have a field %s", objClass, fieldName));
        }
        
        Integer fieldNum = metaInfo.getFieldCode(fieldName);
        
      
        //programBuilder.addInstruction(VMCommands.Push, fieldValueToken.getValue(), fieldValueToken.getVarType());// fieldValue
        
        programBuilder.addInstruction(VMCommands.Push, fieldNum, VarType.Integer); // fieldNum
        programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.SetPtrField), VarType.Integer);
    }
    
    protected void addCommandsLeftPartVar(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        String varName = node.getToken().getValue();
        if (!programBuilder.isIsLocalContext()) {
            programBuilder.addInstructionVarArg(VMCommands.Var_Put, varName, programBuilder.isIsLocalContext());
        } else {
            programBuilder.addInstructionVarArg(VMCommands.Var_Put_Local, varName, programBuilder.isIsLocalContext());
        }
    }
    @Override
    public void compileChild(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        String nodeName = "";
        if( node.getName() != null) nodeName = node.getName();
        
        switch(nodeName){
            case "LetStart":
                
                break;
            case "LeftObjName":
                objLeftPart = true;
                leftObjName = node.getToken().getValue();
                addCommandsLeftPartObj(node, programBuilder);
                if(rightPartNode!=null)addRightPartCommands(rightPartNode, programBuilder);
                break;
            case "RightPartExpr":
                //addRightPartCommands(node, programBuilder);
                objLeftPart = false;
                rightPartNode = node;
                RightPartIsNull = false;
                break;
            case "LeftVarName":
                varName = node.getToken().getValue();
                if(!this.className.equals("")){
                    
                    String varClass = programBuilder.getVarDescription(varName).getClassName();
                    if(!varClass.equals(this.className)){
                       throw new CompilerException(String.format("Variable %s is decalred as %s. But there is an attempt to assign it to instance of %s",
                               varName, varClass, this.className));
                    } 
                }
                
                
                if(objLeftPart){
                    addCommandsLeftPartObjField(node, programBuilder);
                } else{
                    if(rightPartNode!=null)addRightPartCommands(rightPartNode, programBuilder);
                    addCommandsLeftPartVar(node, programBuilder);
                }
                
                if(RightPartIsNull){
                    programBuilder.addInstruction(VMCommands.Push, -1, VarType.Integer);
                    addCommandsLeftPartVar(node, programBuilder);
                }
                break;
            
            /*default:
                System.err.println("Unknown part in Let compiler:" + node.getToken().getValue());*/
        }
    }
    public  void compileRootPre(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        
        AstNode rightExprNode = node.findChild("RightPartExpr");
        this.className = "";
        if(rightExprNode != null){
          AstNode classNameNode =  rightExprNode.findChild("ClassName");
          if(classNameNode != null) {
              this.className = classNameNode.getToken().getValue();
          }
        }
        
        
    }
   
    
}
