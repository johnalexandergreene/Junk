package org.fleen.junk.azalea;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fleen.geom_2D.DPoint;
import org.fleen.geom_2D.DPolygon;
import org.fleen.geom_2D.GD;

/*
 * The essential geometry of a ZLattice
 * Copied before any fluffing has occurred
 * A permanent geometry cache
 * With methods for getting angles and such
 * Its points are the primary points
 */
public class ZLatticeSoul{
  
  /*
   * ################################
   * CONSTRUCTOR
   * ################################
   */
  
  ZLatticeSoul(ZLattice lattice){
    this.lattice=lattice;
    initPrimaryGeometry();
    initBasicGeometry();}
  
  /*
   * ################################
   * LATTICE
   * ################################
   */
  
  ZLattice lattice;
  
  /*
   * ################################
   * GEOMETRY
   * ################################
   */
  
  private Map<ZPoint,DPoint> basicpointbyprimarypoint=new Hashtable<ZPoint,DPoint>();
  private Map<DPoint,ZPoint> primarypointbybasicpoint=new Hashtable<DPoint,ZPoint>();
  
  /*
   * ++++++++++++++++++++++++++++++++
   * PRIMARY GEOMETRY
   * We have primary points and polygons
   * Copied from the zlattice zpolygons before fluffing or or other transformation has occurred 
   * We may move them but we do not destroy them
   * We hold a reference to them because it's useful for our fluffing process. 
   * ++++++++++++++++++++++++++++++++
   */
  
  private List<ZPoint> primaryouterpolygon;
  private List<List<ZPoint>> primaryinnerpolygons=null;
  
  private void initPrimaryGeometry(){
    primaryouterpolygon=lattice.getOuterPolygon().getPoints();
    List<ZPolygon> inner=lattice.getInnerPolygons();
    primaryinnerpolygons=new ArrayList<List<ZPoint>>(inner.size());
    for(ZPolygon p:inner)
      primaryinnerpolygons.add(p.getPoints());}
  
  public List<ZPoint> getPrimaryOuterPolygon(){
    //we protect it
    return new ArrayList<ZPoint>(primaryouterpolygon);}
  
  public List<List<ZPoint>> getPrimaryPolygons(){
    List<List<ZPoint>> pp=new ArrayList<List<ZPoint>>(primaryinnerpolygons.size()+1);
    pp.add(primaryouterpolygon);
    pp.addAll(primaryinnerpolygons);
    return pp;}
  
  public Set<ZPoint> getPrimaryPoints(){
    Set<ZPoint> pp=new HashSet<ZPoint>();
    for(List<ZPoint> p:getPrimaryPolygons())
      pp.addAll(p);
    return pp;}
  
  /*
   * returns true if the specified point is a member of the primary points list
   */
  public boolean isPrimary(ZPoint point){
    return getPrimaryPoints().contains(point);}
  
  /**
   * The angle at every primary point
   * primary angles corrospond to primary points by index
   */
  public List<Double> getPrimaryAngles(){
    int 
      iprior,
      inext,
      s=primaryouterpolygon.size();
    double a;
    List<Double> primaryangles=new ArrayList<Double>(s);
    ZPoint pprior,p,pnext;
    for(int i=0;i<s;i++){
      iprior=i-1;
      if(iprior==-1)iprior=s-1;
      inext=i+1;
      if(inext==s)inext=0;
      pprior=primaryouterpolygon.get(iprior);
      p=primaryouterpolygon.get(i);
      pnext=primaryouterpolygon.get(inext);
      a=GD.getAngle_3Points(pprior.x,pprior.y,p.x,p.y,pnext.x,pnext.y);
      primaryangles.add(a);}
    return primaryangles;}
  
  /**
   * returns primary points mapped to angles
   */
  public Map<ZPoint,Double> getPrimaryAnglesMap(){
    Map<ZPoint,Double> primaryangles=new Hashtable<ZPoint,Double>();
    List<Double> palist=getPrimaryAngles();
    int s=primaryouterpolygon.size();
    for(int i=0;i<s;i++)
      primaryangles.put(primaryouterpolygon.get(i),palist.get(i));
    return primaryangles;}
  
  /*
   * ++++++++++++++++++++++++++++++++
   * BASIC GEOMETRY
   * We have basic polygons. 
   * The essential shape of each polygon, as dictated by the forsythia
   * We neither move them nor destroy them
   * we just refer to them
   * ++++++++++++++++++++++++++++++++
   */
  
  private DPolygon basicouterpolygon;
  private List<DPolygon> basicinnerpolygons=null;
  
  private void initBasicGeometry(){
    DPoint dp;
    basicouterpolygon=new DPolygon(primaryouterpolygon.size());
    for(ZPoint zp:primaryouterpolygon){
      dp=new DPoint(zp.x,zp.y);
      basicpointbyprimarypoint.put(zp,dp);
      primarypointbybasicpoint.put(dp,zp);
      basicouterpolygon.add(dp);}
    //
    basicinnerpolygons=new ArrayList<DPolygon>(primaryinnerpolygons.size());
    DPolygon dpolygon;
    for(List<ZPoint> zpolygon:primaryinnerpolygons){
      dpolygon=new DPolygon(zpolygon.size());
      basicinnerpolygons.add(dpolygon);
      for(ZPoint zp:zpolygon){
        dp=new DPoint(zp.x,zp.y);
        basicpointbyprimarypoint.put(zp,dp);
        primarypointbybasicpoint.put(dp,zp);
        dpolygon.add(dp);}}}
  
}
