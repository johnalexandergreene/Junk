package org.fleen.junk.mesh;

/*
 * A role played by a point in the definition of a polygon
 * a point may participate in the definition of several polygons, have several roles
 */
public class MPointRole{
  
  /*
   * ################################
   * CONSTRUCTORS
   * ################################
   */
  
  public MPointRole(MPolygon polygon,MPoint a0,MPoint a1){
    this.polygon=polygon;
    this.a0=a0;
    this.a1=a1;}
  
  public MPointRole(MPolygon polygon){
    this.polygon=polygon;}
  
  /*
   * ################################
   * GEOMETRY
   * ################################
   */
  
  //the polygon to which this role refers. 
  public MPolygon polygon;
  //the point's adjacents within that polygon. order is irrelevant
  public MPoint a0=null,a1=null;
  
  public void connect(MPoint p){
    if(a0==null)
      a0=p;
    else
      a1=p;}
  
  public void disconnect(MPoint p){
    if(a0==p)
      a0=null;
    else if(a1==p)
      a1=null;
    else
      throw new IllegalArgumentException("Point is not connected. Point:"+p);}
  
  public boolean hasNullConnection(){
    return a0==null||a1==null;}
  
  public MPoint getOtherAdjacent(MPoint p){
    if(a0==p)
      return a1;
    else if(a1==p)
      return a0;
    else
      throw new IllegalArgumentException("p is not in this role");}
  
}
