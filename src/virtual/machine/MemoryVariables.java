/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine;

import common.VarType;
import java.util.Iterator;
import java.util.function.Consumer;
import sun.text.normalizer.ICUBinary;
import virtual.machine.memory.Memory;

/**
 *
 * @author Andrey
 */
public class MemoryVariables implements Iterator<MemoryVariable>{

    protected Memory memory;
    protected int startPos;
    protected int endPos;
    protected int curAddr;

    public MemoryVariables(Memory memory, int startPos, int endPos) {
        this.memory = memory;
        this.startPos = startPos;
        this.endPos = endPos;
        this.curAddr = startPos;
    }
    
   
    
    @Override
    public boolean hasNext() {
        return curAddr < endPos;
    }

    @Override
    public MemoryVariable next() {
       byte varTypeNum = memory.getValue(curAddr, 1)[0];
       VarType varType =  VarType.values()[varTypeNum];
       
       MemoryVariable res = new MemoryVariable(curAddr, varType);
       int curBlockSize = memory.getPtrSizeWithHeaders(curAddr);
       curAddr += curBlockSize;
       return res;
    }

    @Override
    public void forEachRemaining(Consumer<? super MemoryVariable> action) {
        Iterator.super.forEachRemaining(action); //To change body of generated methods, choose Tools | Templates.
    }
    
}
