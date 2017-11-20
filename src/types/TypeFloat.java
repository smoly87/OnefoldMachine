/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package types;

import java.nio.ByteBuffer;
import main.ByteUtils;

/**
 *
 * @author Andrey
 */
public class TypeFloat implements IType  {

    @Override
    public Byte[] toBinary(String value) {
       return toBinary(Float.valueOf(value));
    }
    public Byte[] toBinary(float value) {
        int bits = Float.floatToIntBits(value);
        Byte[] bytes = new Byte[4];
        bytes[3] = (byte) (bits & 0xff);
        bytes[2] = (byte) ((bits >> 8) & 0xff);
        bytes[1] = (byte) ((bits >> 16) & 0xff);
        bytes[0] = (byte) ((bits >> 24) & 0xff);
        float tst = getValue(bytes);
        return bytes;
    }
    
    public float getValue(Byte[] value){
       return ByteBuffer.wrap(ByteUtils.convert(value)).getFloat();
    }

    @Override
    public int getTypeSize() {
        return 4;
    }
    
}
