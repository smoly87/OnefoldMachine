/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.builder;

import compiler.metadata.ClassMemeberDescription;

/**
 *
 * @author Andrey
 */
public class MethodDescription extends ClassMemeberDescription{
    protected int address;
    protected int bodyAddress;

    public int getBodyAddress() {
        return bodyAddress;
    }

    public void setBodyAddress(int bodyAddress) {
        this.bodyAddress = bodyAddress;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }
}
