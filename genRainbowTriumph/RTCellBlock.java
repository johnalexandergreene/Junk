package org.fleen.junk.genRainbowTriumph;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.fleen.geom_Kisrhombille.KVertex;
import org.fleen.junk.genRainbowTriumph.palette.Palette_Abstract;

/*
 * A rectangular mass of CDCells
 * diamond origin is rectangle bottom left
 * dimensions are in terms of rectangular 24 cell chunks (see notes)
 * create cells 1 chunk at a time
 * link up adjacents
 * 
 */
public class RTCellBlock{
  
  //dimensions of an unscaled unit rectangle of 6 cells
  //a cell block's dimensions are in terms of UC6Rs 
  static final double 
    UR6CWIDTH=Math.sqrt(3.0),
    UR6CHEIGHT=3.0;
  
  double scale;
  Palette_Abstract palette;
  double scannercreationprobability;
  int maxscanners;
  
  static final double SCANNERCREATIONPROBABILITY=0.6;
  static int MAXSCANNERS=8;
  
  /*
   * ################################
   * CONSTRUCTOR
   * ################################
   */
  
  /*
   * width and height in terms of UC6Rs
   * scale for cell triangles
   * palette fro rendering cells 
   */
  public RTCellBlock(
    int width,
    int height,
    double scale,
    Palette_Abstract palette,
    double scannercreationprobability,
    int maxscanners){
    this.scale=scale;
    this.palette=palette;
    this.scannercreationprobability=scannercreationprobability;
    this.maxscanners=maxscanners;
    initCells(width,height);}
  
  /*
   * ################################
   * CELLS
   * ################################
   */
  
  //all the cells
  public RTCell[] cells;
  //min and max cell coordinate values on each axis  
  public int 
    ax0min=Integer.MAX_VALUE,
    ax0max=Integer.MIN_VALUE,
    ax1min=Integer.MAX_VALUE,
    ax1max=Integer.MIN_VALUE,
    ax2min=Integer.MAX_VALUE,
    ax2max=Integer.MIN_VALUE,
    ax3min=Integer.MAX_VALUE,
    ax3max=Integer.MIN_VALUE,
    ax4min=Integer.MAX_VALUE,
    ax4max=Integer.MIN_VALUE,
    ax5min=Integer.MAX_VALUE,
    ax5max=Integer.MIN_VALUE; 
  //cells by axis, grouped by value on that axis
  //index=axiscellgroupvalue-axiscellgroupmin
  public RTCell[][] 
    ax0cellgroups,
    ax1cellgroups,
    ax2cellgroups,
    ax3cellgroups,
    ax4cellgroups,
    ax5cellgroups;

  public RTCell[] getCellGroup(int axis,int value){
    switch(axis){
    case 0:return ax0cellgroups[value-ax0min];
    case 1:return ax1cellgroups[value-ax1min];
    case 2:return ax2cellgroups[value-ax2min];
    case 3:return ax3cellgroups[value-ax3min];
    case 4:return ax4cellgroups[value-ax4min];
    case 5:return ax5cellgroups[value-ax5min];
    default:
      throw new IllegalArgumentException("foo");}}
  
  private void initCells(int w,int h){
    createCells(w,h);
    initAX0CellGroups();
    initAX1CellGroups();
    initAX2CellGroups();
    initAX3CellGroups();
    initAX4CellGroups();
    initAX5CellGroups();}
  
  private void createCells(int w,int h){
    List<RTCell> c=new ArrayList<RTCell>();
    int ant,bat,cat;
    for(int x=0;x<w;x++){
      for(int y=0;y<h;y++){
        if(x%2==y%2){
          ant=(x-y)/2;
          cat=y;
          bat=KVertex.getBat(ant,cat);
          c.add(new RTCell(this,ant,bat,cat,0));
          c.add(new RTCell(this,ant,bat,cat,1));
          c.add(new RTCell(this,ant,bat,cat,2));
          c.add(new RTCell(this,ant,bat+1,cat+1,6));
          c.add(new RTCell(this,ant,bat+1,cat+1,7));
          c.add(new RTCell(this,ant,bat+1,cat+1,8));
        }else{
          ant=((x+1)-y)/2;
          cat=y;
          bat=KVertex.getBat(ant,cat);
          c.add(new RTCell(this,ant,bat,cat,9));
          c.add(new RTCell(this,ant,bat,cat,10));
          c.add(new RTCell(this,ant,bat,cat,11));
          c.add(new RTCell(this,ant-1,bat,cat+1,3));
          c.add(new RTCell(this,ant-1,bat,cat+1,4));
          c.add(new RTCell(this,ant-1,bat,cat+1,5));}}}
    cells=c.toArray(new RTCell[c.size()]);}
  
  private void initAX0CellGroups(){
    Map<Integer,List<RTCell>> groups=new Hashtable<Integer,List<RTCell>>(); 
    List<RTCell> group;
    for(RTCell cell:cells){
      if(ax0min>cell.ax0)ax0min=cell.ax0;
      if(ax0max<cell.ax0)ax0max=cell.ax0;
      group=groups.get(cell.ax0);
      if(group==null){
        group=new ArrayList<RTCell>();
        groups.put(cell.ax0,group);}
      group.add(cell);}
    ax0cellgroups=new RTCell[ax0max-ax0min+1][];
    List<RTCell> a;
    for(int i=ax0min;i<=ax0max;i++){
      a=groups.get(i);
      ax0cellgroups[i-ax0min]=a.toArray(new RTCell[a.size()]);}}
  
  private void initAX1CellGroups(){
    Map<Integer,List<RTCell>> groups=new Hashtable<Integer,List<RTCell>>(); 
    List<RTCell> group;
    for(RTCell cell:cells){
      if(ax1min>cell.ax1)ax1min=cell.ax1;
      if(ax1max<cell.ax1)ax1max=cell.ax1;
      group=groups.get(cell.ax1);
      if(group==null){
        group=new ArrayList<RTCell>();
        groups.put(cell.ax1,group);}
      group.add(cell);}
    ax1cellgroups=new RTCell[ax1max-ax1min+1][];
    List<RTCell> a;
    for(int i=ax1min;i<=ax1max;i++){
      a=groups.get(i);
      ax1cellgroups[i-ax1min]=a.toArray(new RTCell[a.size()]);}}
  
  private void initAX2CellGroups(){
    Map<Integer,List<RTCell>> groups=new Hashtable<Integer,List<RTCell>>(); 
    List<RTCell> group;
    for(RTCell cell:cells){
      if(ax2min>cell.ax2)ax2min=cell.ax2;
      if(ax2max<cell.ax2)ax2max=cell.ax2;
      group=groups.get(cell.ax2);
      if(group==null){
        group=new ArrayList<RTCell>();
        groups.put(cell.ax2,group);}
      group.add(cell);}
    ax2cellgroups=new RTCell[ax2max-ax2min+1][];
    List<RTCell> a;
    for(int i=ax2min;i<=ax2max;i++){
      a=groups.get(i);
      ax2cellgroups[i-ax2min]=a.toArray(new RTCell[a.size()]);}}
  
  private void initAX3CellGroups(){
    Map<Integer,List<RTCell>> groups=new Hashtable<Integer,List<RTCell>>(); 
    List<RTCell> group;
    for(RTCell cell:cells){
      if(ax3min>cell.ax3)ax3min=cell.ax3;
      if(ax3max<cell.ax3)ax3max=cell.ax3;
      group=groups.get(cell.ax3);
      if(group==null){
        group=new ArrayList<RTCell>();
        groups.put(cell.ax3,group);}
      group.add(cell);}
    ax3cellgroups=new RTCell[ax3max-ax3min+1][];
    List<RTCell> a;
    for(int i=ax3min;i<=ax3max;i++){
      a=groups.get(i);
      ax3cellgroups[i-ax3min]=a.toArray(new RTCell[a.size()]);}}
  
  private void initAX4CellGroups(){
    Map<Integer,List<RTCell>> groups=new Hashtable<Integer,List<RTCell>>(); 
    List<RTCell> group;
    for(RTCell cell:cells){
      if(ax4min>cell.ax4)ax4min=cell.ax4;
      if(ax4max<cell.ax4)ax4max=cell.ax4;
      group=groups.get(cell.ax4);
      if(group==null){
        group=new ArrayList<RTCell>();
        groups.put(cell.ax4,group);}
      group.add(cell);}
    ax4cellgroups=new RTCell[ax4max-ax4min+1][];
    List<RTCell> a;
    for(int i=ax4min;i<=ax4max;i++){
      a=groups.get(i);
      ax4cellgroups[i-ax4min]=a.toArray(new RTCell[a.size()]);}}
  
  private void initAX5CellGroups(){
    Map<Integer,List<RTCell>> groups=new Hashtable<Integer,List<RTCell>>(); 
    List<RTCell> group;
    for(RTCell cell:cells){
      if(ax5min>cell.ax5)ax5min=cell.ax5;
      if(ax5max<cell.ax5)ax5max=cell.ax5;
      group=groups.get(cell.ax5);
      if(group==null){
        group=new ArrayList<RTCell>();
        groups.put(cell.ax5,group);}
      group.add(cell);}
    ax5cellgroups=new RTCell[ax5max-ax5min+1][];
    List<RTCell> a;
    for(int i=ax5min;i<=ax5max;i++){
      a=groups.get(i);
      ax5cellgroups[i-ax5min]=a.toArray(new RTCell[a.size()]);}}
  
  /*
   * ################################
   * SCANNERS
   * ################################
   */
  
  private List<Scanner> scanners=new ArrayList<Scanner>();
  private Random random=new Random();
  
  //conditionally create a random scanner and stick it on the scanners list
  public void scannersCreate(){  
    if(random.nextDouble()<SCANNERCREATIONPROBABILITY&&scanners.size()<MAXSCANNERS){
      scanners.add(
        new Scanner(
          this,
          random.nextInt(RTCell.AXISCOUNT),
          random.nextBoolean(),
          random.nextBoolean()));}}
  
  //remove all finished scanners from the scanners list
  public void scannersDestroy(){
    Iterator<Scanner> i=scanners.iterator();
    Scanner s;
    while(i.hasNext()){
      s=i.next();
      if(s.finished())
        i.remove();}}
  
  //increment the location of all scanners
  public void scannersMove(){
    for(Scanner a:scanners)
      a.move();}
  
  //change the color of cells for all scanners
  public void scannersApply(){
    for(Scanner a:scanners)
      a.apply();}
  
}
