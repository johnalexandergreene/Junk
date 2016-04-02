package org.fleen.junk.fleenRasterCompositionGen.renderer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.HashMap;

import org.fleen.core.grammaticOLD.GBubble;
import org.fleen.forsythia.tree.PGFTree;
import org.fleen.junk.fleenRasterCompositionGen.Composition;
import org.fleen.junk.fleenRasterCompositionGen.Log;
import org.fleen.junk.fleenRasterCompositionGen.ui.ImageViewer;

public abstract class Renderer_Abstract implements Serializable{

  private static final long serialVersionUID=6677960572910285947L;
  
  /*
   * ################################
   * RENDER
   * ################################
   */
  
  public static HashMap<RenderingHints.Key,Object> RENDERING_HINTS=
    new HashMap<RenderingHints.Key,Object>();
    
  static{
    RENDERING_HINTS.put(
      RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
    RENDERING_HINTS.put(
      RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
    RENDERING_HINTS.put(
      RenderingHints.KEY_DITHERING,RenderingHints.VALUE_DITHER_DEFAULT);
    RENDERING_HINTS.put(
      RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    RENDERING_HINTS.put(
      RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    RENDERING_HINTS.put(
      RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_QUALITY); 
    RENDERING_HINTS.put(
      RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_NORMALIZE);}
  
  private static final int EXPORT_IMAGE_MARGIN=12;
  
  public BufferedImage renderForExport(Composition composition,double scale){
    Rectangle2D.Double bounds=getPolygonBoundingRect(composition);
    int 
      w=((int)(scale*bounds.getWidth()))+EXPORT_IMAGE_MARGIN*2,
      h=((int)(scale*bounds.getHeight()))+EXPORT_IMAGE_MARGIN*2;
    BufferedImage image=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics=image.createGraphics();
    graphics.addRenderingHints(RENDERING_HINTS);
    AffineTransform transform=new AffineTransform();
    transform.scale(scale,scale);
    transform.translate(
      -bounds.getMinX()+EXPORT_IMAGE_MARGIN/scale,
      -bounds.getMinY()+EXPORT_IMAGE_MARGIN/scale);
    graphics.setTransform(transform);
    render(composition,graphics,transform);
    Log.m1("[finished rendering]");
    return image;}
  
  public BufferedImage renderForViewer(Composition composition,ImageViewer viewer){
    BufferedImage image=new BufferedImage(viewer.getWidth(),viewer.getHeight(),BufferedImage.TYPE_INT_ARGB);
    AffineTransform transform=new AffineTransform();
    transform.scale(viewer.scale,viewer.scale);
    transform.translate(viewer.dx,viewer.dy);
    Graphics2D graphics=image.createGraphics();
    graphics.addRenderingHints(RENDERING_HINTS);
    graphics.setTransform(transform);
    render(composition,graphics,transform);
    Log.m1("[finished rendering]");
    return image;}
  
  protected abstract void render(Composition composition,Graphics2D graphics,AffineTransform transform);
  
  /*
   * ################################
   * UTIL
   * ################################
   */
  
  public static final Rectangle2D.Double getRootBubbleBounds(PGFTree dgc){
    GBubble rootbubble=dgc.getRootBubble();
    double[][] vp=rootbubble.getPolygon();
    double maxx=Double.MIN_VALUE,maxy=maxx,minx=Double.MAX_VALUE,miny=minx;
    for(int i=0;i<vp.length;i++){
      if(minx>vp[i][0])minx=vp[i][0];
      if(miny>vp[i][1])miny=vp[i][1];
      if(maxx<vp[i][0])maxx=vp[i][0];
      if(maxy<vp[i][1])maxy=vp[i][1];}
    return new Rectangle2D.Double(minx,miny,maxx-minx,maxy-miny);}
  
  public static final Path2D.Double getPath2D(GBubble bubble){
    Path2D.Double path2d=new Path2D.Double();
    double[][] lp=bubble.getPolygon();
    path2d.moveTo(lp[0][0],lp[0][1]);
    //
    for(int i=1;i<lp.length;i++)
      path2d.lineTo(lp[i][0],lp[i][1]);
    path2d.closePath();
    return path2d;}
  
  /*
   * ################################
   * OBJECT
   * ################################
   */
  
  public String toString(){
    return this.getClass().getSimpleName();}

}
