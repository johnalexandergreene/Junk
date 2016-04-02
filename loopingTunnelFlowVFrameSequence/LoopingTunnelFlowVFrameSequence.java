package org.fleen.junk.loopingTunnelFlowVFrameSequence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.fleen.core.grammaticOLD.GBubble;

/*
 * It's a sequence of VFrames giving us a smooth zoom through a LoopingTunnelFlowVFrameBlock.
 * We specify the number of frames, calculate the geometry precisely and produce a SMOOTHLY LOOPING FLOW of frames.
 * We get frames via an iterator
 * A frame generator would use one of these, iterating through and processing one vframe at a time.
 * Thus a looping flow of video frames is created. 
 */
public class LoopingTunnelFlowVFrameSequence{
  
  /*
   * ################################
   * FIELDS
   * ################################
   */
  
  //our special fleen from which we glean vframes
  public LoopingTunnelFlowVFrameBlock_Abstract frameblock;
  //how many frames in this sequence
  public int framecount;
  
  public double
    //the lower limit on bubble detail size
    //we generally use the same floor as that in the block, but not necessarily
    detailfloor,
    //outerradius is the root radius of the frameblock
    //a viewport would be this radius.
    outerradius,
    //inner radius is the mouth radius of the frame block
    innerradius,
    //the thickness of the donut
    //that is, outerradius-innerradius
    span,
    //when the viewport radius is eqal to the outerradius then scale is 1.0
    //as we zoom our viewport shrinks and our scale increases.
    //we hit max scale when the viewport radius is the same as the inner radius.
    //at that point the scale is outerradius/innerradius
    minscale=1.0,
    maxscale,
    zoomincrement;

  /*
   * ################################
   * CONSTRUCTOR
   * specify all the params for our sequence here
   * ################################
   */
  
  public LoopingTunnelFlowVFrameSequence(
    LoopingTunnelFlowVFrameBlock_Abstract frameblock,
    int framecount,
    double detailfloor){
    this.frameblock=frameblock;
    this.framecount=framecount;
    this.detailfloor=detailfloor;
    outerradius=frameblock.rootradius;
    innerradius=frameblock.mouthradius;
    span=outerradius-innerradius;
    maxscale=outerradius/innerradius;
    zoomincrement=span/((double)framecount);}
  
  /*
   * ################################
   * ITERATOR
   * This is what we use to get the vframes
   * Returns 1 total sequence, 1 frame at a time
   * ################################
   */
  
  public Iterator<VFrame> getVFrameIterator(){
    return new VFrameIterator();}

  private class VFrameIterator implements Iterator<VFrame>{

    int index=0;
    
    public boolean hasNext(){
      return index<framecount;}

    public VFrame next(){
      VFrame f=getFrame(index);
      index++;
      return f;}

    //not implemented
    public void remove(){}}
  
  /*
   * ################################
   * CREATE FRAME
   * ################################
   */
  
  private VFrame getFrame(int index){
    System.out.println("GETTING FRAME #"+index);
    double depth=((double)index)*zoomincrement;
    double scale=getScaleAtDepth(depth);
    GBubble corebubble=getEnclosingCoreBubbleAtScale(frameblock,scale);
    VFrame f=getFrame(index,frameblock,corebubble,depth,detailfloor);
    return f;}
  
  /*
   * get the scale at the specified viewradius.
   * viewradius max is outermostradius
   * viewradius valid range is [maxradius,0). Note that we exclude 0. Scale is infinite there.
   * the scale magnifies the bubble geometry
   * ???
   */
  private double getScaleAtDepth(double depth){
  double normalizeddepth=depth/span;
  double scale=Math.pow(maxscale,normalizeddepth);
  return scale;}
  
  /*
   * get the core bubble at that scale
   * the core bubble at that scale is the core bubble for which the scaled inradius of the bubble is greater than the 
   * outermostradius of this sequence and the scaled inradius of the next core bubble is lesser than the outermostradius
   */
  private GBubble getEnclosingCoreBubbleAtScale(
    LoopingTunnelFlowVFrameBlock_Abstract frameblock,double scale){
    GBubble b0=frameblock.rootbubble;
    VFrameCoreBubbleData bd0,bd1;
    boolean finished=false;
    while(!finished){
      bd0=(VFrameCoreBubbleData)b0.data;
      bd1=(VFrameCoreBubbleData)bd0.nextframecorebubble.data;
      if(bd0.inradius*scale>=outerradius&&bd1.inradius*scale<outerradius){
        finished=true;
      }else{
        b0=bd0.nextframecorebubble;}}
    return b0;}
  
  /*
   * get all the leaf bubbles rooted at the specified corebubble (except for the next corebubble)
   * if corebubble.nextcorebubble.inradius > smallestframedetail then get the bubbles for that core bubble too
   * keep doing that until nextcorebubble.inradius is less than smallestframedetail.
   */
  private VFrame getFrame(
    int frameindex,
    LoopingTunnelFlowVFrameBlock_Abstract frameblock,
    GBubble enclosingcorebubble,double viewradius,double smallestframedetail){
    double scale=getScaleAtDepth(viewradius);
    //gather visible core bubbles
    GBubble b=enclosingcorebubble,bprior=null;
    List<GBubble> visiblecorebubbles=new ArrayList<GBubble>();
    List<Double> visiblecorebubblescalefactors=new ArrayList<Double>();
    double scalefactor=1.0;
    while(b!=null){
      visiblecorebubbles.add(b);
      visiblecorebubblescalefactors.add(scalefactor);
      bprior=b;
      b=((VFrameCoreBubbleData)b.data).nextframecorebubble;
      if(bprior==frameblock.mouthbubble)scalefactor/=maxscale;
      if(!isVisibleCoreBubble(b,scale*scalefactor,smallestframedetail))b=null;}
    //gather scaled core vbubbles
    int visiblecorebubblecount=visiblecorebubbles.size();
    double bscale;
    ArrayList<VFrameCoreBubble> vframecorebubbles=new ArrayList<VFrameCoreBubble>();
    for(int i=0;i<visiblecorebubblecount;i++){
      b=visiblecorebubbles.get(i);
      bscale=visiblecorebubblescalefactors.get(i);
      vframecorebubbles.add(new VFrameCoreBubble(getScaledVertexPoints(b,scale*bscale),((VFrameCoreBubbleData)b.data).index));}
    //gather scaled vbubbles from the local branch of each visible core bubble
    //we skip the next core bubble by skipping the index0 child bubble
    //TODO what about the mouth core bubble? What happens there?
    //it doesn't have children I think.
    ArrayList<VFrameBubble> vframebubbles=new ArrayList<VFrameBubble>();
    List<GBubble> childbubbles;
    int s,coreindex;
    for(int j=0;j<visiblecorebubblecount;j++){
      b=visiblecorebubbles.get(j);
      bscale=visiblecorebubblescalefactors.get(j);
      //if this core bubble has children
      if(b.childgrid!=null){
        coreindex=((VFrameCoreBubbleData)b.data).index;
        childbubbles=b.getChildBubbles();
        s=childbubbles.size();
        if(s>1){
          for(int i=1;i<s;i++){
            vframebubbles.addAll(
              getLocalBranchFBubbles(childbubbles.get(i),scale*bscale,smallestframedetail,coreindex));}}}}
    //trim arraylists
    vframecorebubbles.trimToSize();
    vframebubbles.trimToSize();
    //
    return new VFrame(frameindex,vframebubbles,vframecorebubbles);}
  
  /*
   * given a core bubble
   * get all the bubbles in the local branch
   * for every bubble that's big enough, make a VFrameBubble from it 
   * TODO I think that every time we get branch bubbles we're getting the whole branch including
   * the next corebubble and it's branch, which is wrong
   * no, we handle that above. This isn't a core bubble, it's the immediate child of a core bubble
   */
  private List<VFrameBubble> getLocalBranchFBubbles(
    GBubble bubble,double scale,double smallestframedetail,int coreindex){
    List<GBubble> branchbubbles=bubble.getBranchBubbles();
    List<VFrameBubble> fbubbles=new ArrayList<VFrameBubble>();
    for(GBubble b:branchbubbles)
      if(b.getDetailSize()*scale>smallestframedetail)
        fbubbles.add(new VFrameBubble(getScaledVertexPoints(b,scale),coreindex));
    return fbubbles;}
  
  private boolean isVisibleCoreBubble(GBubble bubble,double scale,double smallestframedetail){
    double r=((VFrameCoreBubbleData)bubble.data).inradius;
    boolean v=scale*r>smallestframedetail;
    return v;}
  
  private double[][] getScaledVertexPoints(GBubble bubble,double scale){
    double[][] p=bubble.getPolygon();
    double[][] sp=new double[p.length][2];
    for(int i=0;i<p.length;i++)
      sp[i]=new double[]{p[i][0]*scale,p[i][1]*scale};
    return sp;}
  
  /*
   * ################################
   * OBJECT
   * ################################
   */
  
  public String toString(){
    String a="["+framecount+"]";
    return a;}

}
