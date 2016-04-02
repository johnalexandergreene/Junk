package org.fleen.junk.fleenRasterCompositionGen.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import org.fleen.junk.fleenRasterCompositionGen.FRCG;
import org.fleen.junk.fleenRasterCompositionGen.command.CQ;
import org.fleen.junk.fleenRasterCompositionGen.renderer.Renderer_Abstract;

/*
 * VIEW MODEL CONTROL BY MOUSE:
 * mouseleftclick+dragaround to pan
 * shift+mouseleftclick+dragupdown to scale
 * 
 * on pan we modify the deltas and redraw
 * on scale we modify the scale, rerender the image and redraw 
 */
@SuppressWarnings("serial")
public class ImageViewer extends JPanel{
  
  public ImageViewer(){
    super();
    addMouseListener(new MouseListener0());}
  
  /*
   * ################################
   * VIEW GEOMETRY
   * ################################
   */
  
 private  static final int FIT_MARGIN=12;
  
  public double scale=1.0;
  public double dx=0,dy=0;
  
  public void centerAndFit(){
    Rectangle2D.Double bounds=Renderer_Abstract.getPolygonBoundingRect(FRCG.instance.composition);
    setScaleForFit(bounds);
    setDeltasForCenter(bounds);}
  
  private void setScaleForFit(Rectangle2D.Double rbbounds){
    double 
      sx=((double)(getWidth()-FIT_MARGIN*2))/rbbounds.getWidth(),
      sy=((double)(getHeight()-FIT_MARGIN*2))/rbbounds.getHeight();
    scale=Math.min(sx,sy);}
  
  private void setDeltasForCenter(Rectangle2D.Double rbbounds){
    dx=-rbbounds.getMinX()+((getWidth()/scale)-rbbounds.getWidth())/2.0;
    dy=-rbbounds.getMinY()+((getHeight()/scale)-rbbounds.getHeight())/2.0;}
  
  /*
   * ################################
   * VIEW CONTROL BY MOUSE
   * ################################
   */
  
   static final int 
     DRAGMODE_NONE=0,
     DRAGMODE_PAN=1,
     DRAGMODE_SCALE=2;
   
   private int dragmode=DRAGMODE_NONE;
   private java.awt.Point 
     pmousedown,
     pmouseup;
  
   //START MOUSELISTENER0 
  class MouseListener0 extends MouseAdapter{
    
    public void mousePressed(MouseEvent e){
      requestFocus();
      e.consume();
      pmousedown=new Point(e.getX(),e.getY());
      boolean shift=e.isShiftDown();
      int button=e.getButton();
      if(button==MouseEvent.BUTTON1){
        //LEFTCLICK + SHIFT
        if(shift){
          dragmode=DRAGMODE_SCALE;
        //JUST LEFTCLICK
        }else{
          dragmode=DRAGMODE_PAN;}}}
  
    public void mouseReleased(MouseEvent e){
      pmouseup=e.getPoint();
      if(dragmode!=DRAGMODE_NONE)doDragOp();}}
  //END MOUSELISTENER0
  
  private void doDragOp(){
    try{
    double dragx=pmouseup.x-pmousedown.x;
    double dragy=pmouseup.y-pmousedown.y;
    if(dragmode==DRAGMODE_PAN){
      translateByMouse(dragx,dragy);
    }else{//dragMode==DRAGMODE_SCALE
      scaleByMouse(dragy);}
    dragmode=DRAGMODE_NONE;
    }catch(Exception e){
      e.printStackTrace();}}
  
  private void translateByMouse(double dragx,double dragy){
    dx+=dragx/scale;
    dy+=dragy/scale;
    CQ.renderForViewer();}
  
  /*
   * positive drag (downwards) = zoom in, negative drag (upwards) = zoom out
   * full positive drag doubles scale
   * full negative drag halves scale
   */
  private void scaleByMouse(double dragy){
    double ds;
    double dragmagnitude=Math.abs(dragy)/(double)getHeight();
    if(dragy>0){
      ds=dragmagnitude+1.0;
    }else{
      ds=1.0-(dragmagnitude*0.5);}
    dx/=ds;
    dy/=ds;
    scale*=ds;
    CQ.renderForViewer();}
  
  /*
   * ################################
   * IMAGE
   * ################################
   */
  
  public static final Color COLOR_BACKGROUND=new Color(0,0,0);
  
  public void update(){
    repaint();}
  
  public void paint(Graphics g){
    g.setColor(COLOR_BACKGROUND);
    g.fillRect(0,0,getWidth(),getHeight());
    if(FRCG.instance!=null&&FRCG.instance.viewerimage!=null){
      g.drawImage(FRCG.instance.viewerimage,0,0,null);}}
  
  

}
