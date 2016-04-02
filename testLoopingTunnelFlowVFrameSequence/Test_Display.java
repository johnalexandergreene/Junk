package org.fleen.junk.testLoopingTunnelFlowVFrameSequence;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.fleen.forsythia.app.grammarEditor.GE;
import org.fleen.geom_2D.GD;
import org.fleen.junk.loopingTunnelFlowVFrameSequence.LoopingTunnelFlowVFrameSequence;
import org.fleen.junk.loopingTunnelFlowVFrameSequence.VFrame;

public class Test_Display{
  
  static F0 ui;
  static BufferedImage image;
  private static final int 
    MAINCYCLEPERIOD=50,
    IMAGEWIDTH=500,
    IMAGEHEIGHT=500;
  static final FrameRenderer_Abstract RENDERER=new FrameRenderer_TestDisplay();
  static boolean keeprunning=true;
  
  public static final void main(String[] a){
    initUI();
    LoopingTunnelFlowVFrameSequence seq=new LTFVFS_Tight();
    //calculate scale to fit ui
    int w=ui.getWidth(),h=ui.getHeight();
    System.out.println("seq.viewportradius="+seq.outerradius);
    double 
      w0=((double)w)/2.0,
      h0=((double)h)/2.0,
      d=Math.sqrt(w0*w0+h0*h0);;
    double scale=d/seq.outerradius;
    double rotateincrement=(2.0*GD.PI)/((double)seq.framecount);
    //get an iterator, iterate and render, repeat forever
    Iterator<VFrame> frameiterator;
    VFrame frame;
    while(keeprunning){
      frameiterator=seq.getVFrameIterator();
      while(frameiterator.hasNext()){
        try{
          Thread.sleep(MAINCYCLEPERIOD);
        }catch(Exception e){}
        frame=frameiterator.next();
        System.out.println("RENDERING FRAME #"+frame.index);
        image=RENDERER.getImage(frame,w,h,scale,rotateincrement*((double)frame.index),seq.outerradius);
        ui.repaint();}}}
  
  private static final void initUI(){
    ui=new F0("test seq file");
    ui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    ui.setBounds(new Rectangle(300,100,IMAGEWIDTH,IMAGEHEIGHT));
    ui.setVisible(true);}
  
  //FRAME
  @SuppressWarnings("serial")
  static class F0 extends JFrame{
    
    public F0(String s){
      super(s);}
    
    public void paint(Graphics g){
      if(image!=null)
        g.drawImage(image,0,0,null);}}
  
  /*
   * import a DGCLoopingTunnelFlowFrameSequence_Abstract class object from a file
   */
  static LoopingTunnelFlowVFrameSequence getSeq(){
    JFileChooser fc=new JFileChooser();
    int r=fc.showOpenDialog(GE.uimain);
    if(r!=JFileChooser.APPROVE_OPTION)
      return null;
    File file=fc.getSelectedFile();
    FileInputStream fis;
    ObjectInputStream ois;
    LoopingTunnelFlowVFrameSequence seq=null;
    try{
      fis=new FileInputStream(file);
      ois=new ObjectInputStream(fis);
      seq=(LoopingTunnelFlowVFrameSequence)ois.readObject();
      ois.close();
    }catch(Exception e){
      System.out.println("#^#^# EXCEPTION IN IMPORT DGCLTFFS #^#^#");
      e.printStackTrace();
      return null;}
    return seq;}
  
}
