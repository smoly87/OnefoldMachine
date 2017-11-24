/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser.parser;

import java.util.StringJoiner;

/**
 *
 * @author Andrey
 */
public class ProgramBuildingStage extends Subscribeable<IProgramBuildingSubscriber>{

    
    protected StringJoiner eventTextItems;
    protected final String separator = "\n";
    protected String currentEventText;

    
    public ProgramBuildingStage() {
        eventTextItems = new StringJoiner(separator);
    }
    
    
    protected void addTextToEvent(String text){
        eventTextItems.add(text);
    }
    
    protected String flushEventText(){
        String res = eventTextItems.toString();
        eventTextItems = new StringJoiner(separator);
        return res;
    }
    
    protected void callSubscribers(String eventName, String eventText) {
        addTextToEvent(eventText);
        callSubscribers(eventName);
    }
    
    @Override
    protected void callSubscribers(String eventName) {
        currentEventText = flushEventText();  
        super.callSubscribers(eventName); 
    }

    @Override
    protected void callSubscriber(IProgramBuildingSubscriber subscriber, String eventName) {
        subscriber.programEvent(this.getClass(), eventName, currentEventText);
    }
    
}
