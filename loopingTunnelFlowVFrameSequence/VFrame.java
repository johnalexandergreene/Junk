package org.fleen.junk.loopingTunnelFlowVFrameSequence;

import java.util.List;

public class VFrame{
  
  public int index;//index of this frame in a sequence
  public List<VFrameBubble> fbubbles;
  public List<VFrameCoreBubble> fcorebubbles;
  
  public VFrame(int index,List<VFrameBubble> fbubbles,List<VFrameCoreBubble> fcorebubbles){
    this.index=index;
    this.fbubbles=fbubbles;
    this.fcorebubbles=fcorebubbles;}

}
