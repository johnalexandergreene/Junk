package org.fleen.junk.testLoopingTunnelFlowVFrameSequence;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import org.fleen.core.grammaticOLD.BubbleModel;
import org.fleen.core.grammaticOLD.GBubble;
import org.fleen.core.grammaticOLD.Grammar;
import org.fleen.core.grammaticOLD.Jig;
import org.fleen.forsythia.composition.NPartition;
import org.fleen.geom_2D.GD;
import org.fleen.geom_Kisrhombille.KGrid;
import org.fleen.geom_Kisrhombille.KVertex;
import org.fleen.junk.loopingTunnelFlowVFrameSequence.LoopingTunnelFlowVFrameBlock_Abstract;

/*
 * a frameblock that draws it's form from the TF0000 grammar
 */
public class LoopingTunnelFlowVFrameBlock_TF0000_Short extends LoopingTunnelFlowVFrameBlock_Abstract{
  
  /*
   * ################################
   * FIELDS
   * ################################
   */
  
  private static final long serialVersionUID=1914441161062032826L;
  
  protected static final String 
    IDHEXAGON="FC_hexagon",
    IDTRIANGLE="FC_triangle",
    IDSTAR="FC_star";

  /*
   * ################################
   * CONSTRUCTOR
   * ################################
   */
  
  LoopingTunnelFlowVFrameBlock_TF0000_Short(double smallestdetail){
    super(smallestdetail);}
  
  /*
   * ################################
   * GEOM
   * ################################
   */
  
  /*
   * we handle FC_hexagon,FC_triangle and FC_star, from the TF0000 grammar
   */
  protected double getIncircleRadius(GBubble bubble){
    double radius;
    double[][] vp=bubble.getPolygon();
    if(bubble.model.id.equals(IDSTAR)){
      radius=GD.getDistance_PointPoint(0,0,vp[0][0],vp[0][1]);
    }else{//model id == "FC_hexagon" or "FC_triangle"
      double[] mp=GD.getPoint_Mid2Points(vp[0][0],vp[0][1],vp[1][0],vp[1][1]);
      radius=GD.getDistance_PointPoint(0,0,mp[0],mp[1]);}
    return radius;}
  
  /*
   * ################################
   * INIT ROOT AND CORE
   * hex star hex
   * ################################
   */
  
  protected void initRootAndCore(){
    KGrid rg=new KGrid();
    setRootGrid(rg);
    BubbleModel rootbubblemodel=grammar.getBubbleModel(IDHEXAGON);
    KVertex 
      v0=new KVertex(-1,-1,0,4),
      v1=new KVertex(0,0,0,2);
    GBubble rootbubble=new GBubble(
      rg,
      rootbubblemodel,
      GBubble.TYPE_RAFT,
      v0,
      v1,
      true,
      new NPartition(),
      0);
    //
    JigOLD j=grammar.getJig(IDHEXAGON,IDSTAR);
    mouthbubble=j.create(rootbubble).get(0);
    j=grammar.getJig(IDSTAR,IDHEXAGON);
    mouthbubble=j.create(mouthbubble).get(0);
    mouthbubble.capBranch();}
  
  /*
   * ################################
   * GRAMMAR
   * ################################
   */
  
  private static final String GRAMMARPATH=
    "/home/john/projects/FleenCore_2.0/src/org/fleen/loopingTunnelFlowVFrameSequence/test/tunnelflowframeblock0000.grammar";
  
  protected ForsythiaGrammar getGrammar(){
    File selectedfile=new File(GRAMMARPATH);
    FileInputStream fis;
    ObjectInputStream ois;
    ForsythiaGrammar g=null;
    try{
      fis=new FileInputStream(selectedfile);
      ois=new ObjectInputStream(fis);
      g=(ForsythiaGrammar)ois.readObject();
      ois.close();
    }catch(Exception e){
      System.out.println("#^#^# EXCEPTION IN LOAD GRAMMAR #^#^#");
      e.printStackTrace();}
    return g;}

}
