package org.fleen.junk.mesh;

import java.util.Iterator;

import org.fleen.forsythia.composition.FGrid;
import org.fleen.forsythia.composition.FPolygon;
import org.fleen.forsythia.composition.ForsythiaComposition;
import org.fleen.forsythia.composition.ForsythiaTreeNode;

/*
 * metrics for collision map and splice map
 * derived from forsythia composition
 */
public class MapMetrics{
  
  /*
   * ################################
   * CONSTRUCTOR
   * ################################
   */
  
  public MapMetrics(ForsythiaComposition fc){
    gleanSmallestFish(fc);
    init(fc);}
  
  /*
   * ################################
   * ACCESS METRICS
   * ################################
   */
  
  public double
    smallestfish,
    mapxmin,mapymin,mapxmax,mapymax,
    mapwidth,mapheight,
    sectorspan,
    collisionrange;
  
  public int 
    sectorarraywidth,
    sectorarrayheight;
  
  /*
   * ################################
   * GLEAN SMALLEST FISH
   * get the smallest fish value for all grids in the composition
   * this is our base interval for calculating sector span and collision range
   * ################################
   */
  
  private void gleanSmallestFish(ForsythiaComposition fc){
    Iterator<ForsythiaTreeNode> i=fc.getGridIterator();
    smallestfish=Double.MAX_VALUE;
    FGrid g;
    double a;
    while(i.hasNext()){
      g=(FGrid)i.next();
      a=g.getLocalKGrid().getFish();
      if(a<smallestfish)
        smallestfish=a;}}
  
  /*
   * ################################
   * CALCULATE METRICS
   * our map is bounded by the root polygon + fudge
   * ################################
   */
   
  private static final double
    //map span fudge in terms of smallestfish
    MAPSPANFUDGE=0.5,
    //sector span in terms of smallestfish
    SECTORSPAN=0.09,
    //collision range in terms of smallest fish
    COLLISIONRANGE=0.01;
    
  private void init(ForsythiaComposition fc){
    //get root polygon bounding rectangle limits
    FPolygon a=fc.getRootPolygon();
    double[][] points=a.getDPolygon(); 
    mapxmin=Double.MAX_VALUE;
    mapymin=mapxmin;
    mapxmax=Double.MIN_VALUE;
    mapymax=mapxmax;
    for(double[] p:points){
      if(p[0]<mapxmin)mapxmin=p[0];
      if(p[0]>mapxmax)mapxmax=p[0];
      if(p[1]<mapymin)mapymin=p[1];
      if(p[1]>mapymax)mapymax=p[1];}
    //add fudge
    double fudge=MAPSPANFUDGE*smallestfish;
    mapxmin-=fudge;
    mapxmax+=fudge;
    mapymin-=fudge;
    mapymax+=fudge;
    //glean dimensions
    mapwidth=mapxmax-mapxmin;
    mapheight=mapymax-mapymin;
    //get map span, sector span, collision range
    sectorspan=SECTORSPAN*smallestfish;
    collisionrange=COLLISIONRANGE*smallestfish;
    //get map sector array dimensions
    sectorarraywidth=(int)(mapwidth/sectorspan)+1;
    sectorarrayheight=(int)(mapheight/sectorspan)+1;}
  
  /*
   * ################################
   * OBJECT
   * ################################
   */
  
  public String toString(){
    String s=
      "mapxmin="+mapxmin+"\n"+
      "mapxmax="+mapxmax+"\n"+
      "mapymin="+mapymin+"\n"+
      "mapymax="+mapymax+"\n"+
      "sectorspan="+sectorspan+"\n"+
      "sectorarraywidth="+sectorarraywidth+"\n"+
      "sectorarrayheight="+sectorarrayheight;
    return s;}

}
