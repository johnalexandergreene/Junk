package org.fleen.junk.genCurtainFlow;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.WindowConstants;


public class FlowTest{

  public static F0 frame;
  public static FlowImageSource flowsource=new FlowImageSource(); 
  private static final long 
    INIT_DELAY=500,
    PERIODIC_DELAY=20;
  private static final ScheduledExecutorService ifs=Executors.newSingleThreadScheduledExecutor();
  
  public static final void main(String[] a){
    //init display frame
    frame=new F0("TESTFLOW");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setBounds(new Rectangle(22,22,600,400));
    frame.setVisible(true);
    //start incremental flow scheduler
    ifs.scheduleWithFixedDelay(
      new IncrementalFlowExecutor(),
      INIT_DELAY,
      PERIODIC_DELAY, 
      TimeUnit.MILLISECONDS);}
  
  static BufferedImage i0;
  
  static class IncrementalFlowExecutor extends Thread{
    
    public void run(){
      i0=flowsource.getImage(frame.getWidth(),frame.getHeight());
      frame.repaint();}}
  
  @SuppressWarnings("serial")
  static class F0 extends JFrame{
    
    public F0(String s){
      super(s);}
    
    public void paint(Graphics g){
      super.paint(g);
      if(i0!=null)
        g.drawImage(i0,0,0,null);}}
  
}
