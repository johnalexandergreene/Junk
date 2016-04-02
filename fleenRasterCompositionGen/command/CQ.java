package org.fleen.junk.fleenRasterCompositionGen.command;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.fleen.junk.fleenRasterCompositionGen.Log;

/*
 * COMMAND QUEUE 
 */
public class CQ{
  
  /*
   * ################################
   * COMMAND QUEUE
   * ################################
   */
  
  private static final ScheduledExecutorService sched=Executors.newSingleThreadScheduledExecutor();
  private static Command command;
  private static Deque<Command> commands=new LinkedList<Command>();
  private static final long 
    COMMAND_QUEUE_MANAGER_INIT_DELAY=500,
    COMMAND_QUEUE_MANAGER_PERIODIC_DELAY=20;
  private static boolean commandrunning=false;
  
  public static final void init(){
    sched.scheduleWithFixedDelay(
      new CommandExecutor(),
      COMMAND_QUEUE_MANAGER_INIT_DELAY,
      COMMAND_QUEUE_MANAGER_PERIODIC_DELAY, 
      TimeUnit.MILLISECONDS);}
  
  public static class CommandExecutor extends Thread{
    public void run(){
      if(!commands.isEmpty()){
        command=commands.removeFirst();
        try{
          commandrunning=true;
          Log.m1("["+command.getDescription()+"]");
          command.execute();
          commandrunning=false;
        }catch(Throwable x){
          x.printStackTrace();}}}}
  
  public static final boolean idle(){
    return commands.isEmpty()&&!commandrunning;}
  
  /*
   * ################################
   * COMMANDS
   * ################################
   */
  
  public static final void generate(){
    commands.addLast(new C_Generate());}
  
  public static final void renderForViewer(){
    commands.addLast(new C_RenderForViewer());}
  
  public static final void renderForExport(){
    commands.addLast(new C_RenderForExport());}
  
  public static final void export(){
    commands.addLast(new C_Export());}
  
  public static final void generateAndExport(){
    commands.addLast(new C_GenerateAndExport());}
  
  public static final void saveConfig(){
    commands.addLast(new C_SaveConfig());}
  
}
