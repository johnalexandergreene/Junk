package org.fleen.junk.mesh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.fleen.core.g2D.G2D;
import org.fleen.core.g2D.Point2D;
import org.fleen.forsythia.composition.FPolygon;
import org.fleen.junk.mesh.collisionMap.CollisionMap;

/*
 * an mpolygon defines an edge of one mshape
 * it may share edge and vertices with another mpolygon
 */
public class MPolygon{
  
  /*
   * ################################
   * CONSTRUCTORS
   * ################################
   */
  
  /*
   * convert npolygon to mpolygon
   * This is used when converting a forsythia (split) composition to a mesh
   */
  public MPolygon(FPolygon np,CollisionMap cm,MShape shape,List<MPoint> rawpoints){
//    this.shape=shape;
    initPoints(np,cm,rawpoints);}
  
  /*
   * clone specified mpolygon
   * new polygon is congruent with param polygon
   * new polygon points are clones of param polygon points
   *   they each have just one role, it is associated with this polygon
   */
  public MPolygon(MPolygon polygon){
    initPoints(polygon);}
  
  /*
   * create mpolygon by adding appropriate roles to specified points
   * and setting head
   */
  public MPolygon(List<MPoint> points){
    initPoints(points);}
  
  public MPolygon(MPoint... points){
    initPoints(Arrays.asList(points));}
  
  //debug
  public MPolygon(String name,MPoint... points){
    initPoints(Arrays.asList(points));
    this.name=name;}
  
  public String name;
  
  /*
   * ################################
   * SHAPE
   * the shape of which this polygon is an edge
   * ################################
   */
  
//  MShape shape;
  
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
  public MPoint head;
  
  public Iterator<MPoint> getPointIterator(){
    return new MPointIterator(this,head);}
  
  public List<MPoint> getPoints(){
    List<MPoint> points=new ArrayList<MPoint>();
    Iterator<MPoint> i=getPointIterator();
    MPoint p;
    while(i.hasNext()){
      p=i.next();
      points.add(p);}
    return points;}
  
  public boolean isClockwise(){
    List<MPoint> points=getPoints();
    List<double[]> b=new ArrayList<double[]>(points.size());
    for(Point2D p:points)
      b.add(new double[]{p.x,p.y});
    double a=G2D.getSignedArea2D(b);
    return a<0;}
  
  /*
   * POINT INITIALIZER FOR INIT VIA FORSYTHIA NPOLYGON
   * get the points of the npolygon
   * glean equivalent points in mesh from collision map
   * set polygon points
   * set polygon point roles 
   */
  private void initPoints(FPolygon npolygon,CollisionMap cm,List<MPoint> rawpoints){
    //get the points of the npolygon
    double[][] npolygonpoints=npolygon.getDPolygon();
    //get equivalent mpoints
    List<MPoint> mpoints=new ArrayList<MPoint>(npolygonpoints.length);
    Point2D collisionmerge;
    MPoint meshpoint;
    for(double[] npolygonpoint:npolygonpoints){
      collisionmerge=cm.getCollisionGroup(npolygonpoint[0],npolygonpoint[1]).getMergePoint();
      meshpoint=getPointForInit(collisionmerge.x,collisionmerge.y,rawpoints);
      mpoints.add(meshpoint);}
    //
    initPoints(mpoints);}
  
  private MPoint getPointForInit(double x,double y,List<MPoint> rawpoints){
    for(MPoint p:rawpoints)
      if(p.x==x&&p.y==y)return p;
    throw new IllegalArgumentException("MPoint with specified coordinates ("+x+","+y+") does not exist in this list");}
  
  /*
   * POINT INITIALIZER FOR CLONE
   * clone the specified mpolygon by cloning all of its points
   * each point has just one role, referring to this polygon
   * new polygon is congruent with old polygon
   */
  private void initPoints(MPolygon oldpolygon){
    List<MPoint>
      oldpoints=oldpolygon.getPoints(),
      newpoints=new ArrayList<MPoint>(oldpoints.size());
    for(MPoint p:oldpoints)
      newpoints.add(new MPoint(p.x,p.y));
    initPoints(newpoints);}
  
  /*
   * POINT INITIALIZER FOR LIST OF MPOINTS
   * given a list of points
   *   for each point add a role referring to this polygon
   *   link adjacents
   */
  private void initPoints(List<MPoint> mpoints){
    //get head. it's arbitrary. any point on the polygon will do.
    head=mpoints.get(0);
    //set point roles
    int s=mpoints.size(),iprior,inext;
    MPoint pprior,p,pnext;
    for(int i=0;i<s;i++){
      iprior=i-1;
      if(iprior==-1)iprior=s-1;
      inext=i+1;
      if(inext==s)inext=0;
      pprior=mpoints.get(iprior);
      p=mpoints.get(i);
      pnext=mpoints.get(inext);
      p.addRole(new MPointRole(this,pprior,pnext));}}
  
  public int getPointCount(){
    return getPoints().size();}
  
  public int getSegCount(){
    return getPointCount();}
  
  public MSeg getSeg(int i){
    List<MPoint> points=getPoints();
    int 
      s=points.size(),
      inext=i+1;
    if(inext==s)inext=0;
    MSeg seg=new MSeg(points.get(i),points.get(inext));
    return seg;}
  
  public List<MSeg> getSegs(){
    List<MSeg> segs=new ArrayList<MSeg>();
    List<MPoint> points=getPoints();
    int 
      s=points.size(),
      inext;
    MSeg seg;
    for(int i=0;i<s;i++){
      inext=i+1;
      if(inext==s)inext=0;
      seg=new MSeg(points.get(i),points.get(inext));
      segs.add(seg);}
    return segs;}
  
  /*
   * ################################
   * OBJECT
   * ################################
   */
  
  public String toString(){
    return "["+name+"]";
  }
  
}
