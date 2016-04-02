package org.fleen.junk.loopingTunnelFlowVFrameSequence;

import org.fleen.core.grammaticOLD.GBubble;

public class VFrameCoreBubbleData{
  
  GBubble nextframecorebubble;
  double inradius;
  int index;
  
  VFrameCoreBubbleData(GBubble nextframecorebubble,double inradius,int index){
    this.nextframecorebubble=nextframecorebubble;
    this.inradius=inradius;
    this.index=index;
  }
  
}
