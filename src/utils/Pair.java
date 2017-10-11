/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

/**
 *
 * @author Andrey
 */
public class Pair<Type1, Type2> {
    protected Type1 obj1;
    protected Type2 obj2;

    public Pair(Type1 obj1, Type2 obj2) {
        this.obj1 = obj1;
        this.obj2 = obj2;
    }
    
    public Type1 getObj1() {
        return obj1;
    }

    public Type2 getObj2() {
        return obj2;
    }
    
    
}
