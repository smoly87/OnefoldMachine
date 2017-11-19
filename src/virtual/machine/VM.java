/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package virtual.machine;

import virtual.machine.exception.VmStackEmptyPopException;
import virtual.machine.exception.VmExecutionExeption;
import types.TypesInfo;
import common.VarType;
import virtual.machine.memory.Memory;
import virtual.machine.memory.MemoryHeap;
import virtual.machine.memory.MemoryManager;
import virtual.machine.memory.MemoryProgram;
import virtual.machine.memory.MemoryStack;
import virtual.machine.memory.VmSysRegister;

/**
 *
 * @author Andrey
 */
public class VM {
    protected Instructions instructionsService;
    protected DataBinConvertor binConvertorService;
  
    
    
    public static  int ADDR_SIZE = 4;
    public static  int INT_SIZE = 4;
    public static int COMMAND_SIZE =  1 + INT_SIZE; 
    
    protected MemoryManager memoryManager;
    protected int pos;
    protected int offset;
    protected Program program;
    protected VmCodeDebuger codeDebuger; 
    protected VmProgramMetaInfo progMetaInfo;
    
    protected int[] varAddr;
    protected TypesInfo typesInfo;
    
    protected boolean firstF1 = false;
    
    public enum METHOD_ADDR_TYPE{Start, StartBody};
    
    public VM() throws VmExecutionExeption{
        this.binConvertorService =  DataBinConvertor.getInstance();
        this.instructionsService = Instructions.getInstance();
        this.memoryManager = new MemoryManager(binConvertorService);
        this.typesInfo = TypesInfo.getInstance();
    }
    
    protected MemoryHeap getMemHeap(){
        MemoryHeap heap = this.memoryManager.getMemHeap();
        return heap;
    }
    
  
    protected VarType getVarTypeMnemonic(Byte code){
       return VarType.values()[code];
    }
        
    protected int stackPopInt() throws VmStackEmptyPopException, VmExecutionExeption{
        return binConvertorService.bytesToInt(memoryManager.getMemStack().pop(), 0);
    }
    
    
    public void allocateProgram() throws VmExecutionExeption{
        
        
        
        int startInstrsInd = program.readHeader(VmExeHeader.InstructionsStart);
        
        
        memoryManager.allocateProgramInstructions(program.getData(), startInstrsInd);
        
        VMProgramAllocator progAllocator = new VMProgramAllocator(memoryManager, program);
        progAllocator.allocateProgram();
        
        progMetaInfo = new VmProgramMetaInfo(memoryManager, progAllocator.getAddrTables());
        
        
        boolean isDebug = true;
        codeDebuger = new VmCodeDebuger(program, memoryManager, progMetaInfo, progAllocator.getAddrTables());
        codeDebuger.setDebugMode(isDebug);
    }
  
    public void run(Program program) throws VmExecutionExeption{
     // try{ 
        this.program = program;
        allocateProgram();
        VmSysFunctions vmSysFunctions = new VmSysFunctions(memoryManager, progMetaInfo);
        boolean isDebug =  codeDebuger.isDebugMode();
        int progStartPoint = program.readHeader(VmExeHeader.ProgramStartPoint);
        int startAddr = progStartPoint + memoryManager.getSysRegister(VmSysRegister.ProgOffsetAddr);
       
        int progStart = memoryManager.getSysRegister(VmSysRegister.ProgOffsetAddr);
        if(isDebug)codeDebuger.addLog("Entry Point is " + startAddr);
        codeDebuger.showFullCode();
        
        int addr;
        Byte[] arg1Byte, arg2Byte;
        int arg1, arg2;
        int operRes;
        
        boolean haltFlag = false;

        MemoryProgram memProg =  new MemoryProgram(memoryManager, startAddr);
        
        MemoryStack memStack = this.memoryManager.getMemStack();
        MemoryHeap memHeap = this.memoryManager.getMemHeap();
        //Continue in jmp
        String[] codeComments = codeDebuger.loadCodeComments();
        memProg.jump(startAddr);
        
        
        Byte[] value;
        int intVal = -1;
        
        while (!haltFlag) {
                VMCommands command = memProg.getCommand();
                addr = memProg.getCommandAddrArg();
                if(isDebug)codeDebuger.addLog(String.format("Command %s at line %s Addr: %s", command.toString(),memProg.getAddr(), addr ));
                
                switch (command) {
                    case Push_Addr_Value:
                       
                        value = memoryManager.getPtrByteValue(addr);
                        
                   
                        memStack.push(value);
                       // memStack.push(868);
                       if(isDebug)codeDebuger.addLog(String.format("Stack push value: %s addr: %s stackhead: %s", binConvertorService.bytesToInt(value, 0),addr, memoryManager.getSysRegister(VmSysRegister.StackHeadPos)));
                      /* System.err.println("Stack extr value: " + stackPopInt());
                        System.err.println("Stack ext2r value: " + stackPopInt());*/
                        break;
                    case Push_Addr:
                       // value = memoryManager.getPtrByteValue(addr);
                       
                        memStack.push(binConvertorService.toBin(addr));
                        if(isDebug)codeDebuger.addLog(String.format("Stack push addr: %s stack head:", addr, memoryManager.getSysRegister(VmSysRegister.StackHeadPos)));
                        break;    
                    case Pop:
                        memStack.pop();
                        break;
                    case Add:
                        arg1 = stackPopInt();
                        arg2 = stackPopInt();
                        operRes = arg1 + arg2;
                        memStack.push(binConvertorService.toBin(operRes));
                        break;
                    case Sub:
                        arg1 = stackPopInt();
                        arg2 = stackPopInt();
                        operRes = arg1 - arg2;
                        memStack.push(binConvertorService.toBin(operRes));
                        break;    
                    case Mul:
                        arg1 = stackPopInt();
                        arg2 = stackPopInt();
                        operRes = arg1 * arg2;
                        memStack.push(binConvertorService.toBin(operRes));
                        break;
                    case Var_Put:        
                        memStack.pop(addr);
                        break;
                   /* case Var_Load:
                        value = memoryManager.getPtrValue(addr);
                        memStack.push(value);
                        break;*/
                    case Jmp:
                        if(addr == 0){
                            addr = stackPopInt() ;
                            if(isDebug)codeDebuger.addLog("Jump addr from stack");
                        }
                        if(isDebug)codeDebuger.addLog("Jump to addr:" + addr);
                        memProg.jump(addr + progStart);
                        continue;
                    case JmpIf:
                        value = memStack.pop();
                        if (binConvertorService.bytesToBool(value)) {
                            if (addr == 0) {
                                addr = stackPopInt();
                                if(isDebug)codeDebuger.addLog("Jump IfNot addr from stack");
                            }
                            if(isDebug)codeDebuger.addLog("Jump to addr:" + addr);
                            memProg.jump(addr+ progStart);
                            continue;
                        }
                        break;
                    case JmpIfNot:
                        value = memStack.pop();
                        if (!binConvertorService.bytesToBool(value)) {
                            if (addr == 0) {
                                addr = stackPopInt() ;
                                if(isDebug)codeDebuger.addLog("Jump IfNot addr from stack");
                            }
                            if(isDebug)codeDebuger.addLog("Jump to addr:" + addr);
                            memProg.jump(addr+ progStart);
                            continue;
                        }
                        break;             
                    case Invoke_Sys_Function:
                        vmSysFunctions.callSysFunc(addr);
                        break;
                    case Halt:
                        haltFlag = true;
                        //if(isDebug)codeDebuger.addLog("Program finished with Halt Command"); 
                        break;
                    case Var_Declare_Local_Def_value: 
                        intVal = stackPopInt();
                         int varInd = memoryManager.getIntPtrValue(addr) ;//binConvertorService.bytesToInt(addr, 0) ; 
                         int varSize = stackPopInt();
                         memStack.push(new Byte[varSize]);
                         int  locVarAddr = memoryManager.getSysRegister(VmSysRegister.StackHeadPos);
                         int  frameStart = memoryManager.getSysRegister(VmSysRegister.FrameStackTableStart)  ;
                        
                         int  frameHeadersPosEnd = frameStart + Memory.PTR_HEADERS_SIZE + VM.INT_SIZE;
                        
                         memStack.putValue( frameHeadersPosEnd + varInd * INT_SIZE, locVarAddr);
                         break;
                    case Var_Put_Local:
                        /*Local var table is allocated at the begin of stack address space
                        Table Format: int|int|...
                        This table is table of pointers
                        */
                        varInd = addr ;
                        frameStart = memoryManager.getSysRegister(VmSysRegister.FrameStackTableStart) ;
                        frameHeadersPosEnd = frameStart + Memory.PTR_HEADERS_SIZE + VM.INT_SIZE;
                        int varAddr = memStack.getIntValue(frameHeadersPosEnd + varInd * INT_SIZE);
                  
                        if(varAddr > 0){
                           memStack.pop(varAddr, INT_SIZE);  
                        } else{
                            intVal = stackPopInt();
                            if(isDebug)codeDebuger.addLog("Put_Local_Var ERR: " + intVal);
                        }
                        
                        //value =
                        intVal =  memStack.getPtrIntField(varAddr, INT_SIZE);
                        if(isDebug)codeDebuger.addLog("Put Local_Var: " + intVal);
                        break;
                    case Var_Load_Local:
                        varInd = addr ;
                        //if(isDebug)codeDebuger.addLog(String.format("Local var load: varInd: %s : %s " ,varInd, intVal ));
                        frameStart = memoryManager.getSysRegister(VmSysRegister.FrameStackTableStart) ;
                        frameHeadersPosEnd = frameStart + Memory.PTR_HEADERS_SIZE + VM.INT_SIZE;
                        varAddr = memStack.getIntValue(frameHeadersPosEnd + varInd * INT_SIZE);
                       intVal = memStack.getPtrIntField(varAddr, INT_SIZE);
                      //  value = memoryManager.getPtrByteValue(varAddr,INT_SIZE);
                        // intVal = binConvertorService.bytesToInt(value, 0);
                      // 
                       // if(isDebug)codeDebuger.addLog(String.format("Local var load: varInd: %s : %s " ,varInd, intVal ));
                        memStack.push(binConvertorService.integerToByte(intVal));
                        break;    
                    case Mov:
                        int regInd = memoryManager.getIntPtrValue(addr);
                        int srcReg = stackPopInt();
                        int val = memoryManager.getSysRegister(VmSysRegister.values()[srcReg]);
                        memoryManager.setSysRegister(VmSysRegister.values()[regInd], val);
                        
                        if(isDebug)codeDebuger.addLog(String.format("###Set value %s for registre %s from %s", val,VmSysRegister.values()[regInd].toString(),VmSysRegister.values()[srcReg].toString()  ));
                        break;
                    case NOP:
                        if(addr > 0){
                            if(isDebug)codeDebuger.addLog("###" + codeComments[addr - 1]); 
                        }
                         
                        break;
                    case Dup:
                        value = memStack.pop();
                        memStack.push(value);
                        memStack.push(value);
                        break;
                     case CmpEqual:
                        arg2 = stackPopInt();
                        arg1 = stackPopInt();
                        boolean operResBool = arg1 == arg2;
                        memStack.push(binConvertorService.toBin(operResBool));
                        break;    
                    case CmpMore:
                        arg2 = stackPopInt();
                        arg1 = stackPopInt();
                        operResBool = arg1 > arg2;
                        memStack.push(binConvertorService.toBin(operResBool));
                        break;
                     case CmpLess:
                        arg2 = stackPopInt();
                        arg1 = stackPopInt();
                        operResBool = arg1 < arg2;
                        memStack.push(binConvertorService.toBin(operResBool));
                        break;    
                    default:
                        codeDebuger.addError("Unprocessed command: " + command.toString());
               
                }
                memProg.next();
            }
          codeDebuger.varValuesDebug();
        /*} catch(Exception e){
            System.err.println("VM execution Error: " + e.getMessage());
        }*/
    }
}
