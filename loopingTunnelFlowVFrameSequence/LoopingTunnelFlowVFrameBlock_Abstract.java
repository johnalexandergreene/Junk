package org.fleen.junk.loopingTunnelFlowVFrameSequence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.fleen.core.grammaticOLD.BubbleSignature;
import org.fleen.core.grammaticOLD.GBubble;
import org.fleen.core.grammaticOLD.Grammar;
import org.fleen.core.grammaticOLD.Jig;
import org.fleen.forsythia.tree.PGFTree;
import org.fleen.geom_2D.GD;
import org.fleen.geom_Kisrhombille.KGrid;

/*
 * A LoopingTunnelFlowVFrameBlock is a special kind of fleen designed to give us nice tunnel flow frames
 * It consists initially of a series of core bubbles arranged root,child,grandchild,ggchild,etc
 * The core bubbles are all concentraic to the origin. So we have a simple tunnel going on.
 * Then we cultivate the tree, contraining by a detail size function.
 */
public abstract class LoopingTunnelFlowVFrameBlock_Abstract extends PGFTree{
  
  /*
   * ################################
   * FIELDS
   * ################################
   */
  
  public GBubble 
    rootbubble,
    mouthbubble;
  
  public double 
    rootradius,
    mouthradius;
  
  public transient ForsythiaGrammar grammar;
  
  private static final double MINSCALE=1.0; 
  
  transient double
    //the smallest detail at the outer radius. this shrinks as we move inward, accounting for the
    //fact that we will be zooming inward towards the center.
    smallestdetail,
    maxscale,
    minscale=MINSCALE,
    radiusrange,
    smallestdetailatinnerradius,//the mouth
    smallestdetailatouterradius,//the root
    smallestdetailrange;
  
  /*
   * ################################
   * CONSTRUCTOR
   * ################################
   */
  
  public LoopingTunnelFlowVFrameBlock_Abstract(double smallestdetail){
    this.smallestdetail=smallestdetail;
    System.out.println("--- CONSTRUCTING FRAMEBLOCK");
    grammar=getGrammar();
    initRootAndCore();
    KGrid rg=getRoot();
    rootbubble=rg.childbubbles.get(0);
    mouthbubble=getLeaves().get(0);
    doCoreBubbleData();
    rootradius=getIncircle(rootbubble);
    mouthradius=getIncircle(mouthbubble);
    radiusrange=rootradius-mouthradius;
    maxscale=rootradius/mouthradius;
    smallestdetailatinnerradius=smallestdetail/maxscale;
    smallestdetailatouterradius=smallestdetail;
    smallestdetailrange=smallestdetailatouterradius-smallestdetailatinnerradius;
    cultivate();
    System.out.println(" +++ FINISHED CONSTRUCTING FRAMEBLOCK");}
  
  Comparator<GBubble> bubblecomparator=new Comparator<GBubble>(){
    public int compare(GBubble b0,GBubble b1){
      double s0=b0.getDetailSize(),s1=b1.getDetailSize();
      if(s0==s1){
        return 0;
      }else if(s0<s1){
        return -1;
      }else{
        return 1;}}};
  
  /*
   * ################################
   * GRAMMAR
   * get it however
   * ################################
   */
  
  protected abstract ForsythiaGrammar getGrammar();
  
  /*
   * ################################
   * ROOT AND CORE
   * Create root grid and a root bubble
   * then create 0..n more grids and core bubbles via special jigs
   * DON'T FORGET TO CAP THAT MOUTH BUBBLE
   * ################################
   */
  
  protected abstract void initRootAndCore();
  
  /*
   * assign FrameCoreBubbleData objects to each of our core bubbles
   * the data is next core bubble links, index
   * TODO maybe cache the incircleradius too, for speed
   */
  private void doCoreBubbleData(){
    //get the core bubbles into a list
    List<GBubble> corebubbles=new ArrayList<GBubble>();
    GBubble b=rootbubble;
    while(b!=null){
      corebubbles.add(b);
      if(b.childgrid==null){
        b=null;
      }else{
        b=b.getChildBubbles().get(0);}}
    //set data
    int s=corebubbles.size();
    int inext;
    GBubble bnext;
    for(int i=0;i<s;i++){
      b=corebubbles.get(i);
      inext=i+1;
      if(inext==s)inext=0;
      bnext=corebubbles.get(inext);
      b.data=new VFrameCoreBubbleData(bnext,getIncircle(b),i);}}
  
  public int getCoreBubbleCount(){
    GBubble b=rootbubble;
    int bc=0;
    while(b!=null){
      bc++;
      if(b.childgrid==null){
        b=null;
      }else{
        b=b.getChildBubbles().get(0);}}
    return bc;}
  
  /*
   * ################################
   * GEOMETRY
   * ################################
   */
  
  /*
   * the length of this frameblock in terms of core bubble count
   */
  public int getSize(){
    GBubble b=rootbubble;
    int s=0;
    while(b!=null){
      s++;
      if(b.childgrid==null){
        b=null;
      }else{
        b=b.getChildBubbles().get(0);}}
    return s;}
  
  /*
   * span is the thickness of the donut
   * that is, the difference between the root and mouth radii
   */
  public double getSpan(){
    return rootradius-mouthradius;}
  
  /*
   * it's easier to special case the core bubble types than to do a general alg
   */
  protected abstract double getIncircleRadius(GBubble bubble);
  
  /*
   * ################################
   * CULTIVATE
   * ################################
   */
  
  private void cultivate(){
    boolean cultivationhappened=true;
    while(cultivationhappened)
      cultivationhappened=
        doDetailFloorLimitedChorussedRandomJigSelectionCultivationCycle();}
  
  private boolean doDetailFloorLimitedChorussedRandomJigSelectionCultivationCycle(){
    System.out.println("DOING CULTIVATION CYCLE");
    boolean cultivationhappened=false;
    JigOLD j;
    Random random=new Random();
    Map<FPolygonSignature,JigOLD> sigjigs=new Hashtable<FPolygonSignature,JigOLD>();
    for(GBubble bubble:rootbubble.getBranchLeafBubbles()){
      if((!bubble.isCapped())&&
        (!bubble.equals(mouthbubble))&&
        isBigEnoughToCultivate(bubble)){
        j=getJig(bubble,grammar,sigjigs,random);
        if(j!=null){
          cultivationhappened=true;
          j.create(bubble);}}}
    return cultivationhappened;}
  
  /*
   * we use a gradient function based on distance from the inner radius.
   * smallestdetailsizeatradius=(smallestdetailsize/maxscale)+distancefrominnerradius*(scalerange/radiusrange)
   * if bubble.detailsize>=smallestdetailsizeatradius then return true. yes, cultivate it.
   * otherwise return false. nope, this bubble may not be cultivated. 
   */
  private boolean isBigEnoughToCultivate(GBubble bubble){
    double bubbledetailsize=bubble.getDetailSize();
    double[] bubblecentroid=bubble.getCentroid2D();
    double normalizedradiusatbubblecentroid=
      (GD.getDistance_PointPoint(0,0,bubblecentroid[0],bubblecentroid[1])-mouthradius)/radiusrange;
    double smallestdetailatbubblecentroid=
      smallestdetailatinnerradius+normalizedradiusatbubblecentroid*smallestdetailrange;
    boolean bigenough=bubbledetailsize>smallestdetailatbubblecentroid;
    return bigenough;}
  
  /*
   * first check the table to see if there's a jig for a bubble with this bubble's signature
   * if not then get one from the grammer.
   */
  private JigOLD getJig(GBubble bubble,ForsythiaGrammar grammar,Map<FPolygonSignature,JigOLD> sigjigs,Random random){
    JigOLD j=sigjigs.get(bubble.getSignature());
    if(j==null){
      if(doABoiler(bubble)){
        j=getBoilerJig(bubble,grammar,random);
        if(j==null)j=getSplitterJig(bubble,grammar,random);
      }else{
        j=getSplitterJig(bubble,grammar,random);
        if(j==null)j=getBoilerJig(bubble,grammar,random);}
      if(j==null)return null;
      sigjigs.put(bubble.getSignature(),j);}
    return j;}
  
  private boolean doABoiler(GBubble b){
    int cl=b.getRaftLevel();
    if(cl==0){
      return false;
    }else if(cl==1){
      return new Random().nextBoolean();
    }else{//>=2
      return true;}}
  
  private JigOLD getBoilerJig(GBubble bubble,ForsythiaGrammar grammar,Random random){
    List<JigOLD> jigs=grammar.getProtoJigs(bubble.model.id);
    if(jigs.isEmpty())return null;
    Iterator<JigOLD> i=jigs.iterator();
    JigOLD j;
    while(i.hasNext()){
      j=i.next();
      if(j.type==JigOLD.TYPE_SPLITTER)i.remove();}
    if(jigs.isEmpty())return null;
    j=jigs.get(random.nextInt(jigs.size()));
    return j;}
  
  private JigOLD getSplitterJig(GBubble bubble,ForsythiaGrammar grammar,Random random){
    List<JigOLD> jigs=grammar.getProtoJigs(bubble.model.id);
    if(jigs.isEmpty())return null;
    Iterator<JigOLD> i=jigs.iterator();
    JigOLD j;
    while(i.hasNext()){
      j=i.next();
      if(j.type==JigOLD.TYPE_BOILER)i.remove();}
    if(jigs.isEmpty())return null;
    j=jigs.get(random.nextInt(jigs.size()));
    return j;}

}
