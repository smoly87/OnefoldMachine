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
import compiler.metadata.VarDescription;
import syntax.analyser.AstNode;
import program.builder.ProgramBuilder;
import syntax.analyser.CompilerUndeclaredVariableException;
import virtual.machine.VMCommands;

/**
 *
 * @author Andrey
 */
public class MathExprCompiler extends AstCompiler{

    public MathExprCompiler(ProgramBuilder programBuilder) {
        super(programBuilder);
    }


    public void compileOperChild(AstNode node, boolean transformToFloat) throws CompilerException{
        String tagName = node.getToken().getTagName();
        switch(tagName){
            case "Id":
                String varName = node.getToken().getValue();
                this.addVarLoadCommand(varName);
                break;
            case "Integer": case "Float":
                String constValue = node.getToken().getValue();
                VarType type = transformToFloat ? VarType.Float :  VarType.valueOf(tagName);
                programBuilder.addInstruction(VMCommands.Push, constValue, type);
                break;
            default:
        }
    }
    
    protected VarType concludeNodeType(AstNode node) throws CompilerException{
        VarType nodeType = null;
        String tagName = node.getToken().getTagName();
        switch(tagName){
            case "Id":
                String varName = node.getToken().getValue();
                VarDescription varDescr = programBuilder.getVarDescription(varName);
                nodeType = varDescr.getType();
                break;
            case "Integer": case "Float":
                nodeType = VarType.valueOf(tagName);
                break;
            default:
        }
        
        return nodeType;
    }
    
    
    protected boolean hasNestedFloats(AstNode node) throws CompilerException{
        for(AstNode childNode: node.getChildNodes()){
            boolean  res = false; 
            if(childNode.getToken().getTagName().equals("Operator")){
                res = hasNestedFloats(childNode);
            } else{
                res = (concludeNodeType(childNode)  == VarType.Float);
            }
            if(res) return true;
        }
        
        return false; 
    } 
    
    protected void processOperatorNode(AstNode node ) throws CompilerException{
        
        boolean transformToFloat = hasNestedFloats(node);
        
        for(AstNode childNode: node.getChildNodes()){
            if(childNode.getToken().getTagName().equals("Operator")){
                processOperatorNode(childNode);
            } else{
                compileOperChild(childNode, transformToFloat);
            }
            
        }
        Token token = node.getToken();
        node.getToken().setVarType(transformToFloat ? VarType.Float : VarType.Integer);
        VarType operType = node.getToken().getVarType();
        
        switch(token.getName()){
                case "+":
                    programBuilder.addInstruction(operType == VarType.Integer ? VMCommands.IAdd: VMCommands.FAdd);
                    break;
                case "-":
                    programBuilder.addInstruction(operType == VarType.Integer ? VMCommands.ISub: VMCommands.FSub);
                    break;
                case "*":
                    programBuilder.addInstruction(operType == VarType.Integer ? VMCommands.IMul: VMCommands.FMul);
                    break;
        }
    }
    
    @Override
    public void compileRootPre(AstNode node) throws CompilerException {
        processOperatorNode(node);
    }
    
}
