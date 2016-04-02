package org.fleen.junk.mesh.collisionMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fleen.core.g2D.Point2D;
import org.fleen.forsythia.composition.FPolygon;
import org.fleen.forsythia.composition.ForsythiaComposition;
import org.fleen.forsythia.composition.ForsythiaTreeNode;
import org.fleen.junk.mesh.MapMetrics;

/*
 * 
 * Test collection of points for collisions
 *
 * classify points by sector for fast collision check
 *    
 * for each point, test for collisions with other points
 * test the point against every point in sector and every point in surrounding 8 sectors.
 * group points by collision
 *   that is, if A and B collide and B and C collide then A,B,C are in the same group
 *
 * now we have points grouped by collision
 *
 * for each collision group create a collision-resolution point
 *   the resolution point replaces the colliding points, merges them
 *   the resolution point is the average of all points in the group
 * 
 */

public class CollisionMap{
  
  /*
   * ################################
   * CONSTRUCTOR
   * ################################
   */
  
  public CollisionMap(ForsythiaComposition fc,MapMetrics metrics){
    System.out.println("&&&COLLISION MAP STARTED");
    this.metrics=metrics;
    List<Point2D> rawpoints=getRawPoints(fc);
    initSectors(rawpoints);
    createCollisionGroups(rawpoints);
    System.out.println("---COLLISION MAP FINISHED");
    System.out.println(this);
    }
  
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
  
  private void initSectors(List<Point2D> rawpoints){
    sectors=new Sector[metrics.sectorarraywidth][metrics.sectorarrayheight];
    stuffSectors(rawpoints);}
  
  private void stuffSectors(List<Point2D> rawpoints){
    for(Point2D p:rawpoints)
      getSector(p).add(p);}
  
  //--------------------------------
  //ACCESS
  //--------------------------------
  
  private Sector getSector(Point2D p){
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
  
  private int[] getSectorCoors(Point2D p){
    double 
      mapx=p.x-metrics.mapxmin,
      mapy=p.y-metrics.mapymin;
    int
      gridx=(int)(mapx/metrics.sectorspan),
      gridy=(int)(mapy/metrics.sectorspan);
    return new int[]{gridx,gridy};}
  
  /*
   * ################################
   * COLLISION GROUPS
   * remove an arbitrary point from rawpoints.
   * create a collision group from it
   * check for collisions. 
   * every colliding point, remove from rawpoints 
   * ################################
   */
  
  //collision groups keyed by the points that they contain
  //so we can get the group for any raw point
  private Map<Point2D,CollisionGroup> collisiongroupsbypoint=new Hashtable<Point2D,CollisionGroup>();
  
  private void createCollisionGroups(List<Point2D> points){
    Set<Point2D> ungroupedpoints=new HashSet<Point2D>(points);
    Point2D point;
    CollisionGroup cg;
    while(!ungroupedpoints.isEmpty()){
      //create group
      point=ungroupedpoints.iterator().next();
      cg=new CollisionGroup(point);
      //stuff it
      stuffCollisionGroup(cg);
      //associate group with each of its contained points
      for(Point2D p:cg)
        collisiongroupsbypoint.put(p,cg);
      //remove group from ungrouped
      ungroupedpoints.removeAll(cg);}}
  
  private void stuffCollisionGroup(CollisionGroup cg){
    boolean gathered=true;
    while(gathered)
      gathered=gatherCollisions(cg);}
  
  //returns true if new collisions were gathered
  private boolean gatherCollisions(CollisionGroup cg){
    List<Point2D> gathered=new ArrayList<Point2D>(100);//we init to an arbitrary value above the probable number of points in group, for speed
    int[] scoor;
    for(Point2D p:cg){
      scoor=getSectorCoors(p);
      gathered.addAll(gatherCollisionsInSector(p,cg,scoor[0]-1,scoor[1]-1));
      gathered.addAll(gatherCollisionsInSector(p,cg,scoor[0],scoor[1]-1));
      gathered.addAll(gatherCollisionsInSector(p,cg,scoor[0]+1,scoor[1]-1));
      gathered.addAll(gatherCollisionsInSector(p,cg,scoor[0]-1,scoor[1]));
      gathered.addAll(gatherCollisionsInSector(p,cg,scoor[0],scoor[1]));
      gathered.addAll(gatherCollisionsInSector(p,cg,scoor[0]+1,scoor[1]));
      gathered.addAll(gatherCollisionsInSector(p,cg,scoor[0]-1,scoor[1]+1));
      gathered.addAll(gatherCollisionsInSector(p,cg,scoor[0],scoor[1]+1));
      gathered.addAll(gatherCollisionsInSector(p,cg,scoor[0]+1,scoor[1]+1));}
    cg.addAll(gathered);
    return !gathered.isEmpty();}
  
  List<Point2D> gatherCollisionsInSector(Point2D p,CollisionGroup cg,int scoorx,int scoory){
    List<Point2D> collisions=new ArrayList<Point2D>();
    Sector s=getSector(scoorx,scoory);
    //test all points in the sector
    if(s!=null)
      for(Point2D p0:s){
        //if p0 is not already in the group and p0 
        //collides with p then add p0 to the group
        if((!cg.contains(p0))&&collision(p,p0,metrics.collisionrange))
          collisions.add(p0);}
    return collisions;}
  
  boolean collision(Point2D p0,Point2D p1,double range){
    return (Math.abs(p0.x-p1.x)<range&&Math.abs(p0.y-p1.y)<range);}
  
  /*
   * return the collision group that contains the specified point
   */
  public CollisionGroup getCollisionGroup(Point2D p){
    return collisiongroupsbypoint.get(p);}
  
  /*
   * return the collision group that contains the specified point
   */
  public CollisionGroup getCollisionGroup(double x,double y){
    for(Point2D p:collisiongroupsbypoint.keySet())
      if(p.x==x&&p.y==y)
        return collisiongroupsbypoint.get(p);
    return null;}
  
  /*
   * returns the merge points of all collision groups
   * note that we return a set, to cull dupes
   * because we have much duplication of groups in the map
   */
  public Set<Point2D> getCollisionGroupMergePoints(){
    Set<Point2D> p=new HashSet<Point2D>();
    for(CollisionGroup cg:collisiongroupsbypoint.values())
      p.add(cg.getMergePoint());
    return p;}
  
  /*
   * ################################
   * UTIL
   * ################################
   */
  
  /*
   * return all points of all NPolygons in the composition converted to Point2D
   */
  private List<Point2D> getRawPoints(ForsythiaComposition fc){
    List<Point2D> points=new ArrayList<Point2D>();
    Iterator<ForsythiaTreeNode> i=fc.getPolygonIterator();
    FPolygon np;
    while(i.hasNext()){
      np=(FPolygon)i.next();
      for(double[] p:np.getDPolygon())
        points.add(new Point2D(p));}
    return points;}
  
  /*
   * ################################
   * OBJECT
   * ################################
   */
  
  public String toString(){
    String s=metrics.toString();
    return s;}
  
}
