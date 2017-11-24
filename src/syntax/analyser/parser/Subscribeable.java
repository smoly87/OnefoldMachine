/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntax.analyser.parser;

import java.util.LinkedList;

/**
 *
 * @author Andrey
 */
public abstract class Subscribeable<SubscriberType> {
    protected LinkedList<SubscriberType> subscribersList;
    protected boolean hasSubscribers = false;

    
    public Subscribeable(){
         subscribersList = new LinkedList<>();
    }
    
    public void addSubscriber(SubscriberType subscriber){
        subscribersList.add(subscriber);
        hasSubscribers = true;
    }
    
    protected abstract void callSubscriber(SubscriberType subscriber, String eventName);
    
       
    protected void callSubscribers(String eventName){
        for(SubscriberType subscriber : subscribersList){
            this.callSubscriber(subscriber, eventName);
        }
    }
}
