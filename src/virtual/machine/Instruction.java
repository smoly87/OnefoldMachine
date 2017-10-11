/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine;

import grammar.GrammarInfo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author Andrey
 */
public class Instruction {
    
   protected String name; 
   protected  byte code; 
   protected  byte argsCount; 

    public Instruction(String name, byte code, byte argsCount) {
        this.name = name;
        this.code = code;
        this.argsCount = argsCount;
    }

    public String getName() {
        return name;
    }

    public byte getCode() {
        return code;
    }

    public byte getArgsCount() {
        return argsCount;
    }
   
   
}
