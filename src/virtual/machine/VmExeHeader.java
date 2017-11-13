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
//This address tables 4 bytes - int addr of var, func etc
    //ConstCount|VarCount|...funcCount|ClassesCount
    // After instructions
public enum VmExeHeader {
   ConstTableSize, VarTableSize, ClassesTableSize, ProgramStartPoint, CommentsCount,
   ConstStart, VarTableStart, ClassesMetaInfoStart,CommentsStart,  InstructionsStart
}
