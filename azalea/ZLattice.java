package org.fleen.junk.azalea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fleen.util.tree.TreeNode;
import org.fleen.util.tree.TreeNodeServices;

/*
 * a contiguous mass of zpolygons in the azalea tree
 * All zpolygons in the mass share edge either directly or indirectly. Like a mass of bubbles.
 * (except with one outer polygon that wraps the mass, like a skin, the outer polygon)
 * A zlattice explicitly defines one outer zpolygon and 0..n inner zpolygons
 * the inner polygons are unordered.
 * The zpolygons are defined in terms of relationships between zpoints
 * Each zpolygon holds a reference to the fpolygon from which it is derived
 * 
 * zlattices are partitioned, in the tree, by zyards
 * our azalea tree is composed of zlattices and zyards
 */
public class ZLattice implements TreeNode{
  
  /*
   * ################################
   * CONSTRUCTOR
   * ################################
   */
  
  public ZLattice(ZPolygon outerpolygon,Collection<ZPolygon> innerpolygons){
    this.outerpolygon=outerpolygon;
    if(!innerpolygons.isEmpty())
      this.innerpolygons=new ArrayList<ZPolygon>(innerpolygons);}
  
  /*
   * a lattice with just an outer edge
   */
  public ZLattice(ZPolygon outerpolygon){
    this.outerpolygon=outerpolygon;}
  
  /*
   * polygon 0 is the outer polygon, the rest are inner
   */
  public ZLattice(ZPolygon... polygons){
    this.outerpolygon=polygons[0];
    if(polygons.length>1){
      innerpolygons=new ArrayList<ZPolygon>(polygons.length-1);
      for(int i=1;i<polygons.length;i++)
        innerpolygons.add(polygons[i]);}}
  
  /*
   * ################################
   * POLYGONS
   * ################################
   */
  
  private ZPolygon outerpolygon;
  private List<ZPolygon> innerpolygons=null;
  
  public ZPolygon getOuterPolygon(){
    return outerpolygon;}
  
  /*
   * return list of inner polygons
   * return empty list if there aren't any
   */
  public List<ZPolygon> getInnerPolygons(){
    if(innerpolygons==null)return new ArrayList<ZPolygon>(0);
    return innerpolygons;}
  
  public boolean hasInnerPolygons(){
    return innerpolygons!=null;}
  
  /*
   * outer and 0..n inner, in that order
   */
  public List<ZPolygon> getPolygons(){
    List<ZPolygon> inner=getInnerPolygons();
    List<ZPolygon> all=new ArrayList<ZPolygon>(inner.size()+1);
    all.add(outerpolygon);
    all.addAll(inner);
    return all;}
  
  /*
   * return the polygon of this lattice that contains the specified point
   * if we just have an outer polygon then it will be the outerpolygon
   * in we have inner polygons then it will be one of those
   */
  public ZPolygon getEnclosingPolygon(ZPoint p){
    if(!outerpolygon.contains(p))return null;
    if(innerpolygons==null)return outerpolygon;
    for(ZPolygon polygon:innerpolygons)
      if(polygon.contains(p))
        return polygon;
    throw new IllegalArgumentException("outer polygon contains the point but none of the inner polygons contain it.");}
  
  /*
   * Heads
   * A head is a polygon that has no associated child-yard/s
   * It is an exposed face, a graphical area. One of our two graphical areas, 
   * the other being the Yard
   */
  public List<ZPolygon> getHeads(){
    List<ZPolygon> heads=getPolygons();
    if(!hasChildren())return heads;
    ZYard yard;
    for(TreeNode n:getChildren()){
      yard=(ZYard)n;
      heads.remove(yard.getOuterPolygon());}
    return heads;}
  
  /*
   * Undivided polygons
   * If the lattice has no inner polygons then this is the outer polygon
   * Otherwise this is the inner polygons
   */
  public List<ZPolygon> getUndividedPolygons(){
    List<ZPolygon> up=new ArrayList<ZPolygon>();
    if(innerpolygons==null)
      up.add(outerpolygon);
    else
      up.addAll(innerpolygons);
    return up;}
  
  /*
   * given a polygon of this lattice
   * get the yard enclosed by that polygon
   * if there is no such yard then return null   
   */
  public ZYard getYardEnclosedByPolygon(ZPolygon polygon){
    ZYard yard;
    for(TreeNode n:getChildren()){
      yard=(ZYard)n;
      if(yard.getOuterPolygon()==polygon)
        return yard;}
    return null;}
  
  /*
   * ################################
   * INNER STRUCTURE
   * ################################
   */
  
//  public ZStrand getInnerStructureStrands(){
//    
//  }
    
  /*
   * ################################
   * PRIMITIVE GEOMETRY
   * points and segs
   * ################################
   */
  
  /*
   * clear low-level geometry object cache, probably because we're gonna change them
   * polygons : segs and points
   */
  public void clearPrimitiveGeometryCache(){
    for(ZPolygon p:getPolygons())
      p.clearPrimitiveGeometryCache();}

  /*
   * ++++++++++++++++++++++++++++++++
   * POINTS
   * ++++++++++++++++++++++++++++++++
   */
  
  public Set<ZPoint> getPoints(){
    Set<ZPoint> points=new HashSet<ZPoint>();
    points.addAll(outerpolygon.getPoints());
    if(innerpolygons!=null)
      for(ZPolygon g:innerpolygons)
        points.addAll(g.getPoints());
    return points;}
  
  public ZPoint getSamplePoint(){
    return outerpolygon.getPoints().get(0);}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * SEGS
   * ++++++++++++++++++++++++++++++++
   */
  
  public Set<ZSeg> getSegs(){
    Set<ZSeg> segs=new HashSet<ZSeg>();
    segs.addAll(outerpolygon.getSegs());
    if(innerpolygons!=null)
      for(ZPolygon g:innerpolygons)
        segs.addAll(g.getSegs());
    return segs;}
  
  /*
   * ################################
   * TREE
   * ################################
   */
  
  private TreeNodeServices treenodeservices=new TreeNodeServices();
  
  public TreeNode getParent(){
    return treenodeservices.getParent();}
  
  public void setParent(TreeNode node){
    treenodeservices.setParent(node);}
  
  public List<? extends TreeNode> getChildren(){
    return treenodeservices.getChildren();}
  
  public TreeNode getChild(){
    return treenodeservices.getChild();}
  
  public void setChildren(List<? extends TreeNode> nodes){
    treenodeservices.setChildren(nodes);}
  
  public void setChild(TreeNode node){
    treenodeservices.setChild(node);}
  
  public void addChild(TreeNode node){
    treenodeservices.addChild(node);}
  
  public int getChildCount(){
    return treenodeservices.getChildCount();}
  
  public boolean hasChildren(){
    return treenodeservices.hasChildren();}
  
  public void removeChildren(Collection<? extends TreeNode> children){
    treenodeservices.removeChildren(children);}
  
  public void clearChildren(){
    treenodeservices.clearChildren();}
  
  public boolean isRoot(){
    return treenodeservices.isRoot();}
  
  public boolean isLeaf(){
    return treenodeservices.isLeaf();}
  
  public int getDepth(){
    return treenodeservices.getDepth(this);}
  
  public TreeNode getRoot(){
    return treenodeservices.getRoot(this);}
  
  public TreeNode getAncestor(int levels){
    return treenodeservices.getAncestor(this,levels);}
  
  public List<TreeNode> getSiblings(){
    return treenodeservices.getSiblings(this);}
  
  /*
   * ################################
   * SOUL
   * ################################
   */
  
  ZLatticeSoul soul=null;
  
  public ZLatticeSoul getSoul(){
    if(soul==null)
      soul=new ZLatticeSoul(this);
    return soul;}
  
}
