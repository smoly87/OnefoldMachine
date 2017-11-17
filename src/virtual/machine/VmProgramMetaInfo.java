/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine;

import virtual.machine.exception.VmExecutionExeption;
import program.builder.BinBuilderClassesMetaInfo;
import program.builder.BinaryReader;
import utils.Pair;
import static virtual.machine.VM.INT_SIZE;
import virtual.machine.memory.Memory;
import virtual.machine.memory.MemoryManager;

/**
 *
 * @author Andrey
 */
public class VmProgramMetaInfo {
    protected MemoryManager memoryManager;
    protected VMAddrTables addrTables;
    
    public VmProgramMetaInfo(MemoryManager memoryManager, VMAddrTables addrTables){
        this.memoryManager = memoryManager;
        this.addrTables = addrTables;
    }
    
    public int findMethodAddr(int code, int cnt, BinaryReader binReader, int start, VM.METHOD_ADDR_TYPE addrType){
        int k = 0;
        int offset = 0;
        binReader.setCurPos(start);
        while(k < cnt){
            int entryCode = binReader.readIntAndNext();
            
            if(entryCode == code){
                //
                if(addrType  == VM.METHOD_ADDR_TYPE.Start){
                    return binReader.readIntAndNext();
                } else{
                    binReader.readIntAndNext();
                    return binReader.readIntAndNext();
                }
            }
            
            binReader.readAndNextBytes(2 * INT_SIZE);
            
            k++;
        }
        
       return -1;
    }
    
    public int getMethodAddress(int classMetaDataPtr, int methodCode, VM.METHOD_ADDR_TYPE addrType) throws VmExecutionExeption{
        BinaryReader binReader = new BinaryReader(memoryManager.getMemHeap().getData());
        
        int methodsCount = readClassMetaDataHeader(classMetaDataPtr, VmMetaClassHeader.METHODS_COUNT);
        int headersSize = BinBuilderClassesMetaInfo.HEADERS_SIZE * VM.INT_SIZE;
       
        int methodAddr = findMethodAddr(methodCode, methodsCount, binReader, classMetaDataPtr + headersSize, addrType);
        if(methodAddr == -1){
            int parentClassId = readClassMetaDataHeader(classMetaDataPtr, VmMetaClassHeader.PARENT_ID);
             
            while(parentClassId > -1){
                classMetaDataPtr = this.getClassMetaDataPointer(parentClassId);
                methodsCount = readClassMetaDataHeader(classMetaDataPtr, VmMetaClassHeader.METHODS_COUNT);
                methodAddr = findMethodAddr(methodCode, methodsCount, binReader, classMetaDataPtr + headersSize, addrType);
                if(methodAddr > -1) return methodAddr;
                parentClassId = readClassMetaDataHeader(classMetaDataPtr, VmMetaClassHeader.PARENT_ID); 

            }
            throw new VmExecutionExeption(String.format("Method with code: %s not found in class hierachy. SysFunc: getVirtaulFunction", methodCode));

        } else{
            return methodAddr;
        }
    }
    
    public int readClassMetaDataHeader(int classMetaDataPtr, VmMetaClassHeader header){
        return memoryManager.getMemHeap().getIntValue(classMetaDataPtr + header.ordinal() * VM.INT_SIZE);
    }
    
    
     protected Pair<Integer, Integer> getFieldInfoObj(int objPtr, int fieldNum) throws VmExecutionExeption{
         int classId = getClassId(objPtr);
         int metaDataPtr = this.getClassMetaDataPointer(classId);
         Pair<Integer, Integer> fieldInfo = getFieldInfo(metaDataPtr, fieldNum);
         
         if(fieldInfo != null){
             return fieldInfo;
         }else {    
            throw new VmExecutionExeption(String.format("Field with number %s not found", fieldNum));
         }
         
         
    }
    
    protected int getFieldOffsetObj(int objPtr, int fieldNum) throws VmExecutionExeption{      
         Pair<Integer, Integer> fieldInfo = getFieldInfoObj(objPtr, fieldNum);
         return fieldInfo.getObj1();
         
    }
    protected int getClassId(int objPtr){ 
        //First field is size, second is classID, third is linkCount
        int classId = memoryManager.getMemHeap().getIntValue(objPtr + Memory.PTR_HEADERS_SIZE);
        return classId;
    }
    
    protected int getClassMetaDataPointer(int classId){ 
   
        Integer classMetaDataPtr = addrTables.getAddrByIndex(VmExeHeader.ClassesTableSize, classId); 
        
        return classMetaDataPtr;
    }
    
    protected Pair<Integer, Integer> getFieldInfo(int classMetaDataPtr, int fieldNum) throws VmExecutionExeption{
        BinaryReader binReader = new BinaryReader(memoryManager.getMemHeap().getData());
        int fieldsCount = readClassMetaDataHeader(classMetaDataPtr, VmMetaClassHeader.FIELDS_COUNT);
        int methodsCount = readClassMetaDataHeader(classMetaDataPtr, VmMetaClassHeader.METHODS_COUNT);
        int headersSize = BinBuilderClassesMetaInfo.HEADERS_SIZE * VM.INT_SIZE;
        int fieldsStart = headersSize + methodsCount * 2 * VM.INT_SIZE;
        
        binReader.setCurPos(classMetaDataPtr + fieldsStart);
        
        boolean flag = true;
        int offset = 0;
        
        int k = 0;
        while(k < fieldsCount){
            int fieldCode = binReader.readIntAndNext();
            
            if(fieldCode == fieldNum){
                return new Pair(offset, binReader.readIntAndNext());
            }
            
            int fieldSize = binReader.readIntAndNext();
            offset += fieldSize;
            k++;
        }
        
       return null;
    }
    

}
