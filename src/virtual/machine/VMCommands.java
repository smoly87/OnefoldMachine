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
public enum VMCommands {
    NOP, Halt,
    Push, Push_Addr_Value,Push_Addr, Pop, Jmp, 
    Var_Put, Var_Load, Var_Declare_Local,Var_Declare_Local_Def_value,
    Add, Mul,
    Var_Put_Local, Var_Load_Local,
    Invoke_Sys_Function,
    Mov, Dup,
    CmpEqual, CmpMore, CmpLess,
    JmpIf,JmpIfNot, Comment
}
