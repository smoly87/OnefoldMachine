/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine;

import program.builder.ProgramBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringJoiner;

/**
 *
 * @author Andrey
 */
public class ProgSaverAsm {
    protected Instructions instructionsService;
    public ProgSaverAsm(Instructions instructionsService){
        this.instructionsService = instructionsService;
    }
    
    public void save(ProgramBuilder program, String fileName) {
      /* try {
           FileWriter writer = new FileWriter(fileName);
           
           int i =0;
           ArrayList<Byte> data = program.getData();
           while(i < data.size()){
               Byte instructionCode = instructions.get(i);
               
               Instruction instruction = instructionsService.getInstructionByCode(instructionCode);
               
               String command = "";
               byte argsCount = instruction.getArgsCount();
               if(argsCount > 0){
                   //TODO: Нужно подождать переделки в длину типов
                   StringJoiner sb = new StringJoiner(" ");
                   sb.add(instruction.getName());
                   for(byte k = 0; k < argsCount; k++ ){
                       i++;
                       sb.add(instructions.get(i).toString());
                       
                   }
                   sb.toString();
               } else{
                   command = instruction.getName();
               }
               
               
               
               writer.append(command);
               
               i++;
           }
           writer.flush();
         
       }  catch (IOException e){
           System.out.println("Can't save a program : " + e.getMessage()); 
       }
        */
    }
}
