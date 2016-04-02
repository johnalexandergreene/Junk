package org.fleen.junk.azalea;

import java.util.Collections;
import java.util.List;

import org.fleen.geom_2D.DPoint;

public class Util{
  
  /*
   * ################################
   * POINT STUFF
   * ################################
   */
  
//  public static final List<MPoint> copy(List<MPoint> points){
//    int s=points.size();
//    List<MPoint> newpoints=new ArrayList<MPoint>(s);
//    MPoint p;
//    for(int i=0;i<s;i++){
//      p=new MPoint(points.get(i));
//      newpoints.add(p);}
//    return newpoints;}
  
  /*
   * ################################
   * CLOCKWISENESS
   * ################################
   */
  
//  public static final void makeClockwise(List<ZPoint> points){
//    if(isClockwise(points))return;
//    Collections.reverse(points);}
  
  
  //TODO this stuff should be in G2D somewhere
  public static final void makeClockwise(List<? extends DPoint> points){
    if(isClockwise(points))return;
    Collections.reverse(points);}
  
  public static final void makeCounterclockwise(List<? extends DPoint> points){
    if(isCounterclockwise(points))return;
    Collections.reverse(points);}
  
  /*
   * get the signed area of the polygon
   * if it's negative then it's clockwise
   */
  public static final boolean isClockwise(List<? extends DPoint> points){
    double sum=0.0;
    int inext,s=points.size();
    DPoint pi,pinext;
    for(int i=0;i<s;i++){
      inext=i+1;
      if(inext==s)inext=0;
      pi=points.get(i);
      pinext=points.get(inext);
      sum=sum+(pi.x*pinext.y)-(pi.y*pinext.x);}
    double signedarea2d=0.5*sum;
    return signedarea2d<0;}
  
  public static final boolean isCounterclockwise(List<? extends DPoint> points){
    return !isClockwise(points);}

}
