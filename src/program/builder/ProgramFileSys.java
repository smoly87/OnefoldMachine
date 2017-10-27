/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.builder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import main.ByteUtils;
import main.Main;
import main.ShellIOUtils;
import virtual.machine.Program;
/**
 *
 * @author Andrey
 */
public class ProgramFileSys {
    public static Program load(String fileName) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(Main.class.getResource("/assets/" + fileName).getPath()));
        Program prog = new Program(Arrays.asList(ByteUtils.convert(bytes)));
        return prog;
    }
    
    public static void save(Program program, String fileName) throws FileNotFoundException, IOException{

        FileOutputStream fos = new FileOutputStream(ShellIOUtils.getPath(fileName).toFile());
        ArrayList<Byte> data = program.getData();
        Byte[] arr = data.toArray(new Byte[data.size()]);
        fos.write(ByteUtils.convert(arr));
    }
}
