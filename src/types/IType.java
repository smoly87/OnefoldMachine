/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package types;

/**
 *
 * @author Andrey
 */
public interface IType {
    public Byte[] toBinary(String value);
    public int getTypeSize();
}
