/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author Andrey
 */
public class CommandsParamsLoader {
    protected static final String MANDATORY_HEADER = "mandatory:";
    public static HashMap<String, CommandParam> readConfig() throws IOException{
         //String configName =  Main.class.getResource("/src/assets/config/env_params.conf" ).getPath();
         HashMap<String, CommandParam> res = new HashMap<>();
         List<String> lines = Files.readAllLines(ShellIOUtils.getPath("config/env_params.conf"));
         
         HashSet<String> mandatoryParams = new HashSet();
         HashSet<String> optionalParams = new HashSet();
         
         HashSet<String> currentCollection;
         String commandName = "";
         CommandParam commandParam;
         
         for(String line: lines){
             if(line.startsWith("  ")){
                 line = line.substring(2);
                 if(line.startsWith(MANDATORY_HEADER)){
                     currentCollection = mandatoryParams;
                 } else{
                     currentCollection = optionalParams;
                 }
                 line = line.substring(MANDATORY_HEADER.length());
                 String[] params = line.split(" ");
                 currentCollection.addAll(Arrays.asList(params));  
             } else{
                 if(commandName != ""){
                     commandParam = new CommandParam(mandatoryParams, optionalParams);
                     res.put(commandName, commandParam);
                     mandatoryParams = new HashSet();
                     optionalParams = new HashSet();
                 }
                 commandName = line.substring(0, line.length()-1);
               
             }
         }
         commandParam = new CommandParam(mandatoryParams, optionalParams);
         res.put(commandName, commandParam);
         return res;
    }
}
