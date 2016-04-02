package org.fleen.junk.scaledMosaicRasterizer;

import java.util.Map;

import org.fleen.geom_2D.GD;

public class Cell{
  
  /*
   * ################################
   * INIT
   * ################################
   */
  
  public Cell(int x,int y){
    this.x=x;
    this.y=y;
    centerx=(((double)x)+0.5);
    centery=(((double)y)+0.5);}
  
  /*
   * ################################
   * METRICS
   * ################################
   */
  
  //coordinates
  int x,y;
  //center
  double centerx,centery;
  
  /*
   * ################################
   * GEOMETRY
   * ################################
   */
  
  Cell[] neighbors=null;
  
  void initNeighbors(CellGrid cellgrid){
    neighbors=new Cell[]{
      cellgrid.getCell(x,y+1),//n
      cellgrid.getCell(x+1,y+1),//ne
      cellgrid.getCell(x+1,y),//e
      cellgrid.getCell(x+1,y-1),//se
      cellgrid.getCell(x,y-1),//s
      cellgrid.getCell(x-1,y-1),//sw
      cellgrid.getCell(x-1,y),//w
      cellgrid.getCell(x-1,y+1),//nw
    };}
  
  /*
   * returns true if the cell's center is inside the specified polygon
   * false if it's outside
   * null if no such polygon data exists
   */
  public Boolean getSide(double[][] polygon){
    Presence p=getPost(polygon);
    if(p==null)return null;
    //distance > 0 means outside, < 0 means inside  
    return p.distancesq<0;}
  
  /*
   * ################################
   * PRESENCES
   * it has 0..n Presence objects
   * ################################
   */
  
  //first presence in a single-linked list of posts
  Presence presence0=null;
  
  /*
   * add polygon proximity data chunk to this cell 
   * if distancesq and side are unspecified then calculate them
   * if the cell was aready posted to by the specified polygon then fail 
   * return the new Presence on success
   * return null on fail
   */
  Presence postPolygonPresence(double[][] polygon,Boolean side,Double distancesq){
    //if we've already posted for this polygon then fail
    if(hasPost(polygon))return null;
    //if distancesq was not specified then calculate it
    if(distancesq==null){
      distancesq=GD.getDistanceSq_PointPolygon(centerx,centery,polygon);}
    //if side was not specified then specify it
    if(side==null)
      side=GD.getSide_PointPolygon(centerx,centery,polygon);
    //post and return success
    Presence newpresence=new Presence(polygon,side,distancesq);
    if(presence0==null){
      presence0=newpresence;
    }else{
      getLastPresence().nextpresence=newpresence;}
    return newpresence;}
  
  int getPresenceCount(){
    if(presence0==null){
      return 0;
    }else{
      int c=0;
      Presence p=presence0;
      while(p!=null){
      c++;
      p=p.nextpresence;}
    return c;}}
  
  Presence getLastPresence(){
    Presence d=presence0;
    while(d.nextpresence!=null)
      d=d.nextpresence;
    return d;}
  
  /*
   * return true if this cell has been posted to by the specified polygon
   */
  boolean hasPost(double[][] polygon){
    Presence d=presence0;
    while(true){
      if(d==null)return false;
      if(d.polygon==polygon)return true;
      d=d.nextpresence;}}
  
  /*
   * return the Post associated with the specified polygon
   * if nonesuch then return null
   */
  Presence getPost(double[][] polygon){
    Presence d=presence0;
    while(true){
      if(d==null)return null;
      if(d.polygon==polygon)return d;
      d=d.nextpresence;}}
    
  /*
   * ################################
   * COLOR
   * convert polygon post data to color value 
   * ################################
   */
  
  int getColor(
    Map<double[][],Integer> colormap,double glowsize,
    double[][] boundingpolygon,int boundingpolygonrgb){
    if(presence0==null){
      return 0;
    //if this cell has been posted to by just a single polygon 
    //(eg: this cell is in the deeper interior of a polygon)
    }else if(presence0.nextpresence==null){
      if(presence0.polygon==boundingpolygon)return boundingpolygonrgb;
      return colormap.get(presence0.polygon);
    //if this cell has been posted to by a number of polygons 
    //(eg: it exists in the ambiguous space near polygon edges)
    }else{
      return getWeightedMean(colormap,glowsize,boundingpolygon,boundingpolygonrgb);}}
  
  /*
   * outside cells get color from polygon proportional to closeness
   * inside cells get color porportional to farness
   * sum 
   * 
   * get weighted mean of points in rgb space
   * from each post is gotten a weight (from weight function) and a polygon
   * from that polygon is gotten a color : rgb
   * so for each post we have a point in rgb space and a weight
   * get the weighted mean of those rgb points
   * 
   */
  int getWeightedMean(
    Map<double[][],Integer> colormap,double glowsize,
    double[][] boundingpolygon,int boundingpolygonrgb){
    double 
      rsum=0,
      gsum=0,
      bsum=0,
      weight,
      weightsum=0;
    int prgb,red,green,blue;
    Presence presence=this.presence0;
    while(presence!=null){
      //get rgb associated with the specified polygon
      if(presence.polygon==boundingpolygon){
        prgb=boundingpolygonrgb;
      }else{
        prgb=colormap.get(presence.polygon);}
      red=(prgb>>16)&0xFF;
      green=(prgb>>8)&0xFF;
      blue=prgb&0xFF;
      //WEIGHT FUNCTION
      if(presence.polygon==boundingpolygon){
        weight=getWeightForBoundingPolygonPresence(presence.distancesq,glowsize);
      }else{
        weight=getWeightForPolygonPresence(presence.distancesq,glowsize);}
      weightsum+=weight;
      //do weighted sum part
      rsum+=((double)red)*weight;
      gsum+=((double)green)*weight;
      bsum+=((double)blue)*weight;
      presence=presence.nextpresence;}
    //
    rsum/=weightsum;
    gsum/=weightsum;
    bsum/=weightsum;
    red=(int)rsum;
    green=(int)gsum;
    blue=(int)bsum;
    int rgb=((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
    return rgb;}
  
   /*
    * WEIGHT FUNCTION
    * consider the distance from edge
    * if the point is inside (distance is negative) then weight == 1.0
    * if the point is outside : 0<d<dmax then we use our special function
    * if d is outside and d>dmax then weight = 0
    * 
    */
  
  double getWeightForPolygonPresence(double distancesq,double glowsize){
    if(distancesq<0){
      return 1.0;
    }else if(distancesq>glowsize){
      return 0;
    }else{//distance>0&&distance<dmax
      //normalize to 1.0 at edge and 0 at dmax
      distancesq=(glowsize-distancesq)/glowsize;
      //curve more. make distance squared into distance cubed
      return distancesq*distancesq;}}
  
  //reverse everything for the bounding polygon
  //for example, fill the outside
  double getWeightForBoundingPolygonPresence(double distancesq,double glowsize){
    distancesq=-distancesq;
    if(distancesq<0){
      return 1.0;
    }else if(distancesq>glowsize){
      return 0;
    }else{//distance>0&&distance<dmax
      //normalize to 1.0 at edge and 0 at dmax
      distancesq=(glowsize-distancesq)/glowsize;
      //curve more. make distance squared into distance cubed
      return distancesq*distancesq;}}
  
  /*
   * ################################
   * OBJECT
   * ################################
   */
  
//  public int hashCode(){
//    return x+y*65536;}
//  
//  public boolean equals(Object a){
//    return ((Cell)a).x==x&&((Cell)a).y==y;}
  
}
