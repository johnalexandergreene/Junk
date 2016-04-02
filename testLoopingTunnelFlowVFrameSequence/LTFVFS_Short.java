package org.fleen.junk.testLoopingTunnelFlowVFrameSequence;

import org.fleen.junk.loopingTunnelFlowVFrameSequence.LoopingTunnelFlowVFrameBlock_Abstract;
import org.fleen.junk.loopingTunnelFlowVFrameSequence.LoopingTunnelFlowVFrameSequence;


public class LTFVFS_Short extends LoopingTunnelFlowVFrameSequence{

  private static final long serialVersionUID=5333667599194053007L;

  private static final double SMALLESTDETAIL=0.1;

  private static final int FRAMECOUNT=80;
  
  public LTFVFS_Short(){
    super(getBlock(),FRAMECOUNT,SMALLESTDETAIL);}
  
  private static LoopingTunnelFlowVFrameBlock_Abstract getBlock(){
    return new LoopingTunnelFlowVFrameBlock_TF0000_Short(SMALLESTDETAIL);}


}
