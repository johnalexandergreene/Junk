package org.fleen.junk.azalea;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.fleen.forsythia.composition.FGrid;
import org.fleen.forsythia.composition.ForsythiaComposition;
import org.fleen.geom_2D.DPoint;
import org.fleen.util.tree.TreeNode;
import org.fleen.util.tree.TreeNodeIterator;

/*
 * AZALEA
 * 
 * We take a Forsythia
 * derive a graph of polygons from the forsythia's leaf polygons
 * consider everything : points, segs, adjacentness
 * hold references between zpolygons and forsythia tree elements
 * then....
 * well, we've tried some transforms
 * probably gonna do some kind of cell dance, for making nice organic images and maybe animation
 * 
 * 
 * 
 * A tree of ZLattices and ZYards
 * Access to lattice components
 * We derive its initial form from the Forsythia
 * Then we modify it with the Fluffer
 * TODO get rid of Yards, we don't do those anymore
 */
public class Azalea{
  
  /*
   * ################################
   * CONSTRUCTOR
   * ################################
   */
  public Azalea(ForsythiaComposition forsythia){
    System.out.println("===== CONSTRUCTING AZALEA =====");
    this.forsythia=forsythia;
    initMetrics();
    initElements();}
  
  /*
   * for debug
   */
  public Azalea(
    ZLattice root,double xmin,double xmax,double ymin,double ymax,
    double forsythiasmallestfish,double pointcollisiondistance){
    this.root=root;
    this.xmin=xmin;
    this.xmax=xmax;
    this.ymin=ymin;
    this.ymax=ymax;
    this.forsythiasmallestfish=forsythiasmallestfish;
    this.pointcollisiondistance=pointcollisiondistance;}
  
  /*
   * ################################
   * FORSYTHIA
   * ################################
   */
  
  public ForsythiaComposition forsythia;
  
  /*
   * ################################
   * METRICS
   * ################################
   */
  
  private void initMetrics(){
    initMinMax();
    initForsythiaSmallestFish();
    initPointCollisionDistance();}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * BOUNDS
   * The bounding rectangle of the forsythia and zgraph
   * we stay within it in our various fluffing operations too
   * ++++++++++++++++++++++++++++++++
   */
  
  private double 
    xmin=Double.MAX_VALUE,
    xmax=Double.MIN_VALUE,
    ymin=xmin,
    ymax=xmax;
  
  public double getWidth(){
    return xmax-xmin;}
  
  public double getHeight(){
    return ymax-ymin;}
  
  public double getXMin(){
    return xmin;}
  
  public double getXMax(){
    return xmax;}
  
  public double getYMin(){
    return ymin;}
  
  public double getYMax(){
    return ymax;}
  
  public double[] getCenter(){
    return new double[]{
      getXMin()+getWidth()/2,
      getYMin()+getHeight()/2};}
  
  private void initMinMax(){
    for(DPoint p:forsythia.getRootPolygon().getDPolygon()){
      if(p.x<xmin)xmin=p.x;
      if(p.x>xmax)xmax=p.x;
      if(p.y<ymin)ymin=p.y;
      if(p.y>ymax)ymax=p.y;}}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * FORSYTHIA SMALLEST FISH
   * This is the smallest interval between adjacent vertices in all polygons in the forsythia composition
   * it describes the grain-size of the geometry, more or less
   * from this we can derive intervals for testing zgraph point collision and specifying fluffer vector maginitudes.
   * We get this by checking every FTNGrid in the Forsythia grids&polygons tree and getting the smallest grid.fish
   * ++++++++++++++++++++++++++++++++
   */
  
  private double forsythiasmallestfish=Double.MAX_VALUE;
  
  public double getForsythiaSmallestFish(){
    System.out.println("%%% %%% %%% FORSYTHIA SMALLEST FISH = "+forsythiasmallestfish);
    return forsythiasmallestfish;}
  
  private void initForsythiaSmallestFish(){
    double f;
    Iterator<TreeNode> i=forsythia.getGridIterator();
    FGrid g;
    while(i.hasNext()){
      g=(FGrid)i.next();
      f=g.getLocalKGrid().getFish();
      if(f<forsythiasmallestfish)
        forsythiasmallestfish=f;}}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * POINT COLLISION DISTANCE
   * If two points are close enough then they are considered to be in collision. 
   * So we merge them or something.
   * ++++++++++++++++++++++++++++++++
   */
  
  private static final double POINTCOLLISIONDISTANCE_SMALLESTFISHFACTOR=0.01;//TODO test and optimize
  private double pointcollisiondistance;
  
  public double getPointCollisionDistance(){
    return pointcollisiondistance;}
  
  private void initPointCollisionDistance(){
    pointcollisiondistance=
      getForsythiaSmallestFish()*POINTCOLLISIONDISTANCE_SMALLESTFISHFACTOR;}
  
  /*
   * ################################
   * ELEMENTS
   * The azalea is a tree of alternating lattices and yards
   * a lattice is a polygon. Optionally sectioned.
   * a yard is a space defined by an outer polygon and 1..n inner polygons.
   * we also have points and segs
   * ################################
   */
  
  private ZLattice root;
  
  private void initElements(){
    AzaleaBuilder ab=new AzaleaBuilder(this);
    root=ab.rootlattice;}
  
  public ZLattice getRoot(){
    return root;}
  
  public TreeNodeIterator getNodeIterator(){
    return new TreeNodeIterator(root);}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * LATTICES
   * ++++++++++++++++++++++++++++++++
   */
  
  public TreeNodeIterator getLatticeIterator(){
    return new LatticeIterator(root);}
  
  public List<ZLattice> getLattices(){
    List<ZLattice> lattices=new ArrayList<ZLattice>();
    TreeNodeIterator i=getLatticeIterator();
    while(i.hasNext())
      lattices.add((ZLattice)i.next());
    return lattices;}
  
  class LatticeIterator extends TreeNodeIterator{

    public LatticeIterator(TreeNode node){
      super(node);}
    
    protected boolean skip(TreeNode n){
      return n instanceof ZYard;}}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * YARDS
   * ++++++++++++++++++++++++++++++++
   */
  
  public TreeNodeIterator getYardIterator(){
    return new YardIterator(root);}
  
  public List<ZYard> getYards(){
    List<ZYard> yards=new ArrayList<ZYard>();
    TreeNodeIterator i=getYardIterator();
    while(i.hasNext())
      yards.add((ZYard)i.next());
    return yards;}
  
  class YardIterator extends TreeNodeIterator{

    public YardIterator(TreeNode node){
      super(node);}
    
    protected boolean skip(TreeNode n){
      return n instanceof ZLattice;}}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * ALL ELEMENTS
   * ++++++++++++++++++++++++++++++++
   */
  
  public List<ZElement> getElements(){
    List<ZElement> e=new ArrayList<ZElement>();
    e.addAll(getYards());
    for(ZLattice l:getLattices())
      e.addAll(l.getPolygons());
    return e;}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * HEADS
   * 
   * The heads are the terminal polygons of the leaf lattices.
   * If the leaf lattice has inner polygons then we use those
   * if it doesn't then we use the outer polygon
   * 
   * The azalea composition has 2 top-level (?) visible areas
   *   yards and heads
   * 
   * ++++++++++++++++++++++++++++++++
   */
  
  public List<ZPolygon> getHeads(){
    List<ZPolygon> heads=new ArrayList<ZPolygon>();
    TreeNodeIterator i=getLatticeIterator();
    ZLattice lattice;
    while(i.hasNext()){
      lattice=(ZLattice)i.next();
      heads.addAll(lattice.getHeads());}
    return heads;}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * PRIMITIVE GEOMETRY
   * Segs and points
   * Points are associated with polygons and each-other via roles
   * A seg is a pair of points, associated
   * segs are id'd by points so geometrically equivalent segs are considered equal and have the same hashcode.
   * ++++++++++++++++++++++++++++++++
   */
  
  public void clearPrimitiveGeometryCache(){
    TreeNodeIterator i=getLatticeIterator();
    ZLattice lattice;
    while(i.hasNext()){
      lattice=(ZLattice)i.next();
      lattice.clearPrimitiveGeometryCache();}}
  
  public Set<ZPoint> getPoints(){
    Set<ZPoint> points=new HashSet<ZPoint>();
    TreeNodeIterator i=getLatticeIterator();
    ZLattice lattice;
    while(i.hasNext()){
      lattice=(ZLattice)i.next();
      points.addAll(lattice.getPoints());}
    return points;}
  
  public Set<ZPoint> getPrimaryPoints(){
    Set<ZPoint> points=new HashSet<ZPoint>();
    TreeNodeIterator i=getLatticeIterator();
    ZLattice lattice;
    while(i.hasNext()){
      lattice=(ZLattice)i.next();
      points.addAll(lattice.getSoul().getPrimaryPoints());}
    return points;}
  
  public Set<ZPoint> getSecondaryPoints(){
    Set<ZPoint> points=getPoints();
    points.removeAll(getPrimaryPoints());
    return points;}
  
  public Set<ZSeg> getSegs(){
    Set<ZSeg> segs=new HashSet<ZSeg>();
    TreeNodeIterator i=getLatticeIterator();
    ZLattice lattice;
    while(i.hasNext()){
      lattice=(ZLattice)i.next();
      segs.addAll(lattice.getSegs());}
    return segs;}
  
}
