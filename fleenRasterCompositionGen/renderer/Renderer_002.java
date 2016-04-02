package org.fleen.junk.fleenRasterCompositionGen.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

import org.fleen.core.grammaticOLD.GBubble;
import org.fleen.junk.fleenRasterCompositionGen.Composition;
import org.fleen.junk.fleenRasterCompositionGen.Log;


public class Renderer_002 extends Renderer_Abstract{
  
  private static final long serialVersionUID=-8294845813561601629L;

  private static final int ALPHA=255;
  
  static final Color COLOR_POLYGONEDGE=new Color(135,197,249,ALPHA);
  static final Color[] FOAMCOLORS={
    new Color(187,223,233,ALPHA),
    new Color(227,237,215,ALPHA),
    new Color(255,212,97,ALPHA),
    new Color(234,255,119,ALPHA)};
  
  public static final float 
    POLYGON_LINE_STROKE_WIDTH=2.0f;
  
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
