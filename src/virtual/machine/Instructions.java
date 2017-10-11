/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package virtual.machine;

import java.util.HashMap;

/**
 *
 * @author Andrey
 */
public class Instructions {
   /*Var_Load, Var_Put, Jmp, Push, 
   Pop,  Add, Mul*/
    protected static Instructions instance ;
    private Instructions(){
        instructionsMap = new HashMap<>();
        codesMap = new HashMap<>();
        initInstructions();
    }
   
    public  static Instructions getInstance(){
        if(instance == null) {
            instance = new Instructions();
        }
        return instance;
    }
    protected HashMap<String, Instruction> instructionsMap;
    protected HashMap<Byte, Instruction> codesMap;
    
    protected void initInstructions(){
        addInstruction("Push", 1);
        addInstruction("Pop", 1);
        addInstruction("Jmp", 1);
        
        addInstruction("Var_Put", 1);
        addInstruction("Var_Load", 1);
        
        addInstruction("Add", 0);
        addInstruction("Mul", 0);
    }
    
    protected void addInstruction(String instructionName, int argsCount){
        byte code = (byte)instructionsMap.size();
        Instruction instruction = new Instruction(instructionName, code, (byte)argsCount);
        
        instructionsMap.put(instructionName, instruction);
        codesMap.put(code, instruction);
    }
    
    public Instruction getInstructionByCode(byte code){
        return codesMap.get(code);
    }
    
    public Byte getCode(String instruction ){
        return instructionsMap.get(instruction).getCode();
    }
    
}
