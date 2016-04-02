package org.fleen.junk.azalea;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fleen.geom_2D.GD;

public class ZSeg{
  
  /*
   * ################################
   * CONSTRUCTORS
   * ################################
   */
  
  public ZSeg(ZPoint p0,ZPoint p1){
    if(p0.x==p1.x&&p0.y==p1.y)
      throw new IllegalArgumentException("INVALID SEG COORS. DUPE");
    this.p0=p0;
    this.p1=p1;}
  
  public ZSeg(ZSeg s){
    this.p0=s.p0;
    this.p1=s.p1;}
  
  /*
   * ################################
   * GEOMETRY
   * ################################
   */
  
  public ZPoint p0,p1;
  
  /*
   * returns the direction from p0 to p1
   */
  public double getForeward(){
    return GD.getDirection_PointPoint(p0.x,p0.y,p1.x,p1.y);}
  
  public double getLength(){
    return GD.getDistance_PointPoint(p0.x,p0.y,p1.x,p1.y);}
  
  /*
   * return the point on this seg at offset from p0 in direction of p1
   * offset is in terms of seglength
   */
  public double[] getPointAtProportionalOffset(double offset){
    double dir=getForeward();
    double dis=offset*getLength();
    return GD.getPoint_PointDirectionInterval(p0.x,p0.y,dir,dis);}
  
  /*
   * given p, a point on this seg 
   * return the offset of p from p0 as a proportion of dis(p0,p1)
   * If the point is not on the seg then we're looking at errors. We aren't gonna test for that tho because it would take time.
   */
  public double getProportionlOffset(double[] p){
    double d=GD.getDistance_PointPoint(p0.x,p0.y,p[0],p[1]);
    double offset=d/getLength();
    return offset;}
  
  public double[] getIntersection_Ray(double rpx,double rpy,double rd){
    double[] i=GD.getIntersection_RaySeg(rpx,rpy,rd,p0.x,p0.y,p1.x,p1.y);
    return i;}
  
  /*
   * 
   * Return the polygons of which this seg is a part
   * 
   * given 2 roles, one from p0 and the other from p1
   * if the p0 role refers to p1 and the p1 role refers to p0 and they share a polygon 
   *   then that polygon is spanned by the seg
   */
  public Set<ZPolygon> getSpannedPolygons(){
    return getSpannedPolygons(p0,p1);}
  
  public static final Set<ZPolygon> getSpannedPolygons(ZPoint p0,ZPoint p1){
    Set<ZPolygon> 
      p0p=new HashSet<ZPolygon>(),
      p1p=new HashSet<ZPolygon>();
    for(ZPointRole r:p0.roles)
      if(r.isConnected(p1))
        p0p.add(r.polygon);
    for(ZPointRole r:p1.roles)
      if(r.isConnected(p0))
        p1p.add(r.polygon);
    p0p.retainAll(p1p);
    return p0p;}
  
  /*
   * ################################
   * INSERT POINTS FOR MULTIPOINT RETICULATION
   * ################################
   */
  
  /*
   * reticulate this seg on the graph
   * that is to say, insert a number of new ZPoints between zp0 and zp1
   * set point roles appropriately
   */
  public void reticulate(double interval){
    double p0p1dis=p0.getDistance(p1);
    int reticulationcount=(int)(p0p1dis/interval);
    double 
      localinterval=p0p1dis/(reticulationcount+1),
      p0p1dir=p0.getDirection(p1);
    Set<ZPolygon> spannedpolygons=getSpannedPolygons();
    //disconnect seg's p0 from p1
    ZPointRole p0role,p1role;
    for(ZPolygon polygon:spannedpolygons){
      p0role=p0.getRole(polygon);
      p1role=p1.getRole(polygon);
      p0role.disconnect(p1);
      p1role.disconnect(p0);}
    //create and link points
    List<ZPoint> reticulation=new ArrayList<ZPoint>();
    ZPoint 
      priorpoint=p0,
      insertedpoint=null;
    for(int i=1;i<=reticulationcount;i++){
      insertedpoint=new ZPoint(GD.getPoint_PointDirectionInterval(p0.x,p0.y,p0p1dir,i*localinterval));
      reticulation.add(insertedpoint);
      link(priorpoint,insertedpoint,spannedpolygons);
      priorpoint=insertedpoint;}
    //link the last 2 points
    for(ZPolygon polygon:spannedpolygons){
      p0role=insertedpoint.getRole(polygon);
      p1role=p1.getRole(polygon);
      p0role.connect(p1);
      p1role.connect(insertedpoint);}}
  
  private void link(ZPoint p0,ZPoint p1,Set<ZPolygon> spannedpolygons){
    ZPointRole p0role,p1role;
    for(ZPolygon polygon:spannedpolygons){
      p0role=p0.getRole(polygon);
      p1role=new ZPointRole(polygon);
      p1.addRole(p1role);
      p0role.connect(p1);
      p1role.connect(p0);}}
  
  /*
   * ################################
   * INSERT MIDPOINT
   * ################################
   */
  
  public ZPoint insertMidPoint(){
    Set<ZPolygon> sp=getSpannedPolygons();
    disconnect(sp);
    ZPoint pmid=new ZPoint(GD.getPoint_Mid2Points(p0.x,p0.y,p1.x,p1.y));
    ZPointRole p0role,p1role,pmidrole;
    for(ZPolygon polygon:sp){
      p0role=p0.getRole(polygon);
      p1role=p1.getRole(polygon);
      pmidrole=new ZPointRole(polygon);
      pmid.addRole(pmidrole);
      p0role.connect(pmid);
      pmidrole.connect(p0);
      p1role.connect(pmid);
      pmidrole.connect(p1);}
    return pmid;}
  
  private void disconnect(Set<ZPolygon> sp){
    ZPointRole p0role,p1role;
    for(ZPolygon polygon:sp){
      p0role=p0.getRole(polygon);
      p1role=p1.getRole(polygon);
      p0role.disconnect(p1);
      p1role.disconnect(p0);}}
  
  /*
   * ################################
   * OBJECT
   * note that we're using geometry for identity
   * ################################
   */
  
  public int hashCode(){
    return p0.hashCode()+p1.hashCode();}
  
  //TODO if we use zsegs for anything weird this might fuck us up
  public boolean equals(Object a){
    ZSeg b=(ZSeg)a;
    return(b.p0==p0&&b.p1==p1)||(b.p1==p0&&b.p0==p1);}
  
  public String toString(){
    String s="["+p0+p1+"]";
    return s;}
  
}
