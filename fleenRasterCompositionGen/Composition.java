package org.fleen.junk.fleenRasterCompositionGen;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.fleen.core.grammaticOLD.BubbleModel;
import org.fleen.core.grammaticOLD.BubbleSignature;
import org.fleen.core.grammaticOLD.GBubble;
import org.fleen.core.grammaticOLD.Grammar;
import org.fleen.core.grammaticOLD.Jig;
import org.fleen.forsythia.tree.PGFTree;
import org.fleen.geom_Kisrhombille.KGrid;
import org.fleen.junk.fleenRasterCompositionGen.symmetryController.SymmetryControlFunction_Abstract;

public class Composition extends PGFTree{
  
  double detaillimit;
  SymmetryControlFunction_Abstract scf;
  ForsythiaGrammar grammar;
  
  public Composition(){
    detaillimit=FRCG.instance.config.getDetailSizeLimit();
    scf=FRCG.instance.config.getSymmetryControlFunction();
    grammar=FRCG.instance.config.getGrammar();
    build();}

  private void build(){
    KGrid grid=new KGrid();
    setRootGrid(grid);
    BubbleModel rootbubblemodel=FRCG.instance.config.getRootBubbleModel();
    GBubble rootbubble=new GBubble(
      grid,
      rootbubblemodel);
    scf.mindetailsize=detaillimit;
    scf.maxdetailsize=rootbubble.getDetailSize();
    boolean cultivationhappened=true;
    while(cultivationhappened){
      Log.m1("[cultivating]");
      cultivationhappened=cultivate();}}
  
  //DETAIL SIZE LIMITED CHORUSSED RANDOM JIG SELECTION CULTIVATION CYCLE
  //returns true if cultivation happened
  private boolean cultivate(){
    boolean cultivationhappened=false;
    JigOLD j;
    Random random=new Random();
    Map<FPolygonSignature,JigOLD> sigjigs=new Hashtable<FPolygonSignature,JigOLD>();
    int bcount=0;
    for(GBubble bubble:getLeaves()){
      //progress feed
      bcount++;
      if(bcount%4096==0)
        Log.m1(".");
      //
      if(bubble.getDetailSize()>detaillimit){
        j=getJig(bubble,grammar,sigjigs,random);
        if(j!=null){
          cultivationhappened=true;
          j.create(bubble);}}}
    return cultivationhappened;}

  private JigOLD getJig(GBubble bubble,ForsythiaGrammar grammar,Map<FPolygonSignature,JigOLD> sigjigs,Random random){
    JigOLD jig=null;
    //if the symmetry control function says to do symmetry then attempt symmetry
    if(scf.doSymmetry(bubble)){
      //check the table to see if a bubble with this bubble's signature has already been addressed
      //if so then we get a copy of the jig that was picked for that foregone bubble. Thus symmetric treatment.
      jig=sigjigs.get(bubble.getSignature());}
    //if we got a jig then we're done, otherwise get one this other way
    if(jig==null){
      if(doABoiler(bubble)){
        jig=getBoilerJig(bubble,grammar,random);
        if(jig==null)jig=getSplitterJig(bubble,grammar,random);
      }else{
        jig=getSplitterJig(bubble,grammar,random);
        if(jig==null)jig=getBoilerJig(bubble,grammar,random);}
      if(jig==null)return null;
      sigjigs.put(bubble.getSignature(),jig);}
    return jig;}
  
  //our little logic for determining whether to do a boiler or a splitter. This could be a param of course.
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
