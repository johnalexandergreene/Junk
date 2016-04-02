package org.fleen.junk.genLoopingTunnelFlowFrameSeqImage;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.fleen.geom_2D.GD;
import org.fleen.junk.loopingTunnelFlowVFrameSequence.LoopingTunnelFlowVFrameSequence;
import org.fleen.junk.loopingTunnelFlowVFrameSequence.VFrame;

public class Gen{
  
  private static final int 
  MAINCYCLEPERIOD=50,
  //these dimensions for google play screenshots
  IMAGEWIDTH=180,
  IMAGEHEIGHT=120;
  
  static F0 ui;
  static BufferedImage image;
  static final FrameRenderer RENDERER=new FrameRenderer();
  static final FileWriter WRITER=new FileWriter();
  static final int FRAMECOUNT=60;
  static final String EXPORT_DIR_PATH="/home/john/Desktop/frames_export/";
  private static final double
    DETAILFLOOR_SEQ=0.15,//0.1,//0.06,
    DETAILFLOOR_BLOCK=1.6;//1.2;//0.4;
  
  public static final void main(String[] a){
    initUI();
    LoopingTunnelFlowVFrameBlock_TF0000_Tight block=new LoopingTunnelFlowVFrameBlock_TF0000_Tight(DETAILFLOOR_BLOCK);
    LoopingTunnelFlowVFrameSequence seq=new LoopingTunnelFlowVFrameSequence(block,FRAMECOUNT,DETAILFLOOR_SEQ);
    //calculate scale to fit ui
    int w=ui.getWidth(),h=ui.getHeight();
    double 
      w0=((double)w)/2.0,
      h0=((double)h)/2.0,
      d=Math.sqrt(w0*w0+h0*h0);;
    double scale=d/seq.outerradius;
    //
    double rotateincrement=(2.0*GD.PI)/((double)FRAMECOUNT);
    //
    Iterator<VFrame> i=seq.getVFrameIterator();
    VFrame frame;
    while(i.hasNext()){
      frame=i.next();
      System.out.println("RENDERING FRAME #"+frame.index);
      image=RENDERER.getImage(frame,w,h,scale,rotateincrement*((double)frame.index),seq.outerradius);
      ui.repaint();
      WRITER.write(image,EXPORT_DIR_PATH,300);
      try{
        Thread.sleep(MAINCYCLEPERIOD);
      }catch(Exception e){}}
    System.out.println("+++FINISHED+++");}
  
  private static final void initUI(){
    ui=new F0("test seq file");
    ui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    ui.setBounds(new Rectangle(300,100,IMAGEWIDTH,IMAGEHEIGHT));
    ui.setVisible(true);}
  
  //UI
  @SuppressWarnings("serial")
  static class F0 extends JFrame{
    
    public F0(String s){
      super(s);}
    
    public void paint(Graphics g){
      if(image!=null)
        g.drawImage(image,0,0,null);}}
  
}
