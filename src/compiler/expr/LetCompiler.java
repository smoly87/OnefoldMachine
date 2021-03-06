/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.expr;

import common.Token;
import common.VarType;
import compiler.AstCompiler;
import compiler.CompilerSubscriber;
import compiler.exception.CompilerException;
import compiler.expr.utils.LetCompilerParams;
import compiler.metadata.ClassInfo;
import compiler.metadata.MetaClassesInfo;
import compiler.metadata.VarDescription;
import syntax.analyser.AstNode;
import program.builder.ProgramBuilder;
import virtual.machine.VMCommands;
import virtual.machine.VMSysFunction;

/**
 *
 * @author Andrey
 */
public class LetCompiler extends AstCompiler {
    
 
    // TODO: It's need to figure out why it so buggy
    protected String className = "";
    protected LetCompilerParams paramsObj;
    protected VarType rightPartType;
    
    public LetCompiler(ProgramBuilder programBuilder){
       super(programBuilder);
    }
    
    public void addRightPartCommands(AstNode node ) throws CompilerException{
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
                       this.addVarLoadCommand(varName); 
                     this.addCommandChangeFieldValue( varName, 1, 1 );
                }
                  
                //}
                
                break;
            case "Null":
                
                //Decrease links count to object
                this.addVarLoadCommand(paramsObj.getVarName()); 
                this.addCommandChangeFieldValue( paramsObj.getVarName(), 1, -1 );
                paramsObj.setRightPartIsNull();
               
                
                break;
            case "Integer":            
                 programBuilder.addInstruction(VMCommands.Push, node.getToken().getValue(), VarType.Integer);
                 break;
            case "Float":            
                 programBuilder.addInstruction(VMCommands.Push, node.getToken().getValue(), VarType.Float);
                 break;     
            case "String":
                  programBuilder.addInstruction(VMCommands.Push_Addr, node.getToken().getValue(), VarType.String);
                 break;
            
             
         
         }
        paramsObj.setRightPartNode(null) ;
    }
    protected void addCommandsLeftPartObj(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
        String varName = node.getToken().getValue();
         //Put obj pointer to stack
        if (!programBuilder.isIsLocalContext()) {
            programBuilder.addInstructionVarArg(VMCommands.Var_Load, varName, programBuilder.isIsLocalContext());
        } else {
            programBuilder.addInstructionVarArg(VMCommands.Var_Load_Local, varName, programBuilder.isIsLocalContext());
           //  programBuilder.addInstruction(VMCommands.Invoke_Sys_Function, sysFuncToStr(VMSysFunction.DeferPtrValue), VarType.Integer);
        }
    }
    
    protected void addCommandsLeftPartObjField(AstNode node, ProgramBuilder programBuilder) throws CompilerException{
       
        String fieldName = node.getToken().getValue();
    
        MetaClassesInfo metaInfo = MetaClassesInfo.getInstance();
         
        String objClass = "";
        ClassInfo classInfo = null;
        
        if(!paramsObj.getLeftObjName().equals("this") ){
            objClass = programBuilder.getVarDescription(paramsObj.getLeftObjName()).getClassName();
            classInfo = metaInfo.getClassInfo(objClass);

            if (classInfo != null && !classInfo.isFieldExists(fieldName)) {
                throw new CompilerException(String.format("Object of type %s doesn't have a field %s", objClass, fieldName));
            }
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
    public void compileChild(AstNode node) throws CompilerException{
        String nodeName = "";
        if( node.getName() != null) nodeName = node.getName();
        
        switch(nodeName){
            case "LetStart":
                 paramsObj = new LetCompilerParams();
                break;
            case "LeftObjName":
                paramsObj.setIsObjLeftPart(true);
                paramsObj.setLeftObjName(node.getToken().getValue());
                addCommandsLeftPartObj(node, programBuilder);
                if(paramsObj.getRightPartNode() !=null){
                    addRightPartCommands(paramsObj.getRightPartNode());
                }
                break;
            case "RightPartExpr":                
                paramsObj.setRightPartNode(node); 
                break;
            case "LeftVarName":
                String varName = node.getToken().getValue();
                paramsObj.setVarName(varName);
                VarDescription varDescr = programBuilder.getVarDescription(paramsObj.isObjLeftPart() ? paramsObj.getLeftObjName() : varName);
                
                if(paramsObj.isObjLeftPart()){
                   if(!this.className.equals("")){ 
                      
                      String varClass = varDescr.getClassName();
                      if(varClass != null && !varClass.equals(this.className)){
                        throw new CompilerException(String.format("Variable %s is decalred as %s. But there is an attempt to assign it to instance of %s",
                               varName, varClass, this.className));
                      } 
                   }
                } else{
                    if(varDescr.getType() != rightPartType){
                        if( rightPartType == VarType.Float && varDescr.getType() == VarType.Integer ){
                            throw new CompilerException(String.format("Try to assing float value to variable %s of Integer type" , varName));
                        }
                    }
                }
                
                
                if(paramsObj.isObjLeftPart()){
                    addCommandsLeftPartObjField(node, programBuilder);
                } else{
                    if(paramsObj.getRightPartNode() !=null){
                        addRightPartCommands(paramsObj.getRightPartNode());
                    }
                    addCommandsLeftPartVar(node, programBuilder);
                }
                
                if(paramsObj.getRightPartIsNull()){
                    programBuilder.addInstruction(VMCommands.Push, -1, VarType.Integer);
                    addCommandsLeftPartVar(node, programBuilder);
                }
                break;
            
            /*default:
                System.err.println("Unknown part in Let compiler:" + node.getToken().getValue());*/            
            /*default:
                System.err.println("Unknown part in Let compiler:" + node.getToken().getValue());*/
        }
    }
    @Override
    public  void compileRootPre(AstNode node) throws CompilerException{
        //paramsObj = new LetCompilerParams();
        AstNode rightExprNode = node.findChild("RightPartExpr");
       
        if(rightExprNode != null){
          AstNode classNameNode =  rightExprNode.findChild("ClassName");
          if(classNameNode != null) {
             this.className = classNameNode.getToken().getValue(); 
          }
        } else{
            //This place is produced after MathParser Type conclusion.
            if(node.getName().equals("RightPartExpr")) rightPartType =  node.getToken().getVarType();
        }
        
        
    }
    
  
    
}
