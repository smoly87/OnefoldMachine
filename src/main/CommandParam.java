/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 *
 * @author Andrey
 */
public class CommandParam {
    protected HashSet<String> mandatoryParams ;
    protected HashSet<String> optionalParams;
    protected String error;

    public String getError() {
        return error;
    }

    public CommandParam(HashSet<String> mandatoryParams, HashSet<String> optionalParams) {
        this.mandatoryParams = mandatoryParams;
        this.optionalParams = optionalParams;
    }
    
    public boolean isValidParam(String paramName){
        return (mandatoryParams.contains(paramName) || optionalParams.contains(paramName));
    }
    
    public boolean checkValidAllParams(ShellCommand command){
        HashMap<String, String> commandsMap = command.getOptions();
        for(Map.Entry<String, String> entry: commandsMap.entrySet()){
            if(!this.isValidParam(entry.getKey())){
                this.error = "Invalid param with name: " + entry.getKey();
                return false;
            }
        }
        return true;
    }
    
    public boolean checkMandatoryParams(ShellCommand command){
        for(String mandPararm: mandatoryParams){
            if(!command.isOptionExists(mandPararm)) return false;
        }
        return true;
    }
}
