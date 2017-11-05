/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtual.machine.memory;

/**
 *
 * @author Andrey
 */
public class GCPtrInfo {
    protected Integer address;
    protected Integer shiftedAddress;

    public GCPtrInfo(Integer address) {
        this.address = address;
        this.shiftedAddress = address;
    }

    public Integer getAddress() {
        return address;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }

    public Integer getShiftedAddress() {
        return shiftedAddress;
    }

    public void shiftAddress(Integer delta) {
        this.shiftedAddress += delta;
    }
}
