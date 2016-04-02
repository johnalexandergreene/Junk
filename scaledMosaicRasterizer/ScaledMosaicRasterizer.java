package org.fleen.junk.scaledMosaicRasterizer;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fleen.forsythia.app.grammarEditor.generator.colorizer.ColorMap;
import org.fleen.forsythia.app.grammarEditor.generator.leafMosaic.LeafMosaic;
import org.fleen.forsythia.app.grammarEditor.generator.leafMosaic.MPoint;
import org.fleen.forsythia.app.grammarEditor.generator.leafMosaic.MPolygon;

/*
 * 
 * Map a mosaic of polygons to a grid of square cells
 * scale is fixed. If scal changes then create another ScaledMosaicRasterizer
 * refer to cells by coordinates if you want a whole big rectangle of raster for an image or something 
 * then get all the cell color values from (x0,y0) to (x1,y1) via something like  
 * for(int x=x0;x<x1;x++)
 *   for(int y=y0;y<y1;y++)
 *     getCellColor(x,y)
 * 
 * 
 * ALG NOTES
 * for every polygon in the mosaic : P
 *   draw its stroke in square-cells. those cells are list C0
 *   for every cell in C0 : CELL0
 *     add Post object for P : CELL0.addPost(new Post(x,y,polygon,true))
 *     get every neighbor of every cell in C0 that is has not posted for P : C1
 *     
 *   for every cell in C1 : CELL1
 *     add Post object for P : CELL1.addPost(new Post(x,y,polygon,true))
 *     get every neighbor of every cell in C1 that is has not posted for P : C2
 *     
 *   now we turn off inside/outside calculation because further generations will not differ from their parent.
 *   
 *   for every cell in C2 : CELL2
 *     add Post object for P : CELL2.addPost(new Post(x,y,polygon,false))
 *     get every neighbor of every cell in C2 that is has not posted for P : C3
 *   
 *   REPEAT what we did for C2 for C3, C4, C5 etc
 *   inside cells stop calculating distance at INSIDE_CELL_DISTANCE_CALCULATION_LIMIT
 *   inside cells stop breeding when there are no more unposted cells inside the polygon
 *   outside cells stop breeding when CN.distance > OUTSIDE_CELL_DISTANCE_BREEDING_LIMIT
 *   
 *     
 *       
 */
public class ScaledMosaicRasterizer{
  
  /*
   * ################################
   * INIT
   * ################################
   */
  
  /**
   * @param w grid width
   * @param h grid height
   * @param m margin span
   * @param mosaic the mosaic getting rasterized
   */
  public ScaledMosaicRasterizer(int w,int h,int m,LeafMosaic mosaic,ColorMap colormap){
    this(w,h,m,mosaic,colormap,GLOWSIZE_DEFAULT,BACKGROUNDRGB_DEFAULT);}
  
  public ScaledMosaicRasterizer(int w,int h,int m,LeafMosaic mosaic,ColorMap colormap,double glowsize,int backgroundrgb){
    System.out.println("####CREATING SCALED MOSAIC RASTER--<--");
    this.glowsize=glowsize;
    this.backgroundrgb=backgroundrgb;
    initMetrics(w,h,m,mosaic);
    //get mosaic polygons transformed to grid coordinates and converted 
    //into double[][] polygons, keyed by mosaic polygons
    Map<ZPolygon,double[][]> transformedpolygons=getTransformedPolygons(mosaic);
    double[][] transformedboundingpolygon=getTransformedOuterPolygon(mosaic.getBoundingPolygon());
    //map the transformed polygons to a cellgrid
    CellGrid cellgrid=getCellGrid(transformedpolygons,transformedboundingpolygon);
    //merge the colormap with the transformedpolygons map to associate double[][] polygons with colors
    Map<double[][],Integer> tcolormap=new Hashtable<double[][],Integer>();
    double[][] tpolygon;
    for(ZPolygon mpolygon:transformedpolygons.keySet()){
      tpolygon=transformedpolygons.get(mpolygon);
      tcolormap.put(tpolygon,colormap.getSplitPaletteEggLevelPolygonColor(mpolygon));}
    //do cell rgb array
    cellrgb=new int[gridwidth][gridheight];
    for(int x=0;x<w;x++){
      for(int y=0;y<h;y++){
        cellrgb[x][y]=cellgrid.getCell(x,y).getColor(
          tcolormap,glowsize,transformedboundingpolygon,this.backgroundrgb);}}}
  
  /*
   * for every polygon in the mosaic
   * transform it's points
   * convert it from MPolygon to double[][] 
   */
  Map<ZPolygon,double[][]> getTransformedPolygons(LeafMosaic mosaic){
    Map<ZPolygon,double[][]> transformedmosaic=new Hashtable<ZPolygon,double[][]>();
    int mpolygonpointcount;
    double[][] transformedpolygon;
    ZPolygon mpolygon;
    Iterator<ZPolygon> impoly=mosaic.getPolygonIterator();
    while(impoly.hasNext()){
      mpolygon=impoly.next();
      mpolygonpointcount=mpolygon.points.length;
      transformedpolygon=new double[mpolygon.points.length][2];
      transformedmosaic.put(mpolygon,transformedpolygon);
      for(int ipoint=0;ipoint<mpolygonpointcount;ipoint++){
        transformedpolygon[ipoint]=
          transformMosaicPointToGridPoint(mpolygon.points[ipoint].x,mpolygon.points[ipoint].y);}}
    return transformedmosaic;}
  
  double[][] getTransformedOuterPolygon(double[][] untransformed){
    double[][] transformed=new double[untransformed.length][2];
    for(int i=0;i<untransformed.length;i++)
      transformed[i]=transformMosaicPointToGridPoint(untransformed[i]);
    return transformed;}
  
  /*
   * ################################
   * CELL RGB
   * ################################
   */
  
  private int[][] cellrgb;
  //the size, in grid coors, of a polygon's glow. The distance from a polygon that we blend into adjacent polygons
  private static final double GLOWSIZE_DEFAULT=1.0;
  private double glowsize=GLOWSIZE_DEFAULT;
  //the rgb of the background filling polygon
  private static final int BACKGROUNDRGB_DEFAULT=0;
  private int backgroundrgb=BACKGROUNDRGB_DEFAULT;
  
  public int getCellRGB(int x,int y){
    return cellrgb[x][y];}
  
  public BufferedImage getImage(){
    BufferedImage i=new BufferedImage(gridwidth,gridheight,BufferedImage.TYPE_INT_RGB);
    for(int x=0;x<gridwidth;x++){
      for(int y=0;y<gridheight;y++){
        i.setRGB(x,y,cellrgb[x][y]);}}
    return i;}
  
  /*
   * ################################
   * METRICS
   * ################################
   */
  
  static final int 
    WIDTHMIN=3,
    HEIGHTMIN=3,
    MARGINMIN=1;
  
  int 
    gridwidth,gridheight,gridmargin;//scaled. that is, in map coors. a square is 1x1.
  double 
    scale,
    mosaicxoff,mosaicyoff;//unscaled. that is, in mosaic coors
  
  /*
   * calculate for nice fitted centered image
   */
  void initMetrics(int w,int h,int m,LeafMosaic polygons){
    //store grid dimensions. enforce minimums.
    gridwidth=w;
    gridheight=h;
    gridmargin=m;
    if(gridmargin<MARGINMIN)gridmargin=MARGINMIN;
    if(gridwidth<WIDTHMIN)gridwidth=WIDTHMIN;
    if(gridheight<HEIGHTMIN)gridheight=HEIGHTMIN;
    //get mosaic offsets for transform
    double[] minmax=getMosaicMinMax(polygons);
    double 
      //polygons bounds
      mosaicw=minmax[1]-minmax[0],
      mosaich=minmax[3]-minmax[2],
      //w/h ratio for map and polygons
      mapr=((double)(gridwidth-gridmargin*2))/((double)(gridheight-gridmargin*2)),
      mosaicr=mosaicw/mosaich;
    //if the map is squattier than the polygons
    if(mapr>mosaicr){
      scale=((double)(gridheight-gridmargin*2))/mosaich;
      mosaicyoff=minmax[2]+gridmargin/scale;
      mosaicxoff=minmax[0]+(((double)gridwidth)/scale-mosaicw)/2;
    //if the map is slenderer than the polygons
    }else{
      scale=((double)(gridwidth-gridmargin*2))/mosaicw;
      mosaicxoff=minmax[0]+gridmargin/scale;
      mosaicyoff=minmax[2]+(((double)gridheight)/scale-mosaich)/2;}}
  
  double[] getMosaicMinMax(LeafMosaic mosaic){
    double 
      minx=Double.MAX_VALUE,
      maxx=Double.MIN_VALUE,
      miny=minx,
      maxy=maxx;
    Iterator<ZPolygon> impoly=mosaic.getPolygonIterator();
    ZPolygon mpolygon;
    while(impoly.hasNext()){
      mpolygon=impoly.next();
      for(ZPoint p:mpolygon.points){
        if(p.x<minx)minx=p.x;
        if(p.x>maxx)maxx=p.x;
        if(p.y<miny)miny=p.y;
        if(p.y>maxy)maxy=p.y;}}
    return new double[]{minx,maxx,miny,maxy};}
  
  /*
   * transform mosaic point to grid point
   * treating grid like a continuous plane of dimensions gridwidthxgridheight
   * after this getting the cell square of a transformed mosaic point is simply a 
   * matter of casting double[] to int[]
   */
  double[] transformMosaicPointToGridPoint(double[] mp){
    double[] gp={
      (mp[0]+mosaicxoff)*scale,
      (mp[1]+mosaicyoff)*scale};
    return gp;}
  
  double[] transformMosaicPointToGridPoint(double x,double y){
    double[] gp={
      (x+mosaicxoff)*scale,
      (y+mosaicyoff)*scale};
    return gp;}
  
  /*
   * returns the minmax of the specified cell, transformed to the mosaic coordinate system
   * {minx,maxx,miny,maxy}
   */
  double[] getCellMinMax(int x,int y){
    double 
      xmin=((double)x)/scale-mosaicxoff,
      ymin=((double)y)/scale-mosaicyoff;
    double[] a={
      xmin,
      xmin+1.0/scale,
      ymin,
      ymin+1.0/scale};
    return a;}
  
  /*
   * ################################
   * CREATE CELL GRID AND MAP THE MOSAIC TO IT
   * map polygons relative to cells by presence. 
   * The more a polygon covers a cells, the more than polygon is present
   * presence translates to color
   * ################################
   */
    
  private CellGrid getCellGrid(Map<ZPolygon,double[][]> polygons,double[][] boundingpolygon){
    CellGrid cellgrid=new CellGrid(gridwidth,gridheight);
    for(double[][] polygon:polygons.values())
      mapPolygon(cellgrid,polygon);
    mapBoundingPolygon(cellgrid,boundingpolygon);
    return cellgrid;}
  
  /*
   * ################################
   * MAP POLYGONS
   * ################################
   */
  
  /*
   * do cells at edge of polygon
   * do cells neighboring edgecells
   * do the rest of the cells
   */
  private void mapPolygon(CellGrid cellgrid,double[][] polygon){  
    Set<Cell> edgecells=mapPolygonEdge(cellgrid,polygon);
    if(edgecells==null)return;//fail on null edge cells. a testing thing.
    Layer1 layer1cells=doLayer1Cells(polygon,edgecells);
    //do inside cells
    Set<Cell> cells=layer1cells.inside;
    while(!cells.isEmpty())
      cells=doInsideCells(polygon,cells);
    //do outside cells
    cells=layer1cells.outside;
    while(!cells.isEmpty())
      cells=doOutsideCells(polygon,cells);}
  
  Set<Cell> doInsideCells(double[][] polygon,Set<Cell> cells){
    Set<Cell> morecells=new HashSet<Cell>();
    Presence post;
    for(Cell cell:cells){
      for(Cell neighbor:cell.neighbors){
        if(neighbor!=null){
          post=neighbor.postPolygonPresence(polygon,true,0.0);
          if(post!=null)
            morecells.add(neighbor);}}}
    return morecells;}
  
  Set<Cell> doOutsideCells(double[][] polygon,Set<Cell> cells){
    Set<Cell> morecells=new HashSet<Cell>();
    Presence post;
    for(Cell cell:cells){
      for(Cell ncell:cell.neighbors){
        if(ncell!=null){
          post=ncell.postPolygonPresence(polygon,false,null);
          if(post!=null&&post.distancesq<glowsize)
            morecells.add(ncell);}}}
    return morecells;}
  
  /*
   * ################################
   * MAP BOUNDING POLYGON
   * ################################
   */
  
  void mapBoundingPolygon(CellGrid cellgrid,double[][] polygon){
    Set<Cell> edgecells=mapPolygonEdge(cellgrid,polygon);
    if(edgecells==null)return;//fail on null edge cells. a testing thing.
    Layer1 layer1=doLayer1Cells(polygon,edgecells);
    //note that we treat the outside cells like inside cells
    doInsideCells(polygon,layer1.outside);}
  
  /*
   * ################################
   * MAP POLYGON EDGE
   * map the polygon to the cells crossed by its edge 
   * we get them by drawing on the grid
   * ################################
   */
  
  /*
   * for each pair of vertices on the polygon edge we have a corrosponding square, specified
   * by coordinate pair in the vertexsquarecoors list
   * for each pair of adjacent vertices we have a pair of squares : s0,s1
   * map the strip squares [s0...s1)
   * note that we exclude s1 to avoid redundant mapping 
   */
  Set<Cell> mapPolygonEdge(CellGrid cellgrid,double[][] polygon){
    //get the cell at each vertex on the polygon
    //if a cell at a vertex is null (off grid) then fail
    //(it happens sometimes when testing the curve processor)
    List<Cell> vertexcells=new ArrayList<Cell>();
    Cell cell;
    for(double[] p:polygon){
      cell=cellgrid.getCell(p[0],p[1]);
      if(cell==null)return null;
      vertexcells.add(cell);}
    //get cells by drawing lines between adjacent vertices
    int i0,i1,s=polygon.length;
    Cell c0,c1;
    List<Cell> edgecells=new ArrayList<Cell>(),segcells;
    for(i0=0;i0<s;i0++){
      i1=i0+1;
      if(i1==s)i1=0;
      c0=vertexcells.get(i0);
      c1=vertexcells.get(i1);
      segcells=doSegCells(
        cellgrid,polygon,
        c0.x,c0.y,
        c1.x,c1.y);
      edgecells.addAll(segcells);}
    return new HashSet<Cell>(edgecells);}
  
  /*
   * ++++++++++++++++++++++++++++++++++++++++
   * PSEUDOBRESENHAM SUPERCOVER LINE DRAW
   * Map a polygon edge seg to a strip of cells that intersect that seg 
   * To avoid repeated points we exclude the second parameter point from mapping
   * 
   * use Bresenham-like algorithm to address a line of squares from (y1,x1) to (y2,x2) 
   * The difference from Bresenham is that ALL the points of the line are 
   * printed, not only one per x coordinate. 
   * Principles of the Bresenham's algorithm (heavily modified) were taken from: 
   * http://www.intranet.ca/~sshah/waste/art7.html 
   * 
   * TODO we should prolly just use the regular BRESENHAM for reliability's sake
   * 
   */
  List<Cell> doSegCells(CellGrid cellgrid,double[][] polygon,int x0,int y0,int x1,int y1){
    List<Cell> segcells=new ArrayList<Cell>();
    int i;               // loop counter 
    int ystep, xstep;    // the step on y and x axis 
    int error;           // the error accumulated during the increment 
    int errorprev;       // vision the previous value of the error variable 
    int y = y0, x = x0;  // the line points 
    double ddy, ddx;        // compulsory variables: the double values of dy and dx 
    int dx = x1 - x0; 
    int dy = y1 - y0;  
    doSegCell(cellgrid,x0,y0,polygon,segcells);
    // NB the last point can't be here, because of its previous point (which has to be verified) 
    if (dy < 0){ 
      ystep = -1; 
      dy = -dy; 
    }else 
      ystep = 1; 
    if (dx < 0){ 
      xstep = -1; 
      dx = -dx; 
    }else 
      xstep = 1; 
    ddy = 2 * dy;  // work with double values for full precision 
    ddx = 2 * dx; 
    if (ddx >= ddy){  // first octant (0 <= slope <= 1) 
      // compulsory initialization (even for errorprev, needed when dx==dy) 
      errorprev = error = dx;  // start in the middle of the square 
      for (i=0 ; i < dx ; i++){  // do not use the first point (already done) 
        x += xstep; 
        error += ddy; 
        if (error > ddx){  // increment y if AFTER the middle ( > ) 
          y += ystep; 
          error -= ddx; 
          // three cases (octant == right->right-top for directions below): 
          if (error + errorprev < ddx){  // bottom square also
            doSegCell(cellgrid,x,y-ystep,polygon,segcells);
          }else if(error + errorprev > ddx){  // left square also 
            doSegCell(cellgrid,x-xstep,y,polygon,segcells);
          }else{  // corner: bottom and left squares also 
            doSegCell(cellgrid,x,y-ystep,polygon,segcells);
            doSegCell(cellgrid,x-xstep,y,polygon,segcells);}} 
        doSegCell(cellgrid,x,y,polygon,segcells);
        errorprev = error;} 
    }else{// the same as above 
      errorprev = error = dy; 
      for (i=0 ; i < dy ; i++){ 
        y += ystep; 
        error += ddx; 
        if (error > ddy){ 
          x += xstep; 
          error -= ddy; 
          if (error + errorprev < ddy){ 
            doSegCell(cellgrid,x-xstep,y,polygon,segcells);
          }else if (error + errorprev > ddy){ 
            doSegCell(cellgrid,x,y-ystep,polygon,segcells);
          }else{ 
            doSegCell(cellgrid,x-xstep,y,polygon,segcells);
            doSegCell(cellgrid,x,y-ystep,polygon,segcells);}}
        doSegCell(cellgrid,x,y,polygon,segcells);
        segcells.add(cellgrid.getCell(x,y));
        errorprev = error;}}
    return segcells;}

  void doSegCell(CellGrid cellgrid,int x,int y,double[][] polygon,List<Cell> segcells){
    Cell c=cellgrid.getCell(x,y);
    c.postPolygonPresence(polygon,null,null);
    segcells.add(c);}
  
  /*
   * ################################
   * LAYER1 CELLS
   * the layer1 cells are the neighbors of the edge cells
   * we gather and post all neighbor cells that have not already been posted 
   * ################################
   */
  
  Layer1 doLayer1Cells(double[][] polygon,Set<Cell> edgecells){
    Layer1 layer1=new Layer1();
    Presence post;
    for(Cell edgecell:edgecells){
      for(Cell ncell:edgecell.neighbors){
        //if the neighbor cell is nonnull; that is, within the bounds of the grid.
        if(ncell!=null){
          post=ncell.postPolygonPresence(polygon,null,null);
          if(post!=null){
            if(post.isInside()){
              layer1.inside.add(ncell);
            }else{
              layer1.outside.add(ncell);}}}}}
    return layer1;}
  
  class Layer1{
    Set<Cell> 
      outside=new HashSet<Cell>(),
      inside=new HashSet<Cell>();}
   
  /*
   * ################################
   * ################################
   * TEST
   * ################################
   * ################################
   */
  
//  public static final void main(String[] a){
//    test000();
//  }
//  
//  static final void test000(){
//    double[][] p0={
//      {0,0},
//      {0,1},
//      {1,1},
//      {1,0}
//    };
//    double[][] p1={
//      {1,0},
//      {1,1},
//      {2,1},
//      {2,0}
//      };
//    List<double[][]> polygons=new ArrayList<double[][]>(1);
//    polygons.add(p0);
//    polygons.add(p1);
//    Map<double[][],Integer> colors=new Hashtable<double[][],Integer>();
//    colors.put(p0,Color.red.getRGB());
//    colors.put(p1,Color.blue.getRGB());
//    Rasterizer r=new Rasterizer();
//    int[][] z=r.getRaster(8,9,1,polygons,colors);
//    System.out.println("fin");
//  }
  
  
//  static final void test000(){
//    double[][] p0={
//      {0,0},
//      {0,1},
//      {1,1},
//      {1,0}
//    };
//    List<double[][]> polygons=new ArrayList<double[][]>(1);
//    polygons.add(p0);
//    Map<double[][],Integer> colors=new Hashtable<double[][],Integer>();
//    colors.put(p0,Color.red.getRGB());
//    Rasterizer r=new Rasterizer();
//    r.getRaster(7,5,1,polygons,colors);
//  }
  
  
}
