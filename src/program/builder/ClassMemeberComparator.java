/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.builder;

import compiler.metadata.ClassMemeberDescription;
import java.util.Comparator;

/**
 *
 * @author Andrey
 */
public class ClassMemeberComparator implements Comparator<ClassMemeberDescription>{

    @Override
    public int compare(ClassMemeberDescription o1, ClassMemeberDescription o2) {
        if ( o1.getCode() == o2.getCode()) return 0 ;
        return o1.getCode() > o2.getCode() ? 1 : -1;
    }

    
}
