/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author Andrey
 */
public class ProgramLogger {

    public ProgramLogger(boolean enabled, String header) {
        if(header != ""){
            this.addHeader(header);
        }
    }
    
    public void addHeader(String text){
    }
    
    public void addLog(String text){
        System.out.println(text);
    }
    
    public void addError(String text){
        System.err.println(text);
    }
}
