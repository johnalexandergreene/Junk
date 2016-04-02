package org.fleen.junk.mesh.fat;

import java.util.List;

import org.fleen.core.g2D.G2D;
import org.fleen.junk.mesh.MPoint;
import org.fleen.junk.mesh.MShape;

public class FatController_Seg implements FatController{
  
  public FatController_Seg(Fat fat,List<MPoint> outerpoints,List<MPoint> innerpoints,int index){
    this.fat=fat;
    initPoints(outerpoints,innerpoints,index);
    initIdealAngles();}
  
  public Fat fat;
  public MPoint 
    outerpoint,
    outerpointprior,
    outerpointnext,
    innerpoint,
    innerpointprior,
    innerpointnext;
  
  private void initPoints(List<MPoint> outerpoints,List<MPoint> innerpoints,int index){
    int 
      s=outerpoints.size(),
      iprior,inext;
    iprior=index-1;
    if(iprior==-1)iprior=s-1;
    inext=index+1;
    if(inext==s)inext=0;
    outerpoint=outerpoints.get(index);
    outerpointprior=outerpoints.get(iprior);
    outerpointnext=outerpoints.get(inext);
    innerpoint=innerpoints.get(index);
    innerpointprior=innerpoints.get(iprior);
    innerpointnext=innerpoints.get(inext);}
  
  /*
   * ################################
   * IDEAL ANGLES
   * these angles are used as a reference when creating the idealizing vector, maybe the inflating vector too
   * ################################
   */
  
  //these are the angles at init
  //formed by the figure described by this seg, its points of contact 
  //with the inner and outer edges, and the associated segs on those polygons.
  //we use these angles for calculating vectors at points when doing fluffer.inflate
  //we use them as the ideal angle. the neutral state of our springs
  public double 
    //the angle formed by the seg of the outer polygon prior (counterclockwise) to outerpoint and this seg
    idealangle_outer0,
    //the angle formed by the seg of the outer polygon after (clockwise) to outerpoint and this seg
    idealangle_outer1,
    //the angle formed by the seg of the inner polygon prior (counterclockwise) to innerpoint and this seg 
    idealangle_inner0,
    //the angle formed by the seg of the inner polygon after (clockwise) to innerpoint and this seg 
    idealangle_inner1;
  
  //at init our inner and outer points are coincident so we just use half the angle pprior-p-pnext for everything 
  private void initIdealAngles(){
    double outerangles=G2D.getAngle_3Points(
      outerpointprior.x,outerpointprior.y,outerpoint.x,outerpoint.y,outerpointnext.x,outerpointnext.y)/2.0;
    double innerangles=G2D.PI-outerangles;
    idealangle_outer0=outerangles;
    idealangle_outer1=outerangles;
    idealangle_inner0=innerangles;
    idealangle_inner1=innerangles;}
  
  //TODO  maybe this should be a parameter. probably
  private static final double CLOSEINTERVAL=0.5;
  
  /*
   * we do this 2 ways. If the innerpoint and outerpoint are close then we use the inward direction at pprior-p-pnext
   * if outerpoint and innerpoint are not close then we use dir(outerpoint,innerpoint)
   */
  public double getInwardDirection(){
    double inward,dis=G2D.getDistance_PointPoint(outerpoint.x,outerpoint.y,innerpoint.x,innerpoint.y);
    if(dis<CLOSEINTERVAL)
      inward=G2D.getDirection_3Points(outerpointprior.x,outerpointprior.y,outerpoint.x,outerpoint.y,outerpointnext.x,outerpointnext.y);
    else
      inward=G2D.getDirection_PointPoint(outerpoint.x,outerpoint.y,innerpoint.x,innerpoint.y);
    return inward;}
  
  public double getOutwardDirection(){
    double outward,dis=G2D.getDistance_PointPoint(outerpoint.x,outerpoint.y,innerpoint.x,innerpoint.y);
    if(dis<CLOSEINTERVAL)
      outward=G2D.getDirection_3Points(outerpointnext.x,outerpointnext.y,outerpoint.x,outerpoint.y,outerpointprior.x,outerpointprior.y);
    else
      outward=G2D.getDirection_PointPoint(innerpoint.x,innerpoint.y,outerpoint.x,outerpoint.y);
    return outward;}
  
  /**
   * Returns true if this fatcontroller controls (that is, creates a vector for) the specified point.
   */
  public boolean controls(MPoint p){
    return p==innerpoint||p==outerpoint;}
  
  public double getLength(){
    return G2D.getDistance_PointPoint(outerpoint.x,outerpoint.y,innerpoint.x,innerpoint.y);}

}
