package org.fleen.junk.mesh.collisionMap;

import java.util.HashSet;

import org.fleen.core.g2D.Point2D;

@SuppressWarnings("serial")
public class CollisionGroup extends HashSet<Point2D>{
  
  /*
   * ################################
   * CONSTRUCTOR
   * ################################
   */
  
  CollisionGroup(Point2D point){
    add(point);}
  
  /*
   * ################################
   * MERGED POINT
   * The resolution of a 2..n point collision
   * The average of all participating points
   * ################################
   */
  
  private Point2D mergedpoint=null;
  
  public Point2D getMergePoint(){
    if(mergedpoint==null)
      initMergedPoint();
    return mergedpoint;}
  
  private void initMergedPoint(){
    //if this group contains just one point then that's the point
    if(size()==1){
      mergedpoint=new Point2D(iterator().next());
      return;}
    //otherwise we do an average
    double xsum=0,ysum=0;
    for(Point2D p:this){
      xsum+=p.x;
      ysum+=p.y;}
    double s=size();
    xsum/=s;
    ysum/=s;
    mergedpoint=new Point2D(xsum,ysum);}

}
