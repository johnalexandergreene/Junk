package org.fleen.junk.azalea;

import java.util.ArrayList;

import org.fleen.util.tree.TreeNode;

/*
 * A lattice group is 1..n lattices contained within a single polygon
 * That containment is direct. There is no nesting of polygons. The lattices are contained within the polygon.
 *   The lattices are not contained within a polygon contained within the polygon, or any of that. 
 * That polygon is a component of the parent lattice of all lattices in the group (or null, in the case of the root lattice and group)
 * 
 */
@SuppressWarnings("serial")
public class ZLatticeGroup extends ArrayList<ZLattice>{
  
  //the polygon that encompasses the group of lattices 
  ZPolygon polygon=null;
  
  public ZLatticeGroup(ZYard yard){
    polygon=yard.outerpolygon;
    ZLattice lattice;
    for(TreeNode n:yard.getChildren()){
      lattice=(ZLattice)n;
      add(lattice);}}
  
  public ZLatticeGroup(ZLattice root){
    add(root);}

}
