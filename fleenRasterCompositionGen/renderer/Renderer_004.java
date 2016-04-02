package org.fleen.junk.fleenRasterCompositionGen.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

import org.fleen.core.grammaticOLD.GBubble;
import org.fleen.junk.fleenRasterCompositionGen.Composition;
import org.fleen.junk.fleenRasterCompositionGen.Log;


public class Renderer_004 extends Renderer_Abstract{
  
  private static final long serialVersionUID=-8294845813561601629L;

  private static final int ALPHA=255;
  
  static final Color COLOR_POLYGONEDGE=new Color(231,228,209,ALPHA);
  static final Color[] FOAMCOLORS={
    new Color(234,210,110,ALPHA),
    new Color(237,174,102,ALPHA),
    new Color(185,167,131,ALPHA),
    new Color(155,169,178,ALPHA)};
  
  public static final float 
    POLYGON_LINE_STROKE_WIDTH=1.0f;
  
  protected void render(Composition fleen,Graphics2D graphics,AffineTransform transform){
    if(fleen==null)return;
    //get scaled metrics
    float polygonstrokewidth=(float)(POLYGON_LINE_STROKE_WIDTH/transform.getScaleX());
    //render bubbles
    Path2D path;
    Color c;
    int bcount=0;
    for(GBubble bubble:fleen.getPolygons()){
      bcount++;
      if(bcount%4096==0)Log.m1(".");
      path=getPath2D(bubble);
      //FILL POLYGON
      if(path!=null){
        c=FOAMCOLORS[bubble.foam.getLevel()%FOAMCOLORS.length];
        graphics.setPaint(c);
        graphics.setStroke(new BasicStroke(polygonstrokewidth));
        graphics.fill(path);}
      //STROKE POLYGON
      if(path!=null){
        graphics.setPaint(COLOR_POLYGONEDGE);
        graphics.setStroke(new BasicStroke(polygonstrokewidth));
        graphics.draw(path);}}}

}
