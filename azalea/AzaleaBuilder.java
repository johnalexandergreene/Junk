package org.fleen.junk.azalea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fleen.forsythia.composition.FPolygon;
import org.fleen.geom_2D.DPoint;
import org.fleen.geom_2D.DPointCollisionGroup;
import org.fleen.geom_2D.DPolygon;
import org.fleen.geom_2D.GD;
import org.fleen.util.tree.TreeNode;

  /*
   * ################################
   * AZALEA BUILDER
   * 
   * get forsythia fpolygons
   * from those get all the dpoints 
   * group dpoints by collision
   * from grouped dpoints and fpolygons create zpolygons and zpoints
   * splice zpolygons
   * group zpolygons by inter-adjacentless. The groups are our protolattices.
   *   If a point plays a role in polygons X and Y, then X and Y are part of the same group
   *   These protolattices are our zlattice inner zpolygons unordered collections
   * for each protolattice
   *   derive an outer edge zpolygon 
   *   create a zlattice
   * order zlattices by nestingness
   * create zyards, link up zlattices and zyards into a tree 
   * 
   * ################################
   */

public class AzaleaBuilder{
  
  AzaleaBuilder(Azalea azalea){
    this.azalea=azalea;
    createDPolygons();
    createDPointCollisionGroups();
    createZPolygonsAndZPoints();
    spliceZPolygons();
    List<Set<ZPolygon>> protolattices=createProtoLattices();
    createZLattices(protolattices);
    stripPointRolesForUnusedZPolygons();
    buildYardlessLatticeTree();
    createYards();
  
  }
  
  /*
   * ################################
   * AZALEA
   * ################################
   */
  
  Azalea azalea;
  
  /*
   * ################################
   * DPOLYGONS
   * For each FPolygon in the forsythia we have a DPolygon
   * map them both ways
   * ################################
   */
  
  Map<DPolygon,FPolygon> fpolygonbydpolygon=new Hashtable<DPolygon,FPolygon>();
  Map<FPolygon,DPolygon> dpolygonbyfpolygon=new Hashtable<FPolygon,DPolygon>();
  
  private void createDPolygons(){
    List<FPolygon> fpolygons=azalea.forsythia.getPolygons();
    DPolygon dpolygon;
    for(FPolygon fpolygon:fpolygons){
      dpolygon=fpolygon.getDPolygon();
      fpolygonbydpolygon.put(dpolygon,fpolygon);
      dpolygonbyfpolygon.put(fpolygon,dpolygon);}}
  
  /*
   * ################################
   * DPOINT COLLISION GROUPS
   * When dpoints are close enough they are considered to be in collision
   * we group them by collision. Little groups of colliding points.
   * we map each dpoint to its collision group
   * from the resolution of these collisions we accquire our zpoints
   * ################################
   */
  
  Map<DPoint,DPointCollisionGroup> collisiongroupbydpoint=new Hashtable<DPoint,DPointCollisionGroup>();
  
  private void createDPointCollisionGroups(){
    //get the pool of ungrouped 2dpoints
    Set<DPoint> ungrouped=new HashSet<DPoint>();
    for(DPolygon p:dpolygonbyfpolygon.values())
      ungrouped.addAll(p);
    //map each of those 2d points to a collision group
    while(!ungrouped.isEmpty())
      createCollisionGroup(ungrouped);}
  
  private void createCollisionGroup(Set<DPoint> ungrouped){
    //create group with a first member point
    DPoint dp=ungrouped.iterator().next();
    ungrouped.remove(dp);
    DPointCollisionGroup group=new DPointCollisionGroup(azalea.getPointCollisionDistance(),dp);
    collisiongroupbydpoint.put(dp,group);
    //test each point in the ungrouped points set against the group
    //if the test point is in collision with the group then
    //  remove that point from the ungrouped points set
    //  add it to the group
    //keep adding points until it occurs that we have tested all points in 
    //  ungrouped without adding one to the group. Then we're done.
    boolean pointswereaddedtogroup=true;
    Iterator<DPoint> i;
    while(pointswereaddedtogroup&&!ungrouped.isEmpty()){
      pointswereaddedtogroup=false;
      i=ungrouped.iterator();
      SEEK:while(i.hasNext()){
        dp=i.next();
        if(group.collision(dp)){
          i.remove();
          group.add(dp);
          collisiongroupbydpoint.put(dp,group);
          pointswereaddedtogroup=true;
          break SEEK;}}}}
  
  /*
   * ################################
   * ZPOLYGONS AND ZPOINTS
   * We derive them from the DPolygons and the resolution points of the DPolygon collision groups.
   * We keep the zpoints in the zpolygons
   * we map dpolygons and zpolygons both ways
   * ################################ 
   */
  
  Map<DPolygon,ZPolygon> zpolygonbydpolygon=new Hashtable<DPolygon,ZPolygon>();
  Map<ZPolygon,DPolygon> dpolygonbyzpolygon=new Hashtable<ZPolygon,DPolygon>();
  
  private void createZPolygonsAndZPoints(){
    ZPolygon zpolygon;
    for(DPolygon dpolygon:dpolygonbyfpolygon.values()){
      zpolygon=createZPolygon(dpolygon);
      dpolygonbyzpolygon.put(zpolygon,dpolygon);
      zpolygonbydpolygon.put(dpolygon,zpolygon);}}
  
  private ZPolygon createZPolygon(DPolygon dpolygon){
    List<ZPoint> zpoints=new ArrayList<ZPoint>();
    ZPoint zpoint;
    DPointCollisionGroup g;
    for(DPoint dpoint:dpolygon){
      g=collisiongroupbydpoint.get(dpoint);
      zpoint=getCollisionResolutionZPoint(g);
      zpoints.add(zpoint);}
    ZPolygon z=new ZPolygon(zpoints);
    return z;}
  
  public Set<ZPoint> getZPoints(){
    Set<ZPoint> points=new HashSet<ZPoint>();
    for(ZPolygon polygon:zpolygonbydpolygon.values())
      points.addAll(polygon.getPoints());
    return points;}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * DPOINT COLLISION GROUP RESOLUTION ZPOINT
   * We do this so for every collision group there is just the one zpoint
   * ++++++++++++++++++++++++++++++++
   */
  
  private Map<DPointCollisionGroup,ZPoint> zpointbycollision=new Hashtable<DPointCollisionGroup,ZPoint>();
  
  private ZPoint getCollisionResolutionZPoint(DPointCollisionGroup g){
    ZPoint p=zpointbycollision.get(g);
    if(p==null){
      p=new ZPoint(g.getResolution());
      zpointbycollision.put(g,p);}
    return p;}
  
  /*
   * ################################
   * SPLICE POLYGONS
   * if 2 polygons (p0,p1) are adjacent, that is to say they share edge, then we may 
   *   have a point on that shared edge that is explicitly a member of p0 but not of p1 
   *   but implicitly, geometrically, the point is in both polygons
   *   so we make that implicit membership explicit
   * 
   *   get all points in all polygons : allpolygonpoints
   *   for each polygon : polygon
   *     get polygon points : oldpolygonpoints
   *     get allpolygonpoints with oldpolygonpoints excluded : otherpoints
   *     strip role info referring to polygon from all points in polygonpoints
   *     starting at polygon.key, trace through all points involved in polygon, using old polygon points as guide
   *       consider polygon points : p0,p1,p2...
   *       for each adjacent pair of points : a,b
   *         get all the points in otherpoints between a and b : t
   *         sort betweenpoints by closeness to a
   *         link up points : a, t0, t1.. tn, b   
   *         
   * ################################
   */
  
  private void spliceZPolygons(){
    Set<ZPoint> 
      allpolygonpoints=getZPoints(),
      otherpoints=new HashSet<ZPoint>();
    List<ZPoint> oldpolygonpoints;
    for(ZPolygon polygon:zpolygonbydpolygon.values()){
      //present points of the polygon, ordered
      oldpolygonpoints=polygon.getPoints();
      //all the other points
      otherpoints.clear();
      otherpoints.addAll(allpolygonpoints);
      otherpoints.removeAll(oldpolygonpoints);
      //
      stripRolesAndClearGeometryCache(polygon,oldpolygonpoints);
      rebuildPolygon(polygon,oldpolygonpoints,otherpoints);}}
      
  private void stripRolesAndClearGeometryCache(ZPolygon polygon,List<ZPoint> oldpolygonpoints){
    polygon.clearPrimitiveGeometryCache();
    for(ZPoint point:oldpolygonpoints)
      point.removeRole(polygon);}
  
  private void rebuildPolygon(ZPolygon polygon,List<ZPoint> oldpolygonpoints,Set<ZPoint> otherpoints){
    int s=oldpolygonpoints.size(),i1;
    ZPoint p0,p1;
    List<ZPoint> 
      newpoints=new ArrayList<ZPoint>(),
      sidepoints;
    for(int i0=0;i0<s;i0++){
      i1=i0+1;
      if(i1==s)i1=0;
      p0=oldpolygonpoints.get(i0);
      p1=oldpolygonpoints.get(i1);
      sidepoints=getSidePoints(p0,p1,otherpoints);
      newpoints.addAll(sidepoints);}
    polygon.initPointRoles(newpoints);}
  
  /*
   * returns all the points from p0 (inclusively) to p1 (exclusively) and all the points that are inbetween p0 and p1 (in order)
   */
  private List<ZPoint> getSidePoints(ZPoint p0,ZPoint p1,Set<ZPoint> otherpoints){
    //get inbetween points ordered by distance from p0, near to far
    List<ZPoint> points=new ArrayList<ZPoint>();
    for(ZPoint t:otherpoints)
      if(GD.isBetween(t.x,t.y,p0.x,p0.y,p1.x,p1.y))
        points.add(t);
    otherpoints.removeAll(points);
    Map<ZPoint,Double> distances=getP0Distances(p0,points);
    Collections.sort(points,new InbetweenComparator(distances));
    //stick p0 on the front of the list
    points.add(0,p0);
    //
    return points;}
  
  private Map<ZPoint,Double> getP0Distances(ZPoint p0,List<ZPoint> inbetween){
    Map<ZPoint,Double> distances=new Hashtable<ZPoint,Double>();
    double d;
    for(ZPoint p:inbetween){
      d=p0.getDistance(p);
      distances.put(p,d);}
    return distances;}
  
  private class InbetweenComparator implements Comparator<ZPoint>{

    Map<ZPoint,Double> distances;
    
    InbetweenComparator(Map<ZPoint,Double> distances){
      this.distances=distances;}
    
    public int compare(ZPoint p0,ZPoint p1){
      double 
        d0=distances.get(p0),
        d1=distances.get(p1);
      if(d0==d1){
        throw new IllegalArgumentException("BAD DISTANCE");
      }else if(d0>d1){
        return 1;
      }else{
        return -1;}}}
  
  /*
   * ################################
   * CREATE PROTOLATTICES
   * 
   * get all ZPoints
   * for each ZPoint : point
   *   create a protolattice (a set of ZPolygons) : proto
   *     proto will contain every zpolygon for which point has a role.
   * now we have our raw protolattices
   * 
   * merge raw protolattices
   * Merge overlapping sets. If 2 protolattices share 1 or more ZPolygons then merge them
   * this also merges duplicated protolattices (same contents)
   * 
   * for each protolattice : p0
   *   test each protolattice in protolattices against p0 for shared contents
   *     if they have shared contents then merge them
   * keep doing that until no merges are found, or something 
   * 
   * ################################
   */
  
  private List<Set<ZPolygon>> createProtoLattices(){
    List<Set<ZPolygon>> rawprotolattices=createRawProtoLattice();
    List<Set<ZPolygon>> mergedprotolattices=new ArrayList<Set<ZPolygon>>();
    Set<ZPolygon> protolattice;
    while(!rawprotolattices.isEmpty()){
      protolattice=getMergedProtoLattice(rawprotolattices);
      mergedprotolattices.add(protolattice);}
    return mergedprotolattices;}
  
  private List<Set<ZPolygon>> createRawProtoLattice(){
    List<Set<ZPolygon>> protolattices=new ArrayList<Set<ZPolygon>>();
    //create raw protolattices
    Set<ZPolygon> protolattice;
    for(ZPoint point:getZPoints()){
      protolattice=new HashSet<ZPolygon>();
      protolattices.add(protolattice);
      for(ZPointRole r:point.roles)
        protolattice.add(r.polygon);}
    return protolattices;}
  
  /*
   * given the set of rawprotolattices
   * while rawprotolattices is not empty
   *   remove a protolattice set from rawprotolattices : p0
   *   while merge occured
   *     search rawprotolattices for a protolattice set that overlaps p0
   *       if such is discovered then remove it from rawprotolattices ad add it to p0
   *       merge occurred 
   */
  private Set<ZPolygon> getMergedProtoLattice(List<Set<ZPolygon>> rawprotolattices){
    //init our new merged set
    Set<ZPolygon> merged=rawprotolattices.iterator().next();
    rawprotolattices.remove(merged);
    //
    Set<ZPolygon> test;
    Iterator<Set<ZPolygon>> i;
    boolean mergehappened=true;
    while(mergehappened&&!rawprotolattices.isEmpty()){
      mergehappened=false;
      i=rawprotolattices.iterator();
      SEEK:while(i.hasNext()){
        test=i.next();
        if(overlap(merged,test)){
          i.remove();
          merged.addAll(test);
          mergehappened=true;
          break SEEK;}}}
    return merged;}
  
  /*
   * return true if s0 and s1 share any contents
   */
  private boolean overlap(Set<ZPolygon> s0,Set<ZPolygon> s1){
    Set<ZPolygon> test=new HashSet<ZPolygon>(s0);
    test.retainAll(s1);
    boolean overlap=!test.isEmpty();
    return overlap;}
  
  /*
   * ################################
   * CREATE ZLATTICES
   * 
   * We have a bunch of protolattices. Each is a set of zpolygons.
   * for each protolattice
   *   if the protolattice is composed of just 1 zpolygon then we just use that as the outer polygon
   *     and make our lattice from it.
   *   ...otherwise 
   *   get the outer polygon
   *     It encloses the protolattice like a skin
   *   get the inner polygons
   *     all polygons in the protolattice that are undivided
   *       by undivided we mean that they do not enclose any other polygon in the lattice
   *   discard all polygons that are neither outer nor inner
   *     that means remove references to them from the lattice 
   *     it also means strip references to them from their involved zpoints 
   *     
   * ################################
   */
  
  List<ZLattice> zlattices=new ArrayList<ZLattice>();
  
  public List<ZPolygon> getLatticePolygons(){
    List<ZPolygon> latticepolygons=new ArrayList<ZPolygon>();
    for(ZLattice lattice:zlattices){
      latticepolygons.add(lattice.getOuterPolygon());
      latticepolygons.addAll(lattice.getInnerPolygons());}
    return latticepolygons;}
  
  private void createZLattices(List<Set<ZPolygon>> protolattices){
    ZLattice lattice;
    ZPolygon outerpolygon;
    List<ZPolygon> innerpolygons;
    Map<ZPolygon,List<SegInfo>> seginfo;
    for(Set<ZPolygon> protolattice:protolattices){
      if(protolattice.size()==1){
        outerpolygon=protolattice.iterator().next();
        lattice=new ZLattice(outerpolygon);
      }else{
        seginfo=getSegInfo(protolattice);
        outerpolygon=getOuterPolygon(protolattice,seginfo);
        protolattice.remove(outerpolygon);//because if it's outer then it isn't inner
        seginfo.remove(outerpolygon);
        innerpolygons=getInnerPolygons(protolattice,seginfo);
        lattice=new ZLattice(outerpolygon,innerpolygons);}
      zlattices.add(lattice);}}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * SEG INFO
   * we use this stuff for gleaning our lattice outer and inner polygons
   * ++++++++++++++++++++++++++++++++
   */
  
  private Map<ZPolygon,List<SegInfo>> getSegInfo(Set<ZPolygon> protolattice){
    Map<ZPolygon,List<SegInfo>> seginfo=new Hashtable<ZPolygon,List<SegInfo>>();
    List<SegInfo> polygonseginfo;
    for(ZPolygon polygon:protolattice){
      polygonseginfo=getPolygonSegInfo(polygon);
      seginfo.put(polygon,polygonseginfo);}
    return seginfo;}
  
  private List<SegInfo> getPolygonSegInfo(ZPolygon polygon){
    List<SegInfo> seginfos=new ArrayList<SegInfo>();
    SegInfo seginfo;
    for(ZSeg seg:polygon.getSegs()){
      seginfo=new SegInfo(seg);
      seginfos.add(seginfo);}
    return seginfos;}
  
  private class SegInfo{
    
    double[] pcenter;
    ZSeg seg;
    
    SegInfo(ZSeg seg){
      this.seg=seg;
      initCenterPoint();}
    
    private void initCenterPoint(){
      double f=seg.getForeward();
      double i=seg.getLength()/2;
      pcenter=GD.getPoint_PointDirectionInterval(seg.p0.x,seg.p0.y,f,i);}
    
    public int hashCode(){
      return seg.hashCode();}
    
    public boolean equals(Object a){
      SegInfo b=(SegInfo)a;
      return seg.equals(b.seg);}}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * GET OUTER POLYGON
   * ++++++++++++++++++++++++++++++++
   */
  
  /*
   * for each polygon in the lattice
   *   get other (not associated with the polygon) seginfos
   *     test polygon to see if it contains all otherseginfos'center points
   *       if it does then we have a winner
   */
  
  private ZPolygon getOuterPolygon(Set<ZPolygon> protolattice,Map<ZPolygon,List<SegInfo>> latticeseginfos){
    //get all seginfo
    Set<SegInfo> 
      allseginfo=new HashSet<SegInfo>(),
      otherseginfo=new HashSet<SegInfo>();
    for(List<SegInfo> seginfo:latticeseginfos.values())
      allseginfo.addAll(seginfo);
    //test each polygon in the lattice
    for(ZPolygon polygon:protolattice){
      otherseginfo.clear();
      otherseginfo.addAll(allseginfo);
      otherseginfo.removeAll(latticeseginfos.get(polygon));
      if(containsAllSegCenters(polygon,otherseginfo))
        return polygon;}
    throw new IllegalArgumentException("OUTER POLYGON NOT GOTTEN");}

  private boolean containsAllSegCenters(ZPolygon polygon,Set<SegInfo> otherseginfo){
    for(SegInfo seginfo:otherseginfo)
      if(!polygon.contains(seginfo.pcenter))
        return false;
    return true;}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * GET INNER POLYGONS
   * ++++++++++++++++++++++++++++++++
   */
  
  /*
   * an inner polygon is undivided
   * that is to say, it does not contain any segs
   * 
   * get all the seg infos in a single set : allseginfos
   * 
   * for each polygon in protolattice : polygon
   *   get the set of all seginfos except for those associated with polygon : otherseginfos
   *   test the center point of all of those seginfo.segs against polygon for containment
   *   if any test true then polygon is not undivided and is therefor not inner
   *   otherwise it is   
   *   
   */
  private List<ZPolygon> getInnerPolygons(Set<ZPolygon> protolattice,Map<ZPolygon,List<SegInfo>> latticeseginfos){
    List<ZPolygon> innerpolygons=new ArrayList<ZPolygon>();
    //get all seginfo
    Set<SegInfo> 
      allseginfo=new HashSet<SegInfo>(),
      otherseginfo=new HashSet<SegInfo>();
    for(List<SegInfo> seginfo:latticeseginfos.values())
      allseginfo.addAll(seginfo);
    //test each polygon in the lattice
    for(ZPolygon polygon:protolattice){
      otherseginfo.clear();
      otherseginfo.addAll(allseginfo);
      otherseginfo.removeAll(latticeseginfos.get(polygon));
      if(isUndivided(polygon,otherseginfo))
        innerpolygons.add(polygon);}
    return innerpolygons;}
  
  private boolean isUndivided(ZPolygon polygon,Set<SegInfo> otherseginfo){
    for(SegInfo seginfo:otherseginfo)
      if(polygon.contains(seginfo.pcenter))
        return false;
    return true;}
  
  /*
   * ################################
   * STRIP POINT ROLES FOR UNUSED ZPOLYGONS
   * 
   * When we build the lattices we use only lattice outer edge polygons and undivided polygons
   * this leaves a few
   * strip references to those unused polygons
   * 
   * get all polygons
   * get all polygons used in lattices
   * get set of all polygons not used in lattices : unused 
   * get all points
   * for each point
   *   remove roles associated with all polygons in unused
   *   
   * ################################
   */
  
  private void stripPointRolesForUnusedZPolygons(){
    //get unused polygons
    Set<ZPolygon> unused=new HashSet<ZPolygon>(zpolygonbydpolygon.values());
    unused.removeAll(getLatticePolygons());
    //get points
    Set<ZPoint> points=getZPoints();
    //strip roles
    for(ZPoint point:points)
      for(ZPolygon polygon:unused)
        point.removeRole(polygon);}
  
  /*
   * ################################
   * LATTICE TREE
   * 
   * get relationship info for all lattices
   *   for each pair of lattices X and Y
   *     either X contains Y, Y contains X, or neither
   *       in cases of containment we'll also hold onto the particular 
   *       inner polygon that's doing the containing
   *       
   * From that relationship info we get the tree
   *   the lattices for which containedlatticecount==0 are the leaves
   *   given a lattice : X
   *     given all of the lattices that contain X : containers
   *       the member of container that does not contain any other lattice in containers is the parent of X
   *     if containers is empty then X is the root
   *        
   * ################################
   */
  
  public ZLattice rootlattice;
  /*
   * we also hold on to the enclosing polygon for each lattice. 
   *   It's one of the inner polygons (or the outer polygon, if the lattice has just an outer polygon)
   *   of the lattice's parent lattice
   * we use them when we create the yards
   * a yard's outer polygon is a lattice's enclosing polygon
   *   and if 2 lattices have the same enclosing polygon then they share a yard
   * a yard'd inner polygons are its children's outer polygons 
   */
  public Map<ZLattice,ZPolygon> enclosingpolygonbylattice=new Hashtable<ZLattice,ZPolygon>();
  
  /*
   * build the lattice tree without the yards
   */
  private void buildYardlessLatticeTree(){
    Map<ZLattice,List<ContainerInfo>> allcontainers=getAllContainers();
    createZLatticeParentChildReferences(allcontainers);}
  
  private void createZLatticeParentChildReferences(Map<ZLattice,List<ContainerInfo>> allcontainers){
    ContainerInfo parent;
    for(ZLattice lattice:zlattices){
      parent=getParent(lattice,allcontainers);
      if(parent==null){
        rootlattice=lattice;
      }else{
        lattice.setParent(parent.lattice);
        parent.lattice.addChild(lattice);
        //hold on to the parental polygon that's 
        //enclosing this lattice for use in the yards 
        enclosingpolygonbylattice.put(lattice,parent.polygon);}}}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * GET PARENT
   * 
   * A subject's parent lattice is the subject's immediate container
   *   that is to say, it does not contain any of the subject's other containers
   *   
   * Test all of the subject's containers
   * if one of those containers does not contain any of the other containers then that's the subject's parent
   * if none such exists then subject is root
   *  
   * ++++++++++++++++++++++++++++++++
   */
  
  /*
   * get parent for the subject
   */
  private ContainerInfo getParent(ZLattice subject,Map<ZLattice,List<ContainerInfo>> allcontainers){
    //get all of the containers for the subject
    List<ContainerInfo> subjectcontainers=allcontainers.get(subject);
    //if the subject has no containers then the subject is root
    if(subjectcontainers.isEmpty())return null;
    //test the subject's containers
    //find the one that contains none of the other containers in the list
    //that's the subject's immediate container. The parent.
    for(ContainerInfo container:subjectcontainers)
      if(isImmediateContainer(container.lattice,subjectcontainers,allcontainers))
        return container;
    throw new IllegalArgumentException("COULDN'T FIND PARENT");}
  
  /*
   * for each container of the subject
   * get its container info list
   *   if that list contains the prospective parent then the prospective parent is not the parent 
   */
  private boolean isImmediateContainer(ZLattice prospectivecontainer,List<ContainerInfo> othercontainers,Map<ZLattice,List<ContainerInfo>> allcontainers){
    for(ContainerInfo othercontainer:othercontainers)
      if(contains(prospectivecontainer,othercontainer.lattice,allcontainers))
          return false;
     return true;}
  
  /*
   * return true if the l0 contains l1
   * get the list of containers for l1
   * if l0 is in that list then yes, l0 contains l1
   */
  private boolean contains(ZLattice l0,ZLattice l1,Map<ZLattice,List<ContainerInfo>> allcontainers){
    if(l0==l1)
      return false;
    List<ContainerInfo> containers=allcontainers.get(l1);
    for(ContainerInfo i:containers)
      if(i.lattice==l0)
        return true;
    return false;}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * CONTAINER
   * 
   * a lattice is contained by other lattices
   * the containing lattice contains the contained lattice within one of it's polygons
   * we get the containing lattice and the specific containing polygon
   *   we get all of them, for each lattice
   * 
   * for each lattice : prospectivecontained 
   *   test it against every other lattice : prospectivecontainer
   *   if prospectivecontainer contains (geometrically encloses) prospectivecontained
   *     create a containerinfo object (referring to prospectivecontainer and the specific polygon in prospectivecontainer that 
   *     is involved in enclosing the contained)
   *     add that container to prospectivecontained's containers list
   *     
   * ++++++++++++++++++++++++++++++++
   */

  private Map<ZLattice,List<ContainerInfo>> getAllContainers(){
    Map<ZLattice,List<ContainerInfo>> allcontainers=new Hashtable<ZLattice,List<ContainerInfo>>();
    List<ContainerInfo> containers;
    for(ZLattice lattice:zlattices){
      containers=getContainers(lattice);
      allcontainers.put(lattice,containers);}
    return allcontainers;}
  
  
  private List<ContainerInfo> getContainers(ZLattice contained){
    ZPoint sample=contained.getSamplePoint();
    List<ContainerInfo> containers=new ArrayList<ContainerInfo>();
    ContainerInfo i;
    ZPolygon encloser;
    for(ZLattice prospectivecontainer:zlattices)
      if(prospectivecontainer!=contained){
        encloser=prospectivecontainer.getEnclosingPolygon(sample);
        if(encloser!=null){
          i=new ContainerInfo(prospectivecontainer,encloser);
          containers.add(i);}}
    return containers;}
  
  private class ContainerInfo{
    
    ZLattice lattice;
    ZPolygon polygon;
    
    ContainerInfo(ZLattice lattice,ZPolygon polygon){
      this.lattice=lattice;
      this.polygon=polygon;}}
  
  /*
   * ################################
   * YARDS
   * Create yards.
   * Insert them between lattice parent and children
   * ################################
   */
  
  List<ZYard> yards=new ArrayList<ZYard>();
  
  private void createYards(){
    for(ZLattice lattice:zlattices)
      if(!lattice.isLeaf())
        createAndInsertYards(lattice);}
  
  /*
   * get the parent's children
   * group the children by enclosing polygon
   * for each enclosing polygon create a yard
   *   the yard's parent is the specified parent
   *   the yard's children are the children associated with that enclosing polygon
   *   the yard's outer polygon is the enclosing polygon
   *   the yard's inner polygons are its children's outer polygons 
   */
  private void createAndInsertYards(ZLattice parent){
    //get children, cast properly
    List<ZLattice> children=new ArrayList<ZLattice>();
    for(TreeNode n:parent.getChildren())
      children.add((ZLattice)n);
    //group by enclosing polygon
    Map<ZPolygon,List<ZLattice>> childrengroupedbyenclosingpolygon=getChildrenGroupedByEnclosingPolygon(children);
    //create and insert yards
    List<ZLattice> enclosedchildren;
    ZYard yard;
    for(ZPolygon enclosing:childrengroupedbyenclosingpolygon.keySet()){
      enclosedchildren=childrengroupedbyenclosingpolygon.get(enclosing);
      yard=new ZYard(parent,children,enclosing);
      yards.add(yard);
      parent.removeChildren(enclosedchildren);
      parent.addChild(yard);
      yard.setParent(parent);
      yard.setChildren(enclosedchildren);
      for(ZLattice c:enclosedchildren)
        c.setParent(yard);}}
  
  private Map<ZPolygon,List<ZLattice>> getChildrenGroupedByEnclosingPolygon(List<ZLattice> children){
    Map<ZPolygon,List<ZLattice>> groups=new Hashtable<ZPolygon,List<ZLattice>>();
    List<ZLattice> group;
    ZPolygon enclosing;
    for(ZLattice lattice:children){
      enclosing=enclosingpolygonbylattice.get(lattice);
      group=groups.get(enclosing);
      if(group==null){
        group=new ArrayList<ZLattice>();
        groups.put(enclosing,group);}
      group.add(lattice);}
    return groups;}
  
}
