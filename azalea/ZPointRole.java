package org.fleen.junk.azalea;

/*
 * A role played by a point in the definition of a polygon
 * a point may participate in the definition of several polygons, have several roles
 */
public class ZPointRole{
  
  /*
   * ################################
   * CONSTRUCTORS
   * ################################
   */
  
  public ZPointRole(ZPolygon polygon,ZPoint a0,ZPoint a1){
    this.polygon=polygon;
    this.a0=a0;
    this.a1=a1;}
  
  public ZPointRole(ZPolygon polygon){
    this.polygon=polygon;}
  
  /*
   * ################################
   * GEOMETRY
   * ################################
   */
  
  //the polygon to which this role refers. 
  public ZPolygon polygon;
  //the point's adjacents within that polygon. order is irrelevant
  public ZPoint a0=null,a1=null;
  
  public void connect(ZPoint p){
    if(a0==null)
      a0=p;
    else
      a1=p;}
  
  public void disconnect(ZPoint p){
    if(a0==p)
      a0=null;
    else if(a1==p)
      a1=null;
    else
      throw new IllegalArgumentException("Point cannot be disconnected because it is not connected :"+p);}
  
  /**
   * returns true if this role is connected to the specified point
   * that is, if p==a0 OR p==a1
   */
  public boolean isConnected(ZPoint p){
    return a0==p||a1==p;}
  
  public boolean hasNullConnection(){
    return a0==null||a1==null;}
  
  public ZPoint getOtherAdjacent(ZPoint point){
    if(a0==point)
      return a1;
    else if(a1==point)
      return a0;
    else
      throw new IllegalArgumentException("The specified point is not referred to by this role");}
  
  /*
   * replace the old point with the new point
   * throw exception if old point is not presently referred to
   */
  public void replacePoint(ZPoint oldpoint,ZPoint newpoint){
    if(a0==oldpoint){
      a0=newpoint;
    }else if(a1==oldpoint){
      a1=newpoint;
    }else{
      throw new IllegalArgumentException("oldpoint is not referred to by this point role");}}
  
}
