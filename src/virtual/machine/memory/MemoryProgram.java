/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine.memory;

import virtual.machine.VMCommands;
import virtual.machine.VM;

/**
 *
 * @author Andrey
 */
public class MemoryProgram {
    protected Memory memory;
    protected VMCommands command;
    protected int addr;
    protected int pos;
    
    public MemoryProgram(Memory memory, int offset){
        this.memory = memory;
        addr = offset;
    }
    
    public int getAddr(){
        return addr;
    }
    
    public void jump(int addr){
        this.addr = addr;
    }
    
    public void next(){
        //One byte command and arg
        addr += 1 + VM.INT_SIZE ;
    }
    
    public VMCommands getCommand(){
       int commandCode =  memory.getValue(addr);
       return VMCommands.values()[commandCode];
    }
    
    public int getCommandAddrArg(){
        //One byte on command
        return memory.getIntValue(addr + 1);
    }
}
