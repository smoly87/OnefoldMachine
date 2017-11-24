/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author Andrey
 */
public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        boolean fullDebug = true;
        CommandInterpreter commadInter = null;
        try {
            
           InputStreamReader inputStreamReader = new InputStreamReader(System.in);
           BufferedReader reader = new BufferedReader(inputStreamReader);
           commadInter = new CommandInterpreter();

           while(true){
              System.out.println("Type a command:");
              String inpStr = !fullDebug ? reader.readLine() : "compile_run --path_src gc.txt --debug"; //;// class_expr
              if(inpStr.equals("exit") ) return;
              if(!commadInter.isValidCommand(inpStr)){
                  System.err.println(commadInter.getError());
              } else {
                if(!commadInter.executeCommand(inpStr)){
                   System.err.println(commadInter.getError());
                }
                return;
              }
            }  
        } catch (ConfigLoadException ex) {
            System.err.println(ex.getMessage());
        }
        
       
        
    }
    
}
