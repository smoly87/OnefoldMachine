/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package syntax.analyser;

import com.sun.javafx.scene.SceneHelper;
import syntax.analyser.parser.ParserException;

/**
 *
 * @author Andrey
 */
public class UnexpectedSymbolException extends ParserException{
    public UnexpectedSymbolException(String message){
       super(message);
    }
}
