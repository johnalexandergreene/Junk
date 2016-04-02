package org.fleen.junk.genRainbowTriumph;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import org.fleen.geom_Kisrhombille.KCell;
import org.fleen.geom_Kisrhombille.KVertex;

public class RTCell{
  
  /*
   * ################################
   * CONSTRUCTOR
   * ################################
   */
  
  public RTCell(RTCellBlock block,int ant,int bat,int cat,int dog){
    dcell=new KCell(ant,bat,cat,dog);
    this.block=block;
    initCoorGeom();
    initPoints();
    initPath();}
  
  /*
   * ################################
   * COORDINATES
   * We have 6 values for 6 axii
   * Visualize contiguous straight-edged bands perpendicular to a particular axis
   * each value on that axis is a band
   * the coordinate names corrospond to axis indices
   * eg : c0 describes location on the 0 axis. Axis 0 is a line running in directions 0 and 6. 
   * ################################
   */
  
  public static final int AXISCOUNT=6;
  public static final int 
    AX0=0,AX1=1,AX2=2,AX3=3,AX4=4,AX5=5;
  public int ax0,ax1,ax2,ax3,ax4,ax5;
  
  private void initCoorGeom(){
    switch(dcell.dog){
    case 0:
      ax0=dcell.cat; 
      ax1=dcell.bat+dcell.cat;
      ax2=dcell.bat;
      ax3=dcell.ant+dcell.bat;
      ax4=dcell.ant-1;
      ax5=dcell.ant-dcell.cat-1;
      return;
    case 1:
      ax0=dcell.cat;
      ax1=dcell.bat+dcell.cat;
      ax2=dcell.bat;
      ax3=dcell.ant+dcell.bat;
      ax4=dcell.ant;
      ax5=dcell.ant-dcell.cat-1;
      return;
    case 2:
      ax0=dcell.cat;
      ax1=dcell.bat+dcell.cat;
      ax2=dcell.bat;
      ax3=dcell.ant+dcell.bat;
      ax4=dcell.ant;
      ax5=dcell.ant-dcell.cat;
      return;
    case 3:
      ax0=dcell.cat-1;
      ax1=dcell.bat+dcell.cat;
      ax2=dcell.bat;
      ax3=dcell.ant+dcell.bat;
      ax4=dcell.ant;
      ax5=dcell.ant-dcell.cat;
      return;
    case 4:
      ax0=dcell.cat-1;
      ax1=dcell.bat+dcell.cat-1;
      ax2=dcell.bat;
      ax3=dcell.ant+dcell.bat;
      ax4=dcell.ant;
      ax5=dcell.ant-dcell.cat;
      return;
    case 5:
      ax0=dcell.cat-1;
      ax1=dcell.bat+dcell.cat-1;
      ax2=dcell.bat-1;
      ax3=dcell.ant+dcell.bat;
      ax4=dcell.ant;
      ax5=dcell.ant-dcell.cat;
      return;
    case 6:
      ax0=dcell.cat-1;
      ax1=dcell.bat+dcell.cat-1;
      ax2=dcell.bat-1;
      ax3=dcell.ant+dcell.bat-1;
      ax4=dcell.ant;
      ax5=dcell.ant-dcell.cat;
      return;
    case 7:
      ax0=dcell.cat-1;
      ax1=dcell.bat+dcell.cat-1;
      ax2=dcell.bat-1;
      ax3=dcell.ant+dcell.bat-1;
      ax4=dcell.ant-1;
      ax5=dcell.ant-dcell.cat;
      return;
    case 8:
      ax0=dcell.cat-1;
      ax1=dcell.bat+dcell.cat-1;
      ax2=dcell.bat-1;
      ax3=dcell.ant+dcell.bat-1;
      ax4=dcell.ant-1;
      ax5=dcell.ant-dcell.cat-1;
      return;
    case 9:
      ax0=dcell.cat;
      ax1=dcell.bat+dcell.cat-1;
      ax2=dcell.bat-1;
      ax3=dcell.ant+dcell.bat-1;
      ax4=dcell.ant-1;
      ax5=dcell.ant-dcell.cat-1;
      return;
    case 10:
      ax0=dcell.cat;  
      ax1=dcell.bat+dcell.cat;
      ax2=dcell.bat-1;
      ax3=dcell.ant+dcell.bat-1;
      ax4=dcell.ant-1;
      ax5=dcell.ant-dcell.cat-1;
      return;
    case 11:
      ax0=dcell.cat;  
      ax1=dcell.bat+dcell.cat;
      ax2=dcell.bat;
      ax3=dcell.ant+dcell.bat-1;
      ax4=dcell.ant-1;
      ax5=dcell.ant-dcell.cat-1;
      return;}}
  
  /*
   * ################################
   * CELLBLOCK
   * of which this cell is a member
   * ################################
   */
  
  RTCellBlock block;
  
  /*
   * ################################
   * GEOMETRY
   * The relevant DCell and a bit of geometry data cache
   * ################################
   */
  
  //this CDCell's form in the diamond grid
  public KCell dcell;
  //the cell triangle's 3 vertex points
  public double[] pv12,pv6,pv4;
  //cell triangle center point in real terms unscaled
  public double[] pcenter;
  
  public Path2D.Double path=null;
  
  private void initPoints(){
    KVertex[] v=dcell.getVertices();
    pv12=v[0].getBasicPointCoor();
    pv6=v[1].getBasicPointCoor();
    pv4=v[2].getBasicPointCoor();
    //scale and TODO offset
    pv12[0]*=block.scale;
    pv12[1]*=block.scale;
    pv6[0]*=block.scale;
    pv6[1]*=block.scale;
    pv4[0]*=block.scale;
    pv4[1]*=block.scale;
    //center
    pcenter=new double[]{
      (pv12[0]+pv6[0]+pv4[0])/3.0,
      (pv12[1]+pv6[1]+pv4[1])/3.0};}
  
  /*
   * ################################
   * GRAPHICS
   * cell handles it's own rendering
   * color is an index
   * ################################
   */
  
  int colorindex=0,priorcolor;
  boolean flagforincrement=false;
  
  public void render(Graphics2D g){
    if(priorcolor==colorindex)return;
    g.setColor(block.palette.getColor(colorindex));
    g.fill(path);}
  
  public void incrementColor(){
    priorcolor=colorindex;
    colorindex++;
    if(colorindex==block.palette.getColorCount())
      colorindex=0;}
  
  public void decrementColor(){
    priorcolor=colorindex;
    colorindex--;
    if(colorindex==-1)
      colorindex=block.palette.getColorCount()-1;}
  
  private void initPath(){
    path=new Path2D.Double();
    path.moveTo(pv12[0],pv12[1]);
    path.lineTo(pv6[0],pv6[1]);
    path.lineTo(pv4[0],pv4[1]);
    path.closePath();}
  
  /*
   * ################################
   * OBJECT
   * ################################
   */
  
  public int hashCode(){
    return dcell.hashCode();}
  
  public boolean equals(Object a){
    return ((RTCell)a).dcell.equals(dcell);}
  
  public String toString(){
    return dcell.toString();}

}
