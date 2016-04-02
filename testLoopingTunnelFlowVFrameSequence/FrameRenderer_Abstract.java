package org.fleen.junk.testLoopingTunnelFlowVFrameSequence;

import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import org.fleen.junk.loopingTunnelFlowVFrameSequence.VFrame;

public abstract class FrameRenderer_Abstract{
  
  protected static HashMap<RenderingHints.Key,Object> RENDERING_HINTS=
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
  
  public abstract BufferedImage getImage(VFrame frame,int w,int h,double scale,double rotation,double outermostviewradius);
  
  protected Path2D.Double getPath(double[][] polygon){
    Path2D.Double p=new Path2D.Double();
    p.moveTo(polygon[0][0],polygon[0][1]);
    for(int i=1;i<polygon.length;i++)
      p.lineTo(polygon[i][0],polygon[i][1]);
    p.closePath();
    return p;}

}
