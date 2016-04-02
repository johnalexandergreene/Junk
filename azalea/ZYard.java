package org.fleen.junk.azalea;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.fleen.geom_2D.DPoint;
import org.fleen.util.tree.TreeNode;
import org.fleen.util.tree.TreeNodeServices;

public class ZYard implements TreeNode,ZElement{
  
  /*
   * ################################
   * CONSTRUCTOR
   * ################################
   */
  
  public ZYard(ZLattice parent,List<ZLattice> children,ZPolygon outerpolygon){
    this.outerpolygon=outerpolygon;
    setParent(parent);
    setChildren(children);}
  
  //for debug
  public ZYard(){}
  
  /*
   * ################################
   * GEOMETRY
   * 
   * The outer polygon is an inner polygon of the parent lattice
   *   or the outer polygon of the parent lattice if that lattice has just one polygon
   * We specify that in params
   *  
   * The inner polygons are the child outer polygons
   * ################################
   */
  
  ZPolygon outerpolygon;
  
  public ZPolygon getOuterPolygon(){
    return outerpolygon;}
  
  public List<ZPolygon> getInnerPolygons(){
    List<TreeNode> c=treenodeservices.getChildren();
    List<ZPolygon> i=new ArrayList<ZPolygon>(c.size());
    ZLattice lattice;
    for(TreeNode n:c){
      lattice=(ZLattice)n;
      i.add(lattice.getOuterPolygon());}
    return i;}
  
  public List<ZPolygon> getPolygons(){
    List<ZPolygon> i=getInnerPolygons();
    List<ZPolygon> e=new ArrayList<ZPolygon>(i.size()+1);
    e.add(getOuterPolygon());
    e.addAll(i);
    return e;}
  
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
    for(ZPolygon polygon:getPolygons())
      path.append(polygon.getPath2D(),false);
    return path;}
  
  /*
   * ################################
   * TREENODE
   * ################################
   */
  
  public TreeNodeServices treenodeservices=new TreeNodeServices();
  
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
   * OBJECT
   * ################################
   */
  
  public String toString(){
    return this.getClass().getSimpleName()+":"+hashCode();}
  

}
