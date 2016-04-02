package org.fleen.junk.genLoopingTunnelFlowFrameSeqImage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import org.fleen.junk.loopingTunnelFlowVFrameSequence.VFrame;
import org.fleen.junk.loopingTunnelFlowVFrameSequence.VFrameBubble;
import org.fleen.junk.loopingTunnelFlowVFrameSequence.VFrameCoreBubble;

public class FrameRenderer{
  
  private static HashMap<RenderingHints.Key,Object> RENDERING_HINTS=
    new HashMap<RenderingHints.Key,Object>();
      
  static{
    RENDERING_HINTS.put(
      RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
    RENDERING_HINTS.put(
      RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
    RENDERING_HINTS.put(
      RenderingHints.KEY_DITHERING,RenderingHints.VALUE_DITHER_ENABLE);
    RENDERING_HINTS.put(
      RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    RENDERING_HINTS.put(
      RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    RENDERING_HINTS.put(
      RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_QUALITY); 
    RENDERING_HINTS.put(
      RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_NORMALIZE);}
    
  /*
    CCOREFILL0={78,205,196},
    CCOREFILL1={255,107,107},
    CCORESTROKE={199,244,100},
//    CCOREBACKSTROKE={0,0,0},
    CBRANCHSTROKE0={85,98,112},
    CBRANCHSTROKE1={196,77,88};
   */
  
  private static final Color 
    COLOR_BACKGROUND=new Color(0,0,0),
    CCOREFILL0=new Color(78,205,196),
    CCOREFILL1=new Color(255,107,107),
    CCORESTROKE=new Color(199,244,100),
//    CCOREBACKSTROKE=new Color(234,105,245),
    CBRANCHSTROKE0=new Color(85,98,112),
    CBRANCHSTROKE1=new Color(196,77,88);
  
  private static final float 
    STROKETHICKNESS_CORE=12.0f,
    STROKETHICKNESS_BUBBLE0=6.0f,
    STROKETHICKNESS_BUBBLE1=6.0f;

  public BufferedImage getImage(VFrame frame,int w,int h,double scale,double rotation,double outermostradius){
    //init
    BufferedImage image=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
    Graphics2D ig=image.createGraphics();
    ig.setRenderingHints(RENDERING_HINTS);
    //fill background
    ig.setColor(COLOR_BACKGROUND);
    ig.fillRect(0,0,w,h);
    //transform to fit viewport
    ig.translate(w/2,h/2);
    ig.scale(scale,-scale);
//    ig.rotate(rotation);
    //
    BasicStroke 
      basicstrokecore=new BasicStroke(
        (float)(STROKETHICKNESS_CORE/scale),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,0,null,0),
      basicstrokebranch0=new BasicStroke(
        (float)(STROKETHICKNESS_BUBBLE0/scale),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,0,null,0),
      basicstrokebranch1=new BasicStroke(
        (float)(STROKETHICKNESS_BUBBLE1/scale),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,0,null,0),
      dashedstrokecore=new BasicStroke(
        (float)(STROKETHICKNESS_CORE/scale),
        BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,0,
        new float[]{(float)(12.0/scale),(float)(22.0/scale)},0),
      dashedstrokebranch0=new BasicStroke(
        (float)(STROKETHICKNESS_BUBBLE0/scale),
        BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,0,
        new float[]{(float)(11.0/scale),(float)(22.0/scale)},0),
      dashedstrokebranch1=new BasicStroke(
        (float)(STROKETHICKNESS_BUBBLE1/scale),
        BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,0,
        new float[]{(float)(11.0/scale),(float)(22.0/scale)},0);
    //fill core bubbles
    for(VFrameCoreBubble bubble:frame.fcorebubbles){
      if(bubble.index%2==0){
        ig.setColor(CCOREFILL0);
      }else{
        ig.setColor(CCOREFILL1);}
      ig.fill(getPath(bubble.polygon));}
    //stroke bubbles
    for(VFrameBubble bubble:frame.fbubbles){
      if(bubble.coreindex%2==0){
        ig.setStroke(basicstrokebranch0);
        ig.setColor(CBRANCHSTROKE0);
      }else{
        ig.setStroke(basicstrokebranch1);
        ig.setColor(CBRANCHSTROKE1);}
      ig.draw(getPath(bubble.polygon));}
    //stroke core bubbles
//    ig.setStroke(basicstrokecore);
//    ig.setColor(CCOREBACKSTROKE);
//    for(VFrameCoreBubble bubble:frame.fcorebubbles)
//      if(bubble.index!=0)
//        ig.draw(getPath(bubble.polygon));
    ig.setStroke(basicstrokecore);
    ig.setColor(CCORESTROKE);
    for(VFrameCoreBubble bubble:frame.fcorebubbles)
      if(bubble.index!=0)
        ig.draw(getPath(bubble.polygon));
    return image;}
  
  private Path2D.Double getPath(double[][] polygon){
    Path2D.Double p=new Path2D.Double();
    p.moveTo(polygon[0][0],polygon[0][1]);
    for(int i=1;i<polygon.length;i++)
      p.lineTo(polygon[i][0],polygon[i][1]);
    p.closePath();
    return p;}

}
