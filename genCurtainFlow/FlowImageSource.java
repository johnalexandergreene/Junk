package org.fleen.junk.genCurtainFlow;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Deque;
import java.util.LinkedList;

public class FlowImageSource{
  
  Deque<Stripe_Abstract> blocks=new LinkedList<Stripe_Abstract>(); 
  
  static final Color 
    COLOR_BACKGROUND=Color.white,
    COLOR_STROKE=Color.black;
  
  BufferedImage image=null;
  int imagewidth,imageheight;
  
  BufferedImage getImage(int w,int h){
    System.out.println("getting image");
    if(image==null){
      imagewidth=w;
      imageheight=h;
      initBlocks();
      initImage();}
    renderErase();
    increment();
    renderDraw();
    return image;}
  
  /*
   * draw all the blocks in background color, thus erasing them
   */
  private void renderErase(){
    
  }
  
  /*
   * draw all blocks in foreground color
   */
  private void renderDraw(){
    
  }
  
  /*
   * increment all block y offsets
   * conditionally create block at head
   * conditionally discard block at tail
   */
  private void increment(){
    
  }
  
  private void initBlocks(){
    
  }
  
  private void initImage(){
    image=new BufferedImage(imagewidth,imageheight,BufferedImage.TYPE_INT_ARGB_PRE);
    Graphics2D g=image.createGraphics();
    g.setColor(COLOR_BACKGROUND);
    g.fillRect(0,0,imagewidth,imageheight);}

}
