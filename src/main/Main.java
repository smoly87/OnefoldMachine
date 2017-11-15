/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import common.FullPipeline;
import common.TokenInfo;
import common.VarType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import lexer.LexerResult;
import syntax.analyser.AstNode;
import compiler.TreeWalkerASTCompiler;
import compiler.exception.CompilerException;
import grammar.GrammarInfo;
import grammar.GrammarInfoStorage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import utils.TreeWalkerDST;
import utils.TreeWalkerDSTPrint;
import virtual.machine.Instructions;
import program.builder.ProgramBuilder;
import program.builder.ProgramFileSys;
import syntax.analyser.parser.ParserException;
import virtual.machine.DataBinConvertor;
import virtual.machine.Program;
import virtual.machine.VM;
import virtual.machine.VMCommands;
import virtual.machine.VmExeHeader;
import virtual.machine.VmExecutionExeption;

/**
 *
 * @author Andrey
 */
public class Main {

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        boolean fullDebug = true;
        CommandInterpreter commadInter = null;
        try {
            
           InputStreamReader inputStreamReader = new InputStreamReader(System.in);
           BufferedReader reader = new BufferedReader(inputStreamReader);
           commadInter = new CommandInterpreter();

           while(true){
              System.out.println("Type a command:");
              String inpStr = !fullDebug ? reader.readLine() : "compile_run --path_src gc.txt"; //;//class class_expr
              if(inpStr.equals("exit") ) return;
              if(!commadInter.executeCommand(inpStr)){
                System.err.println(commadInter.getError());
              } else {
                return;
              }
            }  
        } catch (ConfigLoadException ex) {
            System.err.println(ex.getMessage());
        } catch(CompilerException|ParserException ex){
            System.err.println(ex.getMessage());
        } catch(VmExecutionExeption ex){
            System.err.println(ex.getMessage());
        }
       
        
    }
    
}
