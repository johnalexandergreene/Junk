package org.fleen.junk.mesh.spliceMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fleen.core.g2D.G2D;
import org.fleen.junk.mesh.MPoint;
import org.fleen.junk.mesh.MPolygon;
import org.fleen.junk.mesh.MapMetrics;
import org.fleen.junk.mesh.Mesh;

/*
 * 
 * Map MPoints to array of sectors
 * get points between arbitrary point-pairs : p0,p1
 */

public class SpliceMap{
  
  /*
   * ################################
   * CONSTRUCTOR
   * ################################
   */
  
  public SpliceMap(Mesh mesh,MapMetrics metrics,List<MPoint> rawpoints){
    this.metrics=metrics;
    initSectors(rawpoints);}
  
  /*
   * ################################
   * METRICS
   * ################################
   */
  
  MapMetrics metrics;
  
  /*
   * ################################
   * SECTORS
   * A sector is a list of points
   * it contains all of the points in a sector in a grid
   * ################################
   */

  private Sector[][] sectors;
  
  //--------------------------------
  //INIT
  //--------------------------------
  
  private void initSectors(List<MPoint> points){
    sectors=new Sector[metrics.sectorarraywidth][metrics.sectorarrayheight];
    stuffSectors(points);}
  
  private void stuffSectors(List<MPoint> points){
    for(MPoint p:points)
      getSector(p).add(p);}
  
  //--------------------------------
  //ACCESS
  //--------------------------------
  
  private Sector getSector(MPoint p){
    int[] sc=getSectorCoors(p);
    return getSector(sc[0],sc[1]);}
  
  /*
   * returns sector at specified coors
   * return null if coors off map
   * create sector if necessary
   */
  public Sector getSector(int x,int y){
    if(x<0||x>=metrics.sectorarraywidth)return null;
    if(y<0||y>=metrics.sectorarrayheight)return null;
    Sector s=sectors[x][y];
    if(s==null){
      s=new Sector();
      sectors[x][y]=s;}
    return s;}
  
  private int[] getSectorCoors(MPoint p){
    double 
      mapx=p.x-metrics.mapxmin,
      mapy=p.y-metrics.mapymin;
    int
      gridx=(int)(mapx/metrics.sectorspan),
      gridy=(int)(mapy/metrics.sectorspan);
    return new int[]{gridx,gridy};}
  
  public List<MPoint> getSectorPoints(int x,int y){
    Sector s=getSector(x,y);
    if(s!=null)
      return new ArrayList<MPoint>(s);
    else
      return new ArrayList<MPoint>(0);}
  
  /*
   * ################################
   * GET POINTS BETWEEN POINTS
   * given p0 and p1, get the points between p0 and p1 in 
   * order of distance from p0, nearest to farest 
   * ################################
   */
  
  public List<MPoint> getInbetweenPoints(MPoint p0,MPoint p1,MPolygon polygon){
    //get prospective inbetween points
    int[] 
      s0=getSectorCoors(p0),
      s1=getSectorCoors(p1);
    Set<MPoint> prospects=getPointsOnBresenhamLineOfSectors(s0[0],s0[1],s1[0],s1[1]);
    //exclude p0 and p1
    prospects.remove(p0);
    prospects.remove(p1);
    //exclude anything that's already in the polygon, just in case (there are cases when that can happen, like with weird zigzags. right?)
    //is this unnecessary? TODO test it up
    prospects.removeAll(polygon.getPoints());
    //get distance of every prospective inbetween point from p0 and p1
    //we use this to test inbetweenness and sorting
    Map<MPoint,double[]> p0p1dist=getP0P1Distances(p0,p1,prospects);
    //cull non-inbetween points
    cullNonInbetweenPoints(p0,p1,prospects,p0p1dist);
    //convert to list
    List<MPoint> inbetweenpoints=new ArrayList<MPoint>(prospects);
    //sort by distance from p0, closer to further
    Collections.sort(inbetweenpoints,new InbetweenPointComparator(p0p1dist));
    //
    return inbetweenpoints;}
  
  class InbetweenPointComparator implements Comparator<MPoint>{
    
    Map<MPoint,double[]> p0p1dist;
    
    InbetweenPointComparator(Map<MPoint,double[]> p0p1dist){
      this.p0p1dist=p0p1dist;}

    public int compare(MPoint a0,MPoint a1){
      double 
        d0=p0p1dist.get(a0)[0],
        d1=p0p1dist.get(a1)[0];
      if(d0==d1){//TODO check this
        return 0;
      }else if(d0>d1){
        return 1;
      }else{
        return -1;}}}
  
  //note that we use our collision range metric as our inbetweenness error
  private void cullNonInbetweenPoints(MPoint p0,MPoint p1,Set<MPoint> prospects,Map<MPoint,double[]> p0p1dist){
    double totaldist=G2D.getDistance_PointPoint(p0.x,p0.y,p1.x,p1.y);
    Iterator<MPoint> i=prospects.iterator();
    MPoint p;
    double[] distances;
    double test;
    while(i.hasNext()){
      p=i.next();
      distances=p0p1dist.get(p);
      test=Math.abs(totaldist-(distances[0]+distances[1]));
      if(test>metrics.collisionrange)
        i.remove();}}
  
  //--------------------------------
  //GET PO P1 DISTANCES
  
  private Map<MPoint,double[]> getP0P1Distances(MPoint p0,MPoint p1,Set<MPoint> prospects){
    Map<MPoint,double[]> distances=new Hashtable<MPoint,double[]>();
    for(MPoint p:prospects)
      distances.put(p,getP0P1Distances(p0,p1,p));
    return distances;}
  
  private double[] getP0P1Distances(MPoint p0,MPoint p1,MPoint p){
    double 
      d0=G2D.getDistance_PointPoint(p0.x,p0.y,p.x,p.y),
      d1=G2D.getDistance_PointPoint(p1.x,p1.y,p.x,p.y);
    return new double[] {d0,d1};}
  
  /*
   * #####################
   * GET POINTS ON LINE OF SECTORS
   * we use a Bresenham line drawing alg to get a line of sectors
   * given 2 sectors, draw a line of sectors between them
   * for every sector we get the contained points
   * return the points
   * ######################
   */
  
  private Set<MPoint> getPointsOnBresenhamLineOfSectors(int x0,int y0,int x1,int y1){ 
    //if x0y0 is to the right of x1y1 then switch them
    if((x0-x1)>0)
      return getPointsOnBresenhamLineOfSectors(x1,y1,x0,y0); 
    //test slope
    //if steep then use the steepline alg instead
    if(Math.abs(y1-y0)>Math.abs(x1-x0))
      return getPointsOnBresenhamLineOfSectors_Steep(y0,x0,y1,x1);
    //not steep
    Set<MPoint> sectorpoints=new HashSet<MPoint>();
    int 
      x=x0, 
      y=y0, 
      sum=x1-x0, 
      dx=2*(x1-x0), 
      dy=Math.abs(2*(y1-y0));
    int incrementdy=((y1-y0)>0)?1:-1;
    //draw line
    for(int i=0;i<=x1-x0;i++){
      sectorpoints.addAll(getSectorPoints(x,y));
      x++;
      sum-=dy;
      if(sum<0){
        y=y+incrementdy; 
        sum+=dx;}}
    return sectorpoints;}

  private Set<MPoint> getPointsOnBresenhamLineOfSectors_Steep(int x3,int y3,int x4,int y4){
    //if x3y3 is to the right of x4y4 then switch them
    if((x3-x4)>0)
      return getPointsOnBresenhamLineOfSectors_Steep(x4,y4,x3,y3);
    //draw it
    Set<MPoint> sectorpoints=new HashSet<MPoint>();
    int 
      x=x3, 
      y=y3,
      sum=x4-x3,  
      dx=2*(x4-x3), 
      dy=Math.abs(2*(y4-y3));
    int incrementdy=((y4-y3)>0)?1:-1;
    for (int i=0;i<=x4-x3;i++){
      sectorpoints.addAll(getSectorPoints(y,x));//yes, y then x. it works.
      x++;
      sum-=dy;
    if(sum<0){
      y=y+incrementdy; 
      sum+=dx;}}
    return sectorpoints;}
  
  /*
   * ################################
   * OBJECT
   * ################################
   */
  
  public String toString(){
    String s=metrics.toString();
    return s;}
  
}
