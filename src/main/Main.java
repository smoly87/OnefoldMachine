/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import common.FullPipeline;
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
import java.util.ArrayList;
import utils.TreeWalkerDST;
import utils.TreeWalkerDSTPrint;
import virtual.machine.Instructions;
import program.builder.ProgramBuilder;
import virtual.machine.DataBinConvertor;
import virtual.machine.Program;
import virtual.machine.VM;
import virtual.machine.VMCommands;
import virtual.machine.VmSections;

/**
 *
 * @author Andrey
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        // TODO code application logic here
        FullPipeline fullPipe = new  FullPipeline();
       
       // try{
            String path = Main.class.getResource("/assets/math_expr.txt").getPath();
            Scanner scanner = new Scanner( new File(path), "UTF-8" );
            String programSrc = scanner.useDelimiter("\\A").next();
            scanner.close();
            
            programSrc = programSrc.replace(System.getProperty("line.separator"), "");
            programSrc = programSrc.replace(" ", "");
            System.out.println(programSrc);
            LexerResult lexRes = fullPipe.tokenise(programSrc);
            AstNode ast = fullPipe.buildAst(lexRes);
            
            TreeWalkerASTCompiler tw = new TreeWalkerASTCompiler();
            ProgramBuilder progBuilder = tw.walkTree(ast);
            System.out.println(progBuilder.getAsmText());
            TreeWalkerDST walker = new TreeWalkerDSTPrint();
            walker.walkTree(ast);
            
            progBuilder.addInstruction(VMCommands.Halt);
            Program prog = progBuilder.getResult();
            System.out.println(progBuilder.getAsmText());
            System.out.println("Compile success");
            
           /* DataBinConvertor dBin = DataBinConvertor.getInstance();
            
            Byte[] val = dBin.integerToByte(5);
            System.out.println("Conv from bytes: " + dBin.bytesToInt(val, 0)); ;*/
            
            System.out.println("Headers info");
            System.out.println(prog.readHeader(VmSections.ConstStart));
            System.out.println(prog.readHeader(VmSections.VarTableSize));
            
            VM virtMachine = new VM();
            virtMachine.run(prog);
           
           
            
      /*  } catch(Exception e){
            System.err.println("Error: cant't read program source: " + e.getMessage());
        }*/
        
    }
    
}
