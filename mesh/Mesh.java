package org.fleen.junk.mesh;

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
import org.fleen.junk.mesh.collisionMap.CollisionMap;
import org.fleen.junk.mesh.spliceMap.SpliceMap;

/*
 * A disconnected graph
 * it describes a system of shapes
 * each shape consists of 1 outer edge and 0..n inner edges
 * all of the shapes are contained by a root shape, the root shape's immediate children contain children, and so on. Treewise.
 * Shapes share edge at parent-child borders
 * 
 * We initialize the graph with a forsythia composition.
 * NPolygons are converted to 1-polygon shapes
 * Colliding points are merged
 * Implicit point-sharing between polygons is made explicit
 * 
 * Then we apply various processes to the graph's components
 * processes like Boil, Fluff and Smooth
 */
public class Mesh{
  
  /*
   * ################################
   * CONSTRUCTOR
   * ################################
   */
  
  /*
   * merge all colliding points using collision map
   * get merged points. these are the points of our mesh.
   * create mshapes (an their associated mpolygons) from npolygons and mesh (merged) points
   * splice mpolygons together, explicitly including shared points in the respective polygons' geometry.
   * copy npolygon tags to mshapes
   * link mshapes treewise to mirror npolygon tree
   */
  public Mesh(ForsythiaComposition fc){
    MapMetrics mapmetrics=new MapMetrics(fc);
    CollisionMap cm=new CollisionMap(fc,mapmetrics);
    List<MPoint> rawpoints=initRawPoints(cm);
    Map<FPolygon,MShape> mshapebynpoly=initShapesAndPolygons(rawpoints,fc,cm,mapmetrics);
    linkParentsAndChildrenForInit(fc,mshapebynpoly);}
  
  //for test
  public Mesh(){}
  
  /*
   * ################################
   * POINTS
   * ################################
   */
  
  /*
   * by raw we mean that the points have no role data set
   * they are just points, unassociated with any polygons
   */
  private List<MPoint> initRawPoints(CollisionMap cm){
    List<MPoint> rawpoints=new ArrayList<MPoint>();
    Set<Point2D> a=cm.getCollisionGroupMergePoints();
    rawpoints=new ArrayList<MPoint>(a.size());
    for(Point2D p:a)
      rawpoints.add(new MPoint(p));
    return rawpoints;}
  
  /*
   * returns the mpoint in this mesh with the specified coordinates
   * throws exception if there is no such point in this mesh
   */
  public MPoint getPoint(double x,double y){
    Set<MPoint> points=getPoints();
    for(MPoint p:points)
      if(p.x==x&&p.y==y)return p;
    throw new IllegalArgumentException("MPoint with specified coordinates ("+x+","+y+") does not exist in this mesh");}
  
  public Set<MPoint> getPoints(){
    Set<MPoint> points=new HashSet<MPoint>();
    Set<MPolygon> polygons=getPolygons();
    for(MPolygon p:polygons)
      points.addAll(p.getPoints());
    return points;}
  
  /*
   * ################################
   * SEGS
   * ################################
   */
  
  public Set<MSeg> getSegs(){
    Set<MSeg> segs=new HashSet<MSeg>();
    List<MPoint> points;
    int s,inext;
    for(MShape shape:root.getBranchShapes())
      for(MPolygon polygon:shape){
        points=polygon.getPoints();
        s=points.size();
        for(int i=0;i<s;i++){
          inext=i+1;
          if(inext==s)inext=0;
          segs.add(new MSeg(points.get(i),points.get(inext)));}}
    return segs;}
  
  /*
   * ################################
   * POLYGONS
   * ################################
   */
  
  public Set<MPolygon> getPolygons(){
    Set<MPolygon> polygons=new HashSet<MPolygon>();
    for(MShape s:root.getBranchShapes())
      for(MPolygon p:s)
        polygons.add(p);
    return polygons;}
  
  /*
   * ################################
   * SHAPES
   * A shape has 1 outer edge polygon and
   * 0..n inner edge polygons
   * This describes an area with 0..n holes
   * ################################
   */
  
  public MShape root;
  public List<MShape> shapes;
  
  /*
   * for each npolygon we create an mshape.
   *   each mshape has a single edge, derived from that npolygon
   * returns a map with mshape keyed by npolygon
   */
  private Map<FPolygon,MShape> initShapesAndPolygons(
    List<MPoint> rawpoints,ForsythiaComposition fc,CollisionMap cm,MapMetrics mapmetrics){
    Map<FPolygon,MShape> mshapebynpoly=new Hashtable<FPolygon,MShape>();
    //for every npolygon in the forsythia composition
    //create a 1-polygon mshape with points and point-roles set
    //all polygon points are gotten from this mesh
    Iterator<ForsythiaTreeNode> i=fc.getPolygonIterator();
    shapes=new ArrayList<MShape>();
    MShape mshape;
    FPolygon npolygon;
    while(i.hasNext()){
      npolygon=(FPolygon)i.next();
      mshape=new MShape(npolygon,cm,this,rawpoints);
      mshapebynpoly.put(npolygon,mshape);
      shapes.add(mshape);}
    ((ArrayList<MShape>)shapes).trimToSize();
    //a shapes have been created and for each shape a polygon corrosponding
    //to the param npolygon
    //now we splice polygons
    //we make it so points on shared edges reflect it in their points roles  
    splicePolygonsForInit(mapmetrics,rawpoints);
    //
    return mshapebynpoly;}
  
  /*
   * ################################
   * POLYGON SPLICE FOR INIT
   * 
   * Adjacent polygons share edge
   * they also share points, 
   *   some explicitly, the points occur in both polygons
   *   others implicitly, points are on a shared edge but not actually part of both polygon definitions
   * make the implicitly shared points explicitly shared
   * ################################
   */
  
  private void splicePolygonsForInit(MapMetrics mapmetrics,List<MPoint> rawpoints){
    SpliceMap splicemap=new SpliceMap(this,mapmetrics,rawpoints);
    int s,i1;
    List<MPoint> points;
    MPoint p0,p1;
    for(MShape shape:shapes){
      for(MPolygon polygon:shape){
        points=polygon.getPoints();
        s=points.size();
        for(int i0=0;i0<s;i0++){
          i1=i0+1;
          if(i1==s)i1=0;
          p0=points.get(i0);
          p1=points.get(i1);
          splice(polygon,p0,p1,splicemap);}}}}
  
  private void splice(MPolygon polygon,MPoint p0,MPoint p1,SpliceMap splicemap){
    //get inbetween points
    List<MPoint> inbetweenpoints=splicemap.getInbetweenPoints(p0,p1,polygon);
    //if there aren't any then we're done
    if(inbetweenpoints.isEmpty())return;
    //exclude polygon points, just in case
    //add pointrole to all inbetween point for the specified polygon
    for(MPoint p:inbetweenpoints)
      p.addRole(new MPointRole(polygon));
    //connect all roles in the sequence
    int s=inbetweenpoints.size(),iprior,inext;
    MPoint pprior,p,pnext;
    for(int i=0;i<s;i++){
      iprior=i-1;
      inext=i+1;
      p=inbetweenpoints.get(i);
      if(iprior!=-1){
        pprior=inbetweenpoints.get(iprior);
        connect(p,pprior,polygon);}
      if(inext!=s){
        pnext=inbetweenpoints.get(inext);
        connect(p,pnext,polygon);}}
    //insert the sequence between p0 and p1  
    MPoint 
      s0=inbetweenpoints.get(0),
      s1=inbetweenpoints.get(inbetweenpoints.size()-1);
    MPointRole 
      rp0=p0.getRole(polygon),
      rp1=p1.getRole(polygon),
      rs0=s0.getRole(polygon),
      rs1=s1.getRole(polygon);
    rp0.disconnect(p1);
    rp1.disconnect(p0);
    rs0.connect(p0);
    rs1.connect(p1);
    rp0.connect(s0);
    rp1.connect(s1);}
  
  private void connect(MPoint p0,MPoint p1,MPolygon polygon){
    MPointRole 
      r0=p0.getRole(polygon),
      r1=p1.getRole(polygon);
    r0.connect(p1);
    r1.connect(p0);}
  
  /*
   * ################################
   * LINK PARENTS AND CHILDREN FOR INIT
   * we're mirroring the forsythia (split) composition tree
   * set root too
   * ################################
   */
  
  private void linkParentsAndChildrenForInit(ForsythiaComposition fc,Map<FPolygon,MShape> mshapebynpoly){
    //set root
    FPolygon nr=fc.getRootPolygon();
    root=mshapebynpoly.get(nr);
    //mirror tree
    Iterator<ForsythiaTreeNode> i=fc.getPolygonIterator();
    FPolygon thispolygon,parentpolygon;
    MShape thisshape,parentshape;
    List<FPolygon> childpolygons;
    List<MShape> childshapes;
    while(i.hasNext()){
      //get a shape
      thispolygon=(FPolygon)i.next();
      thisshape=mshapebynpoly.get(thispolygon);
      //get its parent
      parentpolygon=thispolygon.getPolygonParent();
      if(parentpolygon!=null){
        parentshape=mshapebynpoly.get(parentpolygon);
        thisshape.parent=parentshape;}
      //get its children
      childpolygons=thispolygon.getPolygonChildren();
      if(childpolygons!=null){
        childshapes=getShapes(childpolygons,mshapebynpoly);
        thisshape.setChildren(childshapes);}}}
  
  private List<MShape> getShapes(List<FPolygon> polygons,Map<FPolygon,MShape> mshapebynpoly){
    List<MShape> shapes=new ArrayList<MShape>(polygons.size());
    MShape shape;
    for(ForsythiaTreeNode n:polygons){
      shape=mshapebynpoly.get((FPolygon)n);
      shapes.add(shape);}
    return shapes;}
  
  /*
   * ################################
   * TEST
   * ################################
   */
  
//  private static final KVertex 
//    V0=new KVertex(0,0,0,0),
//    V1=new KVertex(1,1,0,0),
//    V2=new KVertex(2,2,0,0),
//    V3=new KVertex(2,1,-1,5),
//    V4=new KVertex(1,0,-1,5),
//    V5=new KVertex(0,-1,-1,5); 
  
//  public static final void main(String[] a){
//    
//    ForsythiaComposition fc=new ForsythiaComposition();
//    FMetagon rootpolygonmetagon=new FMetagon(V0,V2,V3,V5);
//    fc.initTree(rootpolygonmetagon);
//    NGridTransform rg=new NGridTransform(
//      new KVertex(0,0,0,0),
//      0,
//      true,
//      1.0);
//    NPolygon rp=fc.getRootPolygon();
//    rp.setChild(rg);
//    rg.setParent(rp);
//    NPolygon 
//      cp0=new NPolygon(new FMetagon(V0,V1,V4,V5)),
//      cp1=new NPolygon(new FMetagon(V1,V2,V3,V4));
//    rg.setChildren(Arrays.asList(new NPolygon[]{cp0,cp1}));
//    cp0.parent=rg;
//    cp1.parent=rg;
//    
//    Mesh mesh=new Mesh(fc);
//    
//  }

}
