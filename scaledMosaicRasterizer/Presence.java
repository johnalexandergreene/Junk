package org.fleen.junk.scaledMosaicRasterizer;

/*
 * this is a report of a polygon's proximity to a square's center point
 * a square may have any number of these
 */
public class Presence{
  
  //the polygon whose post this is
  double[][] polygon;
  //distance from outside of polygon is positive, inside is negative
  //we use the distance squared because we like the curve anyway and it makes the alg go a bit faster
  double distancesq;
  //the next presence in a single-linked list of presence 
  Presence nextpresence;
  
  /*
   * side : true is inside, false is outside
   */
  Presence(double[][] polygon,boolean side,double distance){
    this.polygon=polygon;
    //if side is true (inside) then flip distance to negative to indicate such
    if(side)distance=-distance;
    this.distancesq=distance;}
  
  public boolean isInside(){
    return distancesq<0;}
  
  public boolean isOutside(){
    return distancesq>0;}

}
