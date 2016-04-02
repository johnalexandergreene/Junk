package org.fleen.junk.azalea;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fleen.geom_2D.DPoint;

public class ZPoint extends DPoint{
 
  private static final long serialVersionUID=-1858282217857807165L;
  
  /*
   * ################################
   * CONSTRUCTORS
   * ################################
   */

  public ZPoint(double[] p){
    super(p);}
  
  public ZPoint(double x,double y){
    super(x,y);}
  
  public ZPoint(DPoint p){
    super(p);}
  
  //for debug
  public ZPoint(double x,double y,String name){
    super(x,y);
    this.name=name;}
  
  /*
   * ################################
   * NAME FOR DEBUG
   * ################################
   */
  
  public String name;
  
  /*
   * ################################
   * ROLES
   * An MPoint plays a role in the definition of 1..n polygons
   * ################################
   */
  
  //for each polygon that this point plays a role in we have exactly one role object
  public Set<ZPointRole> roles=new HashSet<ZPointRole>();
  
  public void addRole(ZPointRole role){
    roles.add(role);}
  
  //return the role associated with the specified polygon
  //returns null if no such role exists in this point
  public ZPointRole getRole(ZPolygon polygon){
    for(ZPointRole r:roles)
      if(r.polygon==polygon)
        return r;
    return null;}
  
  //remove the role associated with the specified polygon
  //returns the role that got removed
  //returns null if no such role
  public ZPointRole removeRole(ZPolygon polygon){
    ZPointRole r=getRole(polygon);
    if(r==null)return null;
    roles.remove(r);
    return r;}
  
  /*
   * each role refers to a polygon
   * collect those polygons
   */
  public List<ZPolygon> getRolePolygons(){
    List<ZPolygon> polygons=new ArrayList<ZPolygon>();
    for(ZPointRole role:roles)
      polygons.add(role.polygon);
    return polygons;}
  
  /*
   * return all roles that refer to the specified point
   * if none refer to the specified point then we return an empty list
   */
//  public List<ZPointRole> getRoles(ZPoint p){
//    List<ZPointRole> a=new ArrayList<ZPointRole>();
//    for(ZPointRole b:roles)
//      if(b.isConnected(p))
//        a.add(b);
//    return a;}
//  
//  public void disconnect(ZPoint p){
//    for(ZPointRole r:getRoles(p))
//      r.disconnect(p);}
  
  /*
   * ################################
   * OBJECT
   * ################################
   */
  
  public String toString(){
    String s="["+x+","+y+"]";
    return s;}
  
}
