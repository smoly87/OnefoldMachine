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
public class VmExecutionExeption extends Exception{
    public VmExecutionExeption(){
    }
    public VmExecutionExeption(String message) {
        super(message);
    }
    
}
