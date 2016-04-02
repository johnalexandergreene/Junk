package org.fleen.junk.azalea;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.fleen.geom_2D.DPoint;
import org.fleen.geom_2D.GD;

/*
 * it may share edge and vertices with another zpolygon
 */
public class ZPolygon implements Iterable<ZPoint>,ZElement{
  
  /*
   * ################################
   * CONSTRUCTORS
   * ################################
   */
  
  public ZPolygon(List<ZPoint> zpoints){
    initPointRoles(zpoints);}
  
  public ZPolygon(ZPoint... zpoints){
    initPointRoles(Arrays.asList(zpoints));}
  
  /*
   * ################################
   * GEOMETRY
   * ################################
   */
  
  //the first point in the loop of points that defines this polygon
  //points are associated by role, which is associated with this polygon
  //to get the polygon we iterate the points, traversing from a point with the appropriate role
  //to the next point with the appropriate role, and so on
  //because we have just the one point, direction of polygon traversal (cw or ccw) is arbitrary
  public ZPoint key;
  
  /*
   * we cache points and segs
   * if we add or remove points then we want to clear this
   */
  public void clearPrimitiveGeometryCache(){
    segs=null;
    points=null;}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * POINT CONTAINMENT TEST
   * ++++++++++++++++++++++++++++++++
   */

  public boolean contains(double x,double y){
    List<ZPoint> points=getPoints();
    return GD.getSide_PointPolygon(x,y,points);}
  
  public boolean contains(double[] p){
    List<ZPoint> points=getPoints();
    return GD.getSide_PointPolygon(p[0],p[1],points);}
  
  public boolean contains(ZPoint p){
    List<ZPoint> points=getPoints();
    return GD.getSide_PointPolygon(p.x,p.y,points);}

  /*
   * ++++++++++++++++++++++++++++++++
   * POINTS
   * gleaned via our graph traversing point iterator
   * cached in a list in clockwise order
   * ++++++++++++++++++++++++++++++++
   */
  
  private List<ZPoint> points=null;
  
  public List<ZPoint> getPoints(){
    if(points==null)initPoints();
    return points;}
  
  private void initPoints(){
    points=new ArrayList<ZPoint>();
    Iterator<ZPoint> i=getPointIterator();
    ZPoint p;
    while(i.hasNext()){
      p=i.next();
      points.add(p);}
    //if it isn't clockwise then make it so
    //after that, rotate it too, so the primary is at index 0 
    if(!GD.isClockwiseD(points)){
      Collections.reverse(points);
      Collections.rotate(points,1);}}
  
  /*
   * implementation of Iterable
   * This DOES NOT return the points in guaranteed clockwise order
   */
  public Iterator<ZPoint> iterator(){
    return getPointIterator();}
  
  //This DOES NOT return the points in guaranteed clockwise order
  public Iterator<ZPoint> getPointIterator(){
    return new ZPolygonPointIterator(this,key);}
  
  public int getPointCount(){
    return getPoints().size();}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * SEGS
   * In clockwise order
   * ++++++++++++++++++++++++++++++++
   */
  
  private List<ZSeg> segs=null;
  
  public List<ZSeg> getSegs(){
    if(segs==null)initSegs();
    return segs;}
  
  public void initSegs(){
    List<ZPoint> points=getPoints();
    int 
      segcount=points.size(),
      inext;
    segs=new ArrayList<ZSeg>(segcount);
    ZSeg seg=null;
    for(int i=0;i<segcount;i++){
      inext=i+1;
      if(inext==segcount)inext=0;
      seg=new ZSeg(points.get(i),points.get(inext));
      segs.add(seg);}}
  
  public int getSegCount(){
    return getSegs().size();}
  
  public ZSeg getSeg(int i){
    return getSegs().get(i);}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * CLOSEST POINT
   * Return the closest point on this polygon to the specified point
   * The point we return in an implicit point, a point on a seg at an offset on that seg
   * It is not a ZPoint
   * 
   * We returns an array of doubles
   * the closest point's coordinates : x,y 
   * and the distance to that point : d 
   * {x,y,d}
   * ++++++++++++++++++++++++++++++++
   */
  
  public double[] getClosestPoint(ZPoint p){
    return getClosestPoint(p.x,p.y);}
  
  public double[] getClosestPoint(double[] p){
    return getClosestPoint(p[0],p[1]);}
  
  public double[] getClosestPoint(double x,double y){
    double[] p,pclosest=null;
    double d,dclosest=Double.MAX_VALUE;
    //for each local polygon get the closest point on that polygon to our system point
    for(ZSeg seg:getSegs()){
      p=GD.getPoint_ClosestOnSegToPoint(seg.p0.x,seg.p0.y,seg.p1.x,seg.p1.y,x,y);
      d=GD.getDistance_PointPoint(x,y,p[0],p[1]);
      if(d<dclosest){
        dclosest=d;
        pclosest=p;}}
    return new double[]{pclosest[0],pclosest[1],dclosest};}
  
  /*
   * return the distance from the clsesnt point on the closest seg in this polygon to the specified point
   */
  public double getDistance(double x,double y){
    double d,dclosest=Double.MAX_VALUE;
    for(ZSeg seg:getSegs()){
      d=GD.getDistance_PointSeg(x,y,seg.p0.x,seg.p0.y,seg.p1.x,seg.p1.y);
      if(d<dclosest)
        dclosest=d;}
    return dclosest;}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * POINT CONTAINMENT TEST
   * ++++++++++++++++++++++++++++++++
   */
  
  public boolean containsPoint(DPoint p){
    Path2D a=getPath2D();
    return a.contains(p.x,p.y);}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * PATH2D
   * ++++++++++++++++++++++++++++++++
   */
  
  public Path2D getPath2D(){
    Path2D path=new Path2D.Double();
    List<ZPoint> points=getPoints();
    DPoint p=points.get(0);
    path.moveTo(p.x,p.y);
    for(int i=1;i<points.size();i++){
      p=points.get(i);
      path.lineTo(p.x,p.y);}
    path.closePath();
    return path;}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * POINT ROLES
   * ++++++++++++++++++++++++++++++++
   */
  
  /*
   * given a list of points
   *   for each point add a role referring to this polygon
   *   link adjacents
   *   
   * be careful. this can easily mess up the geometry
   * so far we use this at polygon init and polygon rebuild
   */
  public void initPointRoles(List<ZPoint> zpoints){
    //get key. it's arbitrary. any point on the polygon will do.
    key=zpoints.get(0);
    //set point roles
    int s=zpoints.size(),iprior,inext;
    ZPoint pprior,p,pnext;
    for(int i=0;i<s;i++){
      iprior=i-1;
      if(iprior==-1)iprior=s-1;
      inext=i+1;
      if(inext==s)inext=0;
      pprior=zpoints.get(iprior);
      p=zpoints.get(i);
      pnext=zpoints.get(inext);
      p.addRole(new ZPointRole(this,pprior,pnext));}}
  
  /*
   * ################################
   * OBJECT
   * ################################
   */
  
  public String toString(){
    StringBuffer a=new StringBuffer();
    a.append("[");
    for(ZPoint p:getPoints())
      a.append(p);
    return a.toString();}
  
//  public String toString(){
//    return this.getClass().getSimpleName()+":"+hashCode();}

}
