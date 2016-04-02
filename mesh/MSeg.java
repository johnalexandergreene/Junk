package org.fleen.junk.mesh;

import org.fleen.core.g2D.G2D;

public class MSeg{
  
  
  /*
   * ################################
   * CONSTRUCTORS
   * ################################
   */
  
  public MSeg(MPoint p0,MPoint p1){
    this.p0=p0;
    this.p1=p1;}
  
  /*
   * ################################
   * GEOMETRY
   * ################################
   */
  
  public MPoint p0,p1;
  
  /*
   * returns the direction from p0 to p1
   */
  public double getForeward(){
    return G2D.getDirection_PointPoint(p0.x,p0.y,p1.x,p1.y);}
  
  public double getLength(){
    return G2D.getDistance_PointPoint(p0.x,p0.y,p1.x,p1.y);}
  
  /*
   * ################################
   * OBJECT
   * ################################
   */
  
  public int hashCode(){
    return p0.hashCode()+p1.hashCode();}
  
  //TODO if we use msegs for anything weird this might fuck us up
  public boolean equals(Object a){
    MSeg b=(MSeg)a;
    return(b.p0==p0&&b.p1==p1)||(b.p1==p0&&b.p0==p1);}
  
}
