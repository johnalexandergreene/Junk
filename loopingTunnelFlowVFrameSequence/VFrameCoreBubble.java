package org.fleen.junk.loopingTunnelFlowVFrameSequence;

import java.io.Serializable;

public class VFrameCoreBubble implements Serializable{
  
  private static final long serialVersionUID=1879903507802477681L;
  
  public double[][] polygon;
  //the index of this VFrameCoreBubble in the sequence of VFrameCoreBubbles comprising the core of
  //a LoopingTunnelFlowVFrameBlock. Root is 0.
  public int index;
  
  VFrameCoreBubble(double[][] polygon,int index){
    this.polygon=polygon;
    this.index=index;}

}
