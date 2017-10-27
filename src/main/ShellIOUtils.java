/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Andrey
 */
public class ShellIOUtils {
    public static Path getPath(String relFileName){
        return Paths.get(System.getProperty("user.dir") + "/src/assets/" + relFileName);
    }
}
