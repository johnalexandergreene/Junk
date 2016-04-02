package org.fleen.junk.mesh;

import java.util.HashSet;
import java.util.Set;

import org.fleen.core.g2D.Point2D;

public class MPoint extends Point2D{
 
  /*
   * ################################
   * CONSTRUCTORS
   * ################################
   */
  
  public MPoint(double[] p){
    super(p);}
  
  public MPoint(double x,double y){
    super(x,y);}
  
  public MPoint(Point2D p){
    super(p);}
  
  //CLONE
  public MPoint(MPoint p){
    super(p);}
  
  //debug
  public MPoint(String name,double x,double y){
    super(x,y);
    this.name=name;}
  
  public String name;
  
  /*
   * ################################
   * ROLES
   * An MPoint plays a role in the definition of 1..n polygons
   * ################################
   */
  
  //for each polygon that this point plays a role in we have exactly one role object
  public Set<MPointRole> roles=new HashSet<MPointRole>();
  
  public void addRole(MPointRole r){
    roles.add(r);}
  
  //return the role associated with the specified polygon
  //returns null if no such role exists in this point
  public MPointRole getRole(MPolygon p){
    for(MPointRole r:roles)
      if(r.polygon==p)
        return r;
    return null;}
  
  //remove the role associated with the specified polygon
  //returns the role that got removed
  //returns null if no such role
  public MPointRole removeRole(MPolygon p){
    MPointRole r=getRole(p);
    if(r==null)return null;
    roles.remove(r);
    return r;}
  
  /*
   * ################################
   * OBJECT
   * ################################
   */
  
  
  public void printDebugString(){
    System.out.println("POINT:"+name);
    System.out.println("ROLES");
    for(MPointRole r:roles){
      System.out.println("--------");
      System.out.println("polygon:"+r.polygon.name);
      if(r.a0==null)
        System.out.println("a0:null");
      else
        System.out.println("a0:"+r.a0.name);
      if(r.a1==null)
        System.out.println("a1:null");
      else
        System.out.println("a1:"+r.a1.name);}
  }
}
