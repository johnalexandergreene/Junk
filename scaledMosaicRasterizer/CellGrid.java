package org.fleen.junk.scaledMosaicRasterizer;


/*
 * cell span is 1.0
 */
class CellGrid{
  
  static final Boolean INSIDE=true,OUTSIDE=false;
  
  int width,height;
  Cell[][] cells;
  
  /*
   * ################################
   * INIT
   * ################################
   */
  
  CellGrid(int w,int h){
    width=w;
    height=h;
    //create cells
    cells=new Cell[w][h];
    for(int x=0;x<w;x++){
      for(int y=0;y<h;y++){
        cells[x][y]=new Cell(x,y);}}
    //init neighbor references THIS MIGHT BE A FATAL PIG
    for(int x=0;x<w;x++){
      for(int y=0;y<h;y++){
        cells[x][y].initNeighbors(this);}}}
  
  /*
   * ################################
   * METRICS
   * ################################
   */
  
  int getWidth(){
    return cells.length;}
  
  int getHeight(){
    return cells[0].length;}
  
  /*
   * ################################
   * CELL INTERFACE
   * ################################
   */

  //get cell by coors
  Cell getCell(int x,int y){
    if(x<0||x>=width||y<0||y>=height)return null;
    return cells[x][y];}
  
  //return the cell upon which the specified point falls
  Cell getCell(double x,double y){
    int cx=(int)x,cy=(int)y;
    if(cx<0||cx>=width||cy<0||cy>=height)return null;
    return cells[cx][cy];}
  
}
