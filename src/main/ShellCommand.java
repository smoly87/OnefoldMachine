/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Andrey
 */
public class ShellCommand {
    protected HashMap<String, String> options;
    protected String commandName;

    public HashMap<String, String> getOptions() {
        return options;
    }

    public ShellCommand(String strCommand) {
        this.options = new HashMap<>();
         String command; 
        
        int firstSpace = strCommand.indexOf(" ");
        
        if(firstSpace == -1){
            this.setCommandName(strCommand);
            return;
        }
        
        command = strCommand.substring(0, firstSpace);
        this.setCommandName(command);
        
        strCommand = strCommand.substring(firstSpace);

        Pattern pattern = Pattern.compile("(\\-\\-)(?<key>[\\w]+)(\\s(?<value>[\\w\\.]*))?");
        Matcher mathcher = pattern.matcher(strCommand);
        while (mathcher.find()) {
           this.addOption(mathcher.group("key"), mathcher.group("value")); 
            
        }
        
    }

    public String getOption(String optionName) {
        return this.options.get(optionName);
    }
    
    public boolean isOptionExists(String optionName){
        return this.options.containsKey(optionName);
    }

    protected void addOption(String optionName, String value) {
        this.options.put(optionName, value);
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }
}
