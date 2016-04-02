package org.fleen.junk.testLoopingTunnelFlowVFrameSequence;

import org.fleen.junk.loopingTunnelFlowVFrameSequence.LoopingTunnelFlowVFrameBlock_Abstract;
import org.fleen.junk.loopingTunnelFlowVFrameSequence.LoopingTunnelFlowVFrameSequence;


public class LTFVFS_Long extends LoopingTunnelFlowVFrameSequence{

  private static final long serialVersionUID=5333667599194053007L;

  private static final double SMALLESTDETAIL=0.05;
  private static final int FRAMECOUNT=500;
  
  public LTFVFS_Long(){
    super(getBlock(),FRAMECOUNT,SMALLESTDETAIL);}
  
  private static LoopingTunnelFlowVFrameBlock_Abstract getBlock(){
    return new LoopingTunnelFlowVFrameBlock_TF0000_Long(SMALLESTDETAIL);}


}
