package org.fleen.junk.testLoopingTunnelFlowVFrameSequence;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import org.fleen.junk.loopingTunnelFlowVFrameSequence.VFrame;
import org.fleen.junk.loopingTunnelFlowVFrameSequence.VFrameBubble;
import org.fleen.junk.loopingTunnelFlowVFrameSequence.VFrameCoreBubble;

public class FrameRenderer_TestDisplay extends FrameRenderer_Abstract{
  
  private static final Color 
    COLOR_BACKGROUND=new Color(96,96,96),
    COLOR_FILLROOTBUBBLE_0=new Color(160,160,160),
    COLOR_FILLROOTBUBBLE_1=new Color(96,96,96),
    COLOR_STROKEBUBBLE_0=new Color(128,128,128),
    COLOR_STROKEBUBBLE_1=new Color(128,128,128),
    COLOR_STROKEROOTBUBBLE=new Color(128,128,128);
  
  private static final Color[]
    COLORARRAY0={
    new Color(140,140,140),
    new Color(160,160,160),
    new Color(180,180,180)
    },
    COLORARRAY1={
    new Color(76,76,76),
    new Color(96,96,96),
    new Color(116,116,116)
    };
  
  private static final float 
    STROKETHICKNESS_COREBUBBLE=1.5f,
    STROKETHICKNESS_BUBBLE=1.5f;

  public BufferedImage getImage(VFrame frame,int w,int h,double scale,double rotation,double outermostviewradius){
    Ellipse2D.Double trimcircle=new Ellipse2D.Double(
        -outermostviewradius,
        -outermostviewradius,
        outermostviewradius*2,
        outermostviewradius*2);
    //init
    BufferedImage image=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
    Graphics2D ig=image.createGraphics();
    ig.translate(w/2,h/2);
    ig.scale(scale,-scale);
//    ig.setRenderingHints(RENDERING_HINTS);
    ig.rotate(rotation);
    //fill with background
    int brgb=COLOR_BACKGROUND.getRGB();
    for(int x=0;x<w;x++){
      for(int y=0;y<h;y++){
        image.setRGB(x,y,brgb);}}
    BasicStroke 
      strokecorebubble=new BasicStroke(
        (float)(STROKETHICKNESS_COREBUBBLE/scale),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,0,null,0),
     strokebubble=new BasicStroke(
        (float)(STROKETHICKNESS_BUBBLE/scale),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,0,null,0);
    //fill core bubbles
    for(VFrameCoreBubble bubble:frame.fcorebubbles){
      if(bubble.index%2==0){
        ig.setColor(COLOR_FILLROOTBUBBLE_0);
      }else{
        ig.setColor(COLOR_FILLROOTBUBBLE_1);}
      ig.fill(getPath(bubble.polygon));}
    //stroke bubbles
    ig.setStroke(strokebubble);
    for(VFrameBubble bubble:frame.fbubbles){
      if(bubble.coreindex%2==0){
        ig.setColor(COLOR_STROKEBUBBLE_0);
      }else{
        ig.setColor(COLOR_STROKEBUBBLE_1);}
      ig.draw(getPath(bubble.polygon));}
    //stroke core bubbles
    ig.setStroke(strokecorebubble);
    ig.setColor(COLOR_STROKEROOTBUBBLE);
    for(VFrameCoreBubble bubble:frame.fcorebubbles){
      ig.draw(getPath(bubble.polygon));}
    //draw trim circle
//    ig.setColor(Color.red);
//    ig.draw(trimcircle);
    //
    return image;}

}
