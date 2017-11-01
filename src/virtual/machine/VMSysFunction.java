/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine;

/**
 *
 * @author Andrey
 */
public enum VMSysFunction {
    MemAlloc,
    MemAllocStack,
    GetRegister,
    SetRegister,
    MemAllocPtr,
    SetPtrField,
    GetPtrField,
    GetVirtualFuncAddr,
    Print,
    PrintObjField,
    GarbageCollect,
    DeferPtrValue,
}
