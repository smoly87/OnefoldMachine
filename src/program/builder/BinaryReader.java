/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.builder;

import java.util.ArrayList;
import virtual.machine.DataBinConvertor;
import virtual.machine.VM;

/**
 *
 * @author Andrey
 */
public class BinaryReader {
    protected ArrayList<Byte> data;
    protected int curPos;

    public void setCurPos(int curPos) {
        this.curPos = curPos;
    }

    public int getCurPos() {
        return curPos;
    }
    protected DataBinConvertor binConvertorService;
    
    public BinaryReader(ArrayList<Byte> data){
        binConvertorService = DataBinConvertor.getInstance();
        this.data = data;
    }
    
    public int readIntAndNext(){
       int res = binConvertorService.bytesToInt(data, curPos);
       curPos += VM.INT_SIZE;
       return res;
    }
    
    public int nextBytes(int bytesCount){
        curPos += bytesCount;
        return curPos;
    }
}
