package org.fleen.junk.genRainbowTriumph;

class Scanner{
  
  private RTCellBlock block;
  private int axis;
  private boolean movepolarity;
  private boolean colorchangepolarity;
  private int location;
  
  Scanner(RTCellBlock block,int axis,boolean movepolarity,boolean colorchangepolarity){
    this.block=block;
    this.axis=axis;
    this.movepolarity=movepolarity;
    this.colorchangepolarity=colorchangepolarity;
    switch(axis){
    case RTCell.AX0:
      location=(movepolarity)?block.ax0min:block.ax0max;
      return;
    case RTCell.AX1:
      location=(movepolarity)?block.ax1min:block.ax1max;
      return;
    case RTCell.AX2:
      location=(movepolarity)?block.ax2min:block.ax2max;
      return;
    case RTCell.AX3:
      location=(movepolarity)?block.ax3min:block.ax3max;
      return;
    case RTCell.AX4:
      location=(movepolarity)?block.ax4min:block.ax4max;
      return;
    case RTCell.AX5:
      location=(movepolarity)?block.ax5min:block.ax5max;
      return;}}
  
  void move(){
    if(movepolarity){
      location++;
    }else{
      location--;}}
  
  boolean finished(){
    switch(axis){
    case RTCell.AX0:
      return movepolarity?location>block.ax0max:location<block.ax0min;
    case RTCell.AX1:
      return movepolarity?location>block.ax1max:location<block.ax1min;
    case RTCell.AX2:
      return movepolarity?location>block.ax2max:location<block.ax2min;
    case RTCell.AX3:
      return movepolarity?location>block.ax3max:location<block.ax3min;
    case RTCell.AX4:
      return movepolarity?location>block.ax4max:location<block.ax4min;
    case RTCell.AX5:
      return movepolarity?location>block.ax5max:location<block.ax5min;
    default:
      throw new IllegalArgumentException("foo");}}
  
  void apply(){
    RTCell[] cellgroup=block.getCellGroup(axis,location);
    if(colorchangepolarity){
      for(RTCell c:cellgroup)
        c.incrementColor();
    }else{
      for(RTCell c:cellgroup)
        c.decrementColor();}}
  
}
