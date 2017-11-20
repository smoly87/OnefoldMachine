/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine;

import virtual.machine.exception.VmExecutionExeption;
import common.VarType;
import java.io.UnsupportedEncodingException;
import program.builder.BinaryReader;
import types.TypeString;
import types.TypesInfo;
import virtual.machine.memory.MemoryHeap;
import virtual.machine.memory.MemoryManager;
import virtual.machine.memory.MemoryProgram;
import virtual.machine.memory.MemoryStack;
import virtual.machine.memory.VmSysRegister;

/**
 *
 * @author Andrey
 */
public class VmCodeDebuger {
    protected Program program;
    protected MemoryManager memoryManager;
    protected VmProgramMetaInfo progMetaInfo;
    protected VMAddrTables addrTables;
    protected boolean debugMode;

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
    
    public VmCodeDebuger(Program program, MemoryManager memoryManager, VmProgramMetaInfo progMetaInfo, VMAddrTables addrTables){
        this.program =  program;
        this.memoryManager =  memoryManager;
        this.progMetaInfo = progMetaInfo;
        this.addrTables = addrTables;
    }
    
    public String[] loadCodeComments() { 
       try{
         int commentsCnt = program.readHeader(VmExeHeader.CommentsCount);
         int commentsStart = program.readHeader(VmExeHeader.CommentsStart);
         int commentsEnd = program.readHeader(VmExeHeader.InstructionsStart);
         
         TypeString convertor =(TypeString) TypesInfo.getInstance().getConvertor(VarType.String);
         
         if(commentsCnt > 0){
             String res[] = new String[commentsCnt];
             
             BinaryReader binReader = new BinaryReader(program.getData());
             binReader.setCurPos(commentsStart);

             int k = 0;
             while (binReader.getCurPos() < commentsEnd) {
                 int commentSize = binReader.readIntAndNext();
                 Byte[] binComment = binReader.readAndNextBytes(commentSize);
                 res[k] = convertor.getValue(binComment);
                 k++;
             }
             return res;
         } else{
             return null;
         }
       
          
       } catch(UnsupportedEncodingException e){
           System.err.println("Can't load code comments: " + e.getMessage());
       }
       return null;
    }
    
  
    
    public void showFullCode(){
        int startInstrsInd = program.readHeader(VmExeHeader.InstructionsStart);
        int progStartPoint = program.readHeader(VmExeHeader.ProgramStartPoint);
        int startAddr =  memoryManager.getSysRegister(VmSysRegister.ProgOffsetAddr);
     
        MemoryProgram memProg =  new MemoryProgram(memoryManager, startAddr);
        
        MemoryStack memStack = this.memoryManager.getMemStack();
        MemoryHeap memHeap = this.memoryManager.getMemHeap();
        //Continue in jmp
        
        memProg.jump(startAddr);
       Boolean haltFlag = false; 
       Boolean customProcessFlag;
       String[] codeComments = loadCodeComments();
      
      
       while (!haltFlag) {
             
             VMCommands command = memProg.getCommand();
             
             String argVal ="";
             Integer addr = memProg.getCommandAddrArg();
             customProcessFlag = true;
             switch(command){
                 case Invoke_Sys_Function:
                    this.addLog(String.format("%s %s (%s)", memProg.getAddr(), memProg.getCommand().toString(), VMSysFunction.values()[memoryManager.getIntPtrValue(addr)])) ;
                     
                   break;
                 case NOP:
                     
                     if(addr > 0 ){
                        argVal = codeComments[addr - 1];
                        this.addLog(String.format("### %s", argVal));
                     }
                     break;  
                 case Halt:
                     break;
                 default:
                     customProcessFlag = false;
                   try{
                       argVal = String.format("%s(%s)", addr,  Integer.toString(memoryManager.getIntPtrValue(addr))) ;
                   }  catch(Exception e){
                       
                   }
                     
                     
             }
             
             if(command == VMCommands.Halt) haltFlag = true;
            
             if(!customProcessFlag) this.addLog(String.format("%s| %s| %s", memProg.getAddr(), memProg.getCommand().toString(), argVal));
             memProg.next();
        }
        
    }
    
    protected void printClassMetaInfo(int classId) throws VmExecutionExeption{
        int metaDataPtr = progMetaInfo.getClassMetaDataPointer(classId);
        int fieldsCount = progMetaInfo.readClassMetaDataHeader(metaDataPtr, VmMetaClassHeader.FIELDS_COUNT);
        int methodsCount = progMetaInfo.readClassMetaDataHeader(metaDataPtr, VmMetaClassHeader.METHODS_COUNT);
        int parentId = progMetaInfo.readClassMetaDataHeader(metaDataPtr, VmMetaClassHeader.PARENT_ID);
        
        this.addLog(String.format("Fields: %s, methods: %s.Parent_Id: %s", fieldsCount, methodsCount, parentId));
           
    }
    
        
    protected void varValuesDebug(){
     MemoryHeap memHeap = memoryManager.getMemHeap();   
     int N =  program.readHeader(VmExeHeader.VarTableSize);
     for(int i = 0; i < N; i++){
         int varAddrPtr =  addrTables.getAddrByIndex(VmExeHeader.VarTableSize, i);
         int varAddr = memHeap.getIntValue(varAddrPtr) ;
         this.addLog(String.format("Value of %s by addr %s is %s", i, varAddrPtr, memHeap.getFloatPtrValue(varAddrPtr)));
     }
        this.addLog("Stack pos " + this.memoryManager.getSysRegister(VmSysRegister.StackHeadPos));
    }
    
    public void addLog(String text){
        System.out.println(text);
    }
    
    public void addError(String text){
        System.err.println(text);
    }
}
