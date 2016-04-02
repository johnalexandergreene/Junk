package org.fleen.junk.genRainbowTriumph;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.fleen.junk.genRainbowTriumph.palette.Palette_Abstract;
import org.fleen.junk.genRainbowTriumph.palette.Palette_Grayscale;

/*
 * Prospective names for the product :
 * RAINBOW TRIUMPH
 */
public class TestRTImageGen{

  public static F0 frame;
  //gen params
  private static final int 
    MAINCYCLEPERIOD=800,
    FADEINIMAGEGENCYCLEPERIOD=90,
    FADEINCYCLES=8,
    BLOCKDENSITY=9;
  private static final Palette_Abstract PALETTE=new Palette_Grayscale();
//  private static final Palette_Abstract PALETTE=new Palette_Tangerine();
//  private static final Palette_Abstract PALETTE=new Palette_PastelRainbow();
  private static final double SCANNERCREATIONPROBABILITY=0.8;
  private static final int MAXSCANNERS=7;
  
  public static final void main(String[] a){
    //init display frame
    frame=new F0("test rainbow triumph image gen");
    frame.setUndecorated(true);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setBounds(new Rectangle(300,100,600,800));
    frame.setVisible(true);
    //run the image generator
    ImageGen g=new ImageGen();
    g.init(
      frame.getWidth(),
      frame.getHeight(),
      BLOCKDENSITY,
      MAINCYCLEPERIOD,
      FADEINIMAGEGENCYCLEPERIOD,
      FADEINCYCLES,
      PALETTE,
      SCANNERCREATIONPROBABILITY,
      MAXSCANNERS,
      new IGListener());
    g.start();}
  
  static BufferedImage image;
  
  //IMAGE GEN LISTENER
  static class IGListener implements ImageGenListener{

    public void notify(BufferedImage image){
      TestRTImageGen.image=image;
      frame.repaint();}}
  
  //FRAME
  @SuppressWarnings("serial")
  static class F0 extends JFrame{
    
    public F0(String s){
      super(s);}
    
    public void paint(Graphics g){
      if(image!=null)
        g.drawImage(image,0,0,null);}}
  
}
