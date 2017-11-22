/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine;

import virtual.machine.exception.VmStackEmptyPopException;
import virtual.machine.exception.VMOutOfMemoryException;
import virtual.machine.exception.VmExecutionExeption;
import virtual.machine.exception.VMStackOverflowException;
import common.VarType;
import java.io.UnsupportedEncodingException;
import types.TypeString;
import types.TypesInfo;
import utils.Pair;
import static virtual.machine.VM.INT_SIZE;
import virtual.machine.memory.GarbageCollectorBlock;
import virtual.machine.memory.Memory;
import virtual.machine.memory.MemoryHeap;
import virtual.machine.memory.MemoryManager;
import virtual.machine.memory.MemoryStack;
import virtual.machine.memory.VmSysRegister;

/**
 *
 * @author Andrey
 */
public class VmSysFunctions {
    protected MemoryManager memoryManager; 
    protected VmProgramMetaInfo progMetaInfo;
    protected DataBinConvertor binConvertorService;
    
    public VmSysFunctions(MemoryManager memoryManager, VmProgramMetaInfo progMetaInfo){
        this.memoryManager = memoryManager;
        this.progMetaInfo = progMetaInfo;
        this.binConvertorService =  DataBinConvertor.getInstance();
    }
    
     protected int sysMemAllocStack() throws VmStackEmptyPopException, VMStackOverflowException, VmExecutionExeption {
        MemoryStack memStack = this.memoryManager.getMemStack();
        int dataSize = memoryManager.stackPopInt();
         
        Byte[] data = new Byte[dataSize];
        int ptrStart = memStack.push(data);
         //memStack.push(data); 
         
        // System.out.println(String.format("Allocate on stack: %s in addr# %s", dataSize, ptrStart));
        memStack.push(ptrStart);
        return ptrStart;
    }
    
    protected int sysMemAlloc() throws VmStackEmptyPopException, VMOutOfMemoryException, VMStackOverflowException,  VmExecutionExeption{
        MemoryHeap memHeap = this.memoryManager.getMemHeap();
        MemoryStack memStack = this.memoryManager.getMemStack();
        
        int dataSize = memoryManager.stackPopInt();
        int ptrStart = memHeap.memAlloc(dataSize);
        
        memStack.push(binConvertorService.toBin(ptrStart), VarType.Pointer.ordinal()); 
        return ptrStart;
    }
    
    protected int sysMemAllocPtr() throws VmStackEmptyPopException, VMOutOfMemoryException, VMStackOverflowException,  VmExecutionExeption{
        MemoryHeap memHeap = this.memoryManager.getMemHeap();
        MemoryStack memStack = this.memoryManager.getMemStack();
        
        byte objType = (byte)memoryManager.stackPopInt();
        int dataSize = memoryManager.stackPopInt();
        
        int ptrStart = memHeap.memAllocPtr(dataSize);
        //System.err.println("Create ptr at:" + ptrStart);
        memHeap.putValue(ptrStart, new Byte[]{objType});
        memHeap.putPtrValue(ptrStart, dataSize);
        
        
        memStack.push(binConvertorService.toBin(ptrStart)); 
        return ptrStart;
    }
    
    
    
    protected void sysSetPtrField() throws VmExecutionExeption{
        //TODO: Absolutley need to count field address, not ptr
        MemoryHeap memHeap = this.memoryManager.getMemHeap();
        MemoryStack memStack = this.memoryManager.getMemStack();
        
        
        int fieldNum = memoryManager.stackPopInt();
        Byte[] fieldValue = memStack.pop();
        int ptrAddr = memoryManager.stackPopInt();
        preventForNullPointer(ptrAddr);
        int fieldOffset = fieldNum * INT_SIZE;
        if(fieldNum > 1){
             /*if(ptrAddr < 2000){
                 ptrAddr = memHeap.getPtrIntField(ptrAddr,0);
             }*/
             fieldOffset = progMetaInfo.getFieldOffsetObj(ptrAddr, fieldNum);
        }
        
        memHeap.putValue(ptrAddr + Memory.PTR_HEADERS_SIZE + fieldOffset, fieldValue);
        
       // memHeap.putValue(ptrAddr, fieldValue);
       
        
    }
     protected void sysPrint() throws  VmExecutionExeption{
        int ptrAddr = memoryManager.stackPopInt();
        MemoryStack memStack = this.memoryManager.getMemStack();
        Byte[] val =  memoryManager.getMemHeap().getPtrByteValue(ptrAddr);
        TypeString stringBinConv = (TypeString)TypesInfo.getInstance().getConvertor(VarType.String);
        try{
            System.err.println("SYS_PRINT: " + stringBinConv.getValue(val));
        } catch(UnsupportedEncodingException ex){
            throw new VmExecutionExeption(ex.getClass().getName() + ":" + ex.getMessage());
        }
        
    }
    
    protected void sysPrintObjField() throws  VmExecutionExeption{
        int fieldNum = memoryManager.stackPopInt();
        int ptrAddr = memoryManager.stackPopInt();
        preventForNullPointer(ptrAddr);
        int fieldOffset = progMetaInfo.getFieldOffsetObj(ptrAddr, fieldNum);
        int value = memoryManager.getMemHeap().getPtrIntField(ptrAddr,  fieldOffset);
        System.err.println(String.format("PRINT_OBJ_FIELD: Value: %s FieldNum: %s  ", value, fieldNum) );
    }
    
 
    
    protected void sysGetVirtualFuncAddr() throws  VmExecutionExeption{
         int objPtr = memoryManager.stackPopInt();
         int methodCode = memoryManager.stackPopInt();
         int addrType = memoryManager.stackPopInt();
        
         int classId = progMetaInfo.getClassId(objPtr);
         int metaDataPtr = progMetaInfo.getClassMetaDataPointer(classId);
         
         int methodAddr = progMetaInfo.getMethodAddress(metaDataPtr, methodCode, VM.METHOD_ADDR_TYPE.values()[addrType]);
         
         memoryManager.getMemStack().push(methodAddr);
         
    }
    
    protected void sysArrangeFuncParams() throws  VmExecutionExeption{
        
        
        
        int paramsCount = memoryManager.stackPopInt();
        MemoryStack memStack = this.memoryManager.getMemStack();
        //int dbg = memStack.getIntPtrValue(2548)  ;
        int frameStart = memoryManager.getSysRegister(VmSysRegister.FrameStackTableStart);
        int frameHeadersPosEnd = frameStart + Memory.PTR_HEADERS_SIZE + VM.INT_SIZE;
        
        int varAddr = memoryManager.getSysRegister(VmSysRegister.StackHeadPos);
        
        for(int varInd = paramsCount - 1; varInd >= 0; varInd--){
            int varCellAddr = frameHeadersPosEnd + varInd * INT_SIZE;
            memStack.putValue(varCellAddr, varAddr);
            int size = memStack.getPtrSize(varAddr);
            //if(size == 8){
            
            
            
//              System.out.println(String.format("Local vaar with ind %s address is %s, value: %s stored at %s",varInd,  varAddr, memStack.getPtrIntField(varAddr, VM.INT_SIZE), varCellAddr));
           // }
          varAddr = memStack.getIntPtrValue(varAddr);
            
        }

    }
    
    protected void sysGarbageCollect() throws VmExecutionExeption{
      Pair<Integer, GarbageCollectorBlock> gcRes = memoryManager.getMemHeap().garbageCollect();
      int freedSpace =  gcRes.getObj1();
      GarbageCollectorBlock gcFinalBlock = gcRes.getObj2();
      
      memoryManager.reallocateAddresses(gcFinalBlock);
      
      
      System.out.println("GC: Freed space after clean: " + freedSpace);
    }
    
    protected void sysDeferPtrValue() throws VmExecutionExeption{
        int ptr = memoryManager.stackPopInt();
        Byte[] value = memoryManager.getMemHeap().getPtrByteValue(ptr);
        MemoryStack memStack = this.memoryManager.getMemStack();
        memStack.push(value);
    }
    
     protected void sysDeleteFrame() throws VmExecutionExeption{
        MemoryStack memStack = memoryManager.getMemStack();
        
        int stackRegRestore = memoryManager.stackPopInt();
        int stackTableRegRestore = memoryManager.stackPopInt();
        
    
        memoryManager.setSysRegister(VmSysRegister.StackHeadPos, stackRegRestore);
        memoryManager.setSysRegister(VmSysRegister.FrameStackTableStart, stackTableRegRestore);
        //memStack.putValue(frameHeadersPosEnd + varInd * INT_SIZE, locVarAddr);
    }
     
    protected void preventForNullPointer(int ptrAddr) throws VmExecutionExeption {
        if (ptrAddr == -1) {
            throw new VmExecutionExeption("Null Pointer!");
        }
    }

    protected void changeIntFieldValue(int ptrAddr, int fieldNum, int delta) throws VmExecutionExeption {
        MemoryHeap memHeap = this.memoryManager.getMemHeap();

        preventForNullPointer(ptrAddr);
        int fieldOffset = fieldNum * INT_SIZE;
        if (fieldNum > 1) {
            fieldOffset = progMetaInfo.getFieldOffsetObj(ptrAddr, fieldNum);
        }
        int value = memHeap.getPtrIntField(ptrAddr, fieldOffset);
        memHeap.putValue(ptrAddr + Memory.PTR_HEADERS_SIZE + fieldOffset, value + delta);
    }
    
    protected void sysPrintHeapSize(){
        System.out.println("###Heap size:" + memoryManager.getMemHeap().dataSize());
    }
    
    protected void callSysFunc(int funcTypeAddrPtr) throws VmExecutionExeption{   
        MemoryStack memStack = this.memoryManager.getMemStack();
        int funcType  = memStack.getIntPtrValue(funcTypeAddrPtr);
        int arg;
        int retVal;
        
        VMSysFunction sysFunc = VMSysFunction.values()[funcType];
      
         System.out.println("###Sys func called: " + sysFunc.toString() + "(" + funcTypeAddrPtr + ")");
        switch(sysFunc){
            case MemAlloc:
                retVal = sysMemAlloc();
                System.out.println("Mem alloc at adress: " + retVal);
                break;
            case MemAllocStack:
                sysMemAllocStack();
                break;
            case MemAllocPtr:
                sysMemAllocPtr();
                break;
            case GetRegister:
                arg = memoryManager.stackPopInt();
                VmSysRegister register = VmSysRegister.values()[arg];
                int regValue = memoryManager.getSysRegister(register);       
                System.out.println(String.format("Get Register %s is %s", register.toString(), regValue));
                memStack.push(binConvertorService.toBin(regValue));
                break;
            case SetRegister:
                int regNum = memoryManager.stackPopInt();
                regValue = memoryManager.stackPopInt();  
                register =  VmSysRegister.values()[regNum];
                /*if(register == VmSysRegister.F1){
                    regValue -= memoryManager.getSysRegister(VmSysRegister.ProgOffsetAddr);
                }*/
                
                memoryManager.setSysRegister(register, regValue);
                System.out.println(String.format("Set Register %s is %s", register.toString(), regValue));
                break;
            case SetPtrField:
                sysSetPtrField();
                break;
            case GetPtrField:
                sysGetPtrField();
                break;    
            case GetVirtualFuncAddr:
                sysGetVirtualFuncAddr();
                break;    
            case Print:
                sysPrint();
                break;
            case PrintObjField:
                sysPrintObjField();
                break;
            case PrintHeapSize:
                sysPrintHeapSize();
                break;
            case GarbageCollect:
                sysGarbageCollect();
                break;
            case DeferPtrValue:
                sysDeferPtrValue();
                break;
            case DeleteFrame:
                sysDeleteFrame();
                break;
            case ArrangeFuncParams:
                sysArrangeFuncParams();
                break;
            default:
                System.err.println("Callede unreliased function: " + sysFunc.toString());
        }
    }
    protected void sysGetPtrField() throws VmExecutionExeption{
        //TODO: Absolutley need to count field address, not ptr
        MemoryHeap memHeap = this.memoryManager.getMemHeap();
        MemoryStack memStack = this.memoryManager.getMemStack();
        
        
        int fieldNum = memoryManager.stackPopInt();
        int ptrAddr = memoryManager.stackPopInt();
        preventForNullPointer(ptrAddr);
        
        int fieldOffset = fieldNum * INT_SIZE;
        if(fieldNum > 1){
             fieldOffset = progMetaInfo.getFieldOffsetObj(ptrAddr, fieldNum);
        }
        
        int val = memHeap.getPtrIntField(ptrAddr,  fieldOffset);
        memStack.push(val);
       // memHeap.putValue(ptrAddr, fieldValue);
       
        
    }
}
