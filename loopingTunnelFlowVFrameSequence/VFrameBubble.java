package org.fleen.junk.loopingTunnelFlowVFrameSequence;

import java.io.Serializable;

public class VFrameBubble implements Serializable{
  
  private static final long serialVersionUID=-7606448221705031507L;
  
  public double[][] polygon;
  //the index of the core bubble ancestor of this VFrameBubble's source Bubble
  public int coreindex;
  
  VFrameBubble(double[][] polygon,int coreindex){
    this.polygon=polygon;
    this.coreindex=coreindex;}

}
