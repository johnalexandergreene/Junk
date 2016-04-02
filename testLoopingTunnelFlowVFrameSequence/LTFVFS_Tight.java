package org.fleen.junk.testLoopingTunnelFlowVFrameSequence;

import org.fleen.junk.loopingTunnelFlowVFrameSequence.LoopingTunnelFlowVFrameBlock_Abstract;
import org.fleen.junk.loopingTunnelFlowVFrameSequence.LoopingTunnelFlowVFrameSequence;


public class LTFVFS_Tight extends LoopingTunnelFlowVFrameSequence{

  private static final double SMALLESTDETAIL=0.09;
  private static final int FRAMECOUNT=300;
  
  public LTFVFS_Tight(){
    super(getBlock(),FRAMECOUNT,SMALLESTDETAIL);}
  
  private static LoopingTunnelFlowVFrameBlock_Abstract getBlock(){
    return new LoopingTunnelFlowVFrameBlock_TF0000_Tight(SMALLESTDETAIL);}


}
