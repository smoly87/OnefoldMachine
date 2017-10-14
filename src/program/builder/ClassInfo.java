/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.builder;

import java.util.SortedMap;
import java.util.TreeSet;
import javafx.collections.transformation.SortedList;

/**
 *
 * @author Andrey
 */
public class ClassInfo {
    protected MetaClassesInfo metaInfo;
    protected TreeSet<Integer> methodsList;
    protected String className;

    public String getClassName() {
        return className;
    }
    
    public ClassInfo(String className){
        metaInfo = MetaClassesInfo.getInstance();
        methodsList = new TreeSet<Integer>();
        this.className = className;
    }
    
    public void addMethod(String name){
       int methodCode =  metaInfo.getMethodCode(name);
       methodsList.add(methodCode);
    }
}
