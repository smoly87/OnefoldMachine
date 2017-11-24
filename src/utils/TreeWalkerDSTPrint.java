/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.Collections;
import main.ProgramLogger;
import syntax.analyser.AstNode;

/**
 *
 * @author Andrey
 */
public class TreeWalkerDSTPrint extends TreeWalkerDST{
    protected ProgramLogger logger;
    public TreeWalkerDSTPrint(ProgramLogger logger){
        super();
        this.logger = logger;
    }
    @Override
    protected void callback(AstNode node, int level) {
        String margin = String.join("", Collections.nCopies(level, "-"));
        //Refactor to string Builder
        if(node!=null && node.getToken()!=null ){
             logger.addLog(margin + node.getToken().getTagName()+" " +node.getToken().getValue() );
        }
       
    }
    
}
