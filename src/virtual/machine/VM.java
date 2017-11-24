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
import main.ShellCommand;
import syntax.analyser.parser.ProgramBuildingStage;
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
public class VM extends ProgramBuildingStage{
    protected Instructions instructionsService;
    protected DataBinConvertor binConvertorService;
  
    public static final int ADDR_SIZE = 4;
    public static final int INT_SIZE = 4;
    public static final int COMMAND_SIZE =  1 + INT_SIZE; 
    
    protected MemoryManager memoryManager;
    protected int pos;
    protected int offset;
    protected Program program;
    protected VmCodeDebuger codeDebuger; 
    protected VmProgramMetaInfo progMetaInfo;
    
    protected int[] varAddr;
    protected TypesInfo typesInfo;
    
    protected ShellCommand shellCommand;
    
    public enum METHOD_ADDR_TYPE{Start, StartBody};
    
    protected String[] codeComments;
    protected  VmSysFunctions vmSysFunctions;
    
    public VM(ShellCommand command) throws VmExecutionExeption{
        this.binConvertorService =  DataBinConvertor.getInstance();
        this.instructionsService = Instructions.getInstance();
        
        this.typesInfo = TypesInfo.getInstance();
        this.shellCommand = command;
    }
    
    protected MemoryHeap getMemHeap(){
        MemoryHeap heap = this.memoryManager.getMemHeap();
        return heap;
    }
    
  
    protected VarType getVarTypeMnemonic(Byte code){
       return VarType.values()[code];
    }
        
    protected int stackPopInt() throws VmStackEmptyPopException, VmExecutionExeption{
        return memoryManager.stackPopInt();
    }
    
    protected float stackPopFloat() throws VmStackEmptyPopException, VmExecutionExeption{
        return memoryManager.stackPopFloat();
    }
    
    public void allocateProgram(Program program) throws VmExecutionExeption{
        this.program = program;
        this.memoryManager = new MemoryManager(binConvertorService);
        
        int startInstrsInd = program.readHeader(VmExeHeader.InstructionsStart);
        
        
        memoryManager.allocateProgramInstructions(program.getData(), startInstrsInd);
        
        VMProgramAllocator progAllocator = new VMProgramAllocator(memoryManager, program);
        progAllocator.allocateProgram();
        
        progMetaInfo = new VmProgramMetaInfo(memoryManager, progAllocator.getAddrTables());
        vmSysFunctions = new VmSysFunctions(memoryManager, progMetaInfo);
        
        
        codeDebuger = new VmCodeDebuger(program, memoryManager, progMetaInfo, progAllocator.getAddrTables());
        codeDebuger.setDebugMode(shellCommand.isOptionExists("debug") || shellCommand.isOptionExists("debug_execution"));
        codeComments = codeDebuger.loadCodeComments();
    }
  
    public void run() throws VmExecutionExeption{
     // try{ 
        
        
        boolean isDebug =  codeDebuger.isDebugMode();
        int progStartPoint = program.readHeader(VmExeHeader.ProgramStartPoint);
        int startAddr = progStartPoint + memoryManager.getSysRegister(VmSysRegister.ProgOffsetAddr);
       
        int progStart = memoryManager.getSysRegister(VmSysRegister.ProgOffsetAddr);
        if(isDebug)codeDebuger.addLog("Entry Point is " + startAddr);
        if(isDebug)codeDebuger.showFullCode();
        
        int addr;
        Byte[] arg1Byte, arg2Byte;
        int arg1, arg2;
        int operRes;
        
        boolean haltFlag = false;

        MemoryProgram memProg =  new MemoryProgram(memoryManager, startAddr);
        
        MemoryStack memStack = this.memoryManager.getMemStack();
        MemoryHeap memHeap = this.memoryManager.getMemHeap();
        //Continue in jmp
       
        memProg.jump(startAddr);
        
        
        Byte[] value;
        int intVal = -1;
        
        while (!haltFlag) {
                VMCommands command = memProg.getCommand();
                addr = memProg.getCommandAddrArg();
                if(hasSubscribers)this.callSubscribers("COMMAND_PROCESS", String.format("Command %s at line %s Addr: %s", command.toString(),memProg.getAddr(), addr ));
                
                switch (command) {
                    case Push_Addr_Value:        
                       value = memoryManager.getPtrByteValue(addr);
                       memStack.push(value);
                       break;
                    case Push_Addr:
                        memStack.push(binConvertorService.toBin(addr));
                        break;    
                    case Pop:
                        memStack.pop();
                        break;
                    case IAdd:
                        arg1 = stackPopInt();
                        arg2 = stackPopInt();
                        operRes = arg1 + arg2;
                        memStack.push(binConvertorService.toBin(operRes));
                        break;
                    case ISub:
                        arg1 = stackPopInt();
                        arg2 = stackPopInt();
                        operRes = arg1 - arg2;
                        memStack.push(binConvertorService.toBin(operRes));
                        break;    
                    case IMul:
                        arg1 = stackPopInt();
                        arg2 = stackPopInt();
                        operRes = arg1 * arg2;
                        memStack.push(binConvertorService.toBin(operRes));
                        break;
                    case FAdd:
                        float farg1 = stackPopFloat();
                        float farg2 = stackPopFloat();
                        float foperRes = farg1 + farg2;
                        memStack.push(binConvertorService.toBin(foperRes));
                        break;    
                    case Var_Put:        
                        memStack.pop(addr);
                        break;
   
                    case Jmp:
                        if(addr == 0){
                            addr = stackPopInt() ;  
                        }
                        memProg.jump(addr + progStart);
                        continue;
                    case JmpIf:
                        value = memStack.pop();
                        if (binConvertorService.bytesToBool(value)) {
                            if (addr == 0) {
                                addr = stackPopInt();           
                            }
                            memProg.jump(addr+ progStart);
                            continue;
                        }
                        break;
                    case JmpIfNot:
                        value = memStack.pop();
                        if (!binConvertorService.bytesToBool(value)) {
                            if (addr == 0) {
                                addr = stackPopInt() ;
                            }
                            memProg.jump(addr+ progStart);
                            continue;
                        }
                        break;             
                    case Invoke_Sys_Function:
                        vmSysFunctions.callSysFunc(addr);
                        break;
                    case Halt:
                        haltFlag = true;
                        break;
                    case Var_Declare_Local_Def_value: 
                         intVal = stackPopInt();
                         int varInd = memoryManager.getIntPtrValue(addr) ; 
                         int varSize = stackPopInt();
                         memStack.push(new Byte[varSize]);
                         int  locVarAddr = memoryManager.getSysRegister(VmSysRegister.StackHeadPos);
                         memStack.putValue(locVarAddr, (byte)intVal);
                         
                         int  frameStart = memoryManager.getSysRegister(VmSysRegister.FrameStackTableStart)  ;
                        
                         int  frameHeadersPosEnd = frameStart + Memory.PTR_HEADERS_SIZE + VM.INT_SIZE;
                        
                         memStack.putValue( frameHeadersPosEnd + varInd * INT_SIZE, locVarAddr);
                         break;
                    case Var_Put_Local:
                        /*Local var table is allocated at the top of stack address space for current frame
                        Table Format: int|int|...
                        This table is table of pointers
                        */
                        varInd = addr ;
                        frameStart = memoryManager.getSysRegister(VmSysRegister.FrameStackTableStart) ;
                        frameHeadersPosEnd = frameStart + Memory.PTR_HEADERS_SIZE + VM.INT_SIZE;
                        int varAddr = memStack.getIntValue(frameHeadersPosEnd + varInd * INT_SIZE);
                        
                        memStack.pop(varAddr, INT_SIZE);
                        
                        break;
                    case Var_Load_Local:
                        varInd = addr ;

                        frameStart = memoryManager.getSysRegister(VmSysRegister.FrameStackTableStart) ;
                        frameHeadersPosEnd = frameStart + Memory.PTR_HEADERS_SIZE + VM.INT_SIZE;
                        varAddr = memStack.getIntValue(frameHeadersPosEnd + varInd * INT_SIZE);
 
                        value = memoryManager.getPtrByteValue(varAddr,INT_SIZE);
                        memStack.push(value);
    
                        break;    
                    case Mov:
                        int regInd = memoryManager.getIntPtrValue(addr);
                        int srcReg = stackPopInt();
                        int val = memoryManager.getSysRegister(VmSysRegister.values()[srcReg]);
                        memoryManager.setSysRegister(VmSysRegister.values()[regInd], val);
                        
                        break;
                    case NOP:
                        if(addr > 0){
                            if(hasSubscribers)this.callSubscribers("CODE_COMMENT", "###" + codeComments[addr - 1]); 
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
                        throw new VmExecutionExeption("Unknown command: " + command.toString());
               
                }
                memProg.next();
            }
          if(shellCommand.isOptionExists("debug") || shellCommand.isOptionExists("debug_execution_summary")) codeDebuger.varValuesDebug();
        /*} catch(Exception e){
            System.err.println("VM execution Error: " + e.getMessage());
        }*/
    }
}
