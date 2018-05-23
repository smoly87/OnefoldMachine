/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package perfomance.measure;

import compiler.exception.CompilerException;
import java.util.HashMap;
import java.util.LinkedList;
import syntax.analyser.CompilationException;
import syntax.analyser.parser.ParserException;
import virtual.machine.exception.VmExecutionExeption;

/**
 *
 * @author Andrey
 */
public class EstimatorChainedTasks {
   protected int repeatTimes; 
   protected LinkedList<EstimateTask> tasksChain;
   protected Object initialObj;

   public EstimatorChainedTasks(Object initialObj, int repeatTimes) {
        this.repeatTimes = repeatTimes;
        tasksChain = new LinkedList<>();
        this.initialObj = initialObj;
   }
   
   public EstimatorChainedTasks add(EstimateTask task){
       tasksChain.add(task);
       return this;
   }
   
   public EstimatorChainedTasks add(EstimateTask task, boolean once){
       task.setOnce(once);
       tasksChain.add(task);
       return this;
   }
   
   protected Object estimateTask(EstimateTask task, Object inputObj) throws CompilationException, CompilerException, VmExecutionExeption, ParserException{
       Object res = null;
       for(int k = 0; k < repeatTimes; k++){
           if(k == 0){
               res = task.compute(inputObj);
           } else{
               task.compute(inputObj);
           }
          
       }
       
       return res;
   }
   
   public HashMap<String, EstimateResult> getResults() throws CompilationException, CompilerException, VmExecutionExeption, ParserException{
       Object inputObj = initialObj;
       HashMap<String, EstimateResult> resMap = new HashMap<>();
       for(EstimateTask task : tasksChain){
           
           if(task.isOnce()){
               inputObj = task.compute(inputObj);
               continue;
           }
           
           long startTime = System.currentTimeMillis();
           inputObj = estimateTask(task, inputObj);
           long totalTime = System.currentTimeMillis() - startTime;
           
           EstimateResult estRes = new EstimateResult();
           estRes.setAverageTime(totalTime/repeatTimes);
           
           resMap.put(task.getTaskName(), estRes);
       }
       
       return resMap;
   } 
   
}
