/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.builder;

import java.util.ArrayList;
import virtual.machine.DataBinConvertor;
import virtual.machine.VM;
import virtual.machine.VmExeHeader;

/**
 *
 * @author Andrey
 */
public class BinaryReader {
    protected ArrayList<Byte> data;
    protected int curPos;
    protected DataBinConvertor binConvertorService;

    public void setCurPos(int curPos) {
        this.curPos = curPos;
    }

    public int getCurPos() {
        return curPos;
    }
    
    
    public BinaryReader(ArrayList<Byte> data){
        binConvertorService = DataBinConvertor.getInstance();
        this.data = data;
    }
    
    public int readIntAndNext(){
       int res = binConvertorService.bytesToInt(data, curPos);
       curPos += VM.INT_SIZE;
       return res;
    }
    
    public int readInt(){
       int res = binConvertorService.bytesToInt(data, curPos);
       return res;
    }
    
    
    
    public Byte[] readAndNextBytes(int bytesCount){
      
       Byte[] res = data.subList(curPos, curPos + bytesCount).toArray(new Byte[bytesCount]);
       curPos += bytesCount;
       return res;
    }
    
    public int nextBytes(int bytesCount){
        curPos += bytesCount;
        return curPos;
    }
    
    public int prevBytes(int bytesCount){
        curPos -= bytesCount;
        return curPos;
    }
}
