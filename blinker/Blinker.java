package org.fleen.junk.blinker;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Set;

import org.fleen.forsythia.composition.FGrid;
import org.fleen.forsythia.composition.FPolygon;
import org.fleen.forsythia.composition.ForsythiaComposition;
import org.fleen.forsythia.grammar.ForsythiaGrammar;
import org.fleen.forsythia.util.simpleComposer.FSC_Basic;
import org.fleen.forsythia.util.simpleRenderer.FSR_EggLevelSplitPaletteWithStrokes;
import org.fleen.forsythia.util.simpleRenderer.ForsythiaSimpleRenderer;

/*
 * This is a forsythia composition based animation generator
 * 
 * for each polygon in the composition (just leaves? Do this with azalea?) create a list of colors : colorlist
 * for each tick of the generation clock do a colorlist-rotating process for each polyogon
 * different frequencies, different patters. Could even do cell stuff.
 * at each tick, render the system. Collect frames.
 * 
 * 
 * 
 * 
 * ---------------
 * generate forsythia composition
 * colorize. 
 *   store colors in map
 * 1) render to file
 * prune leaves and maybe their immediate ancestors
 * rebuild
 * colorize
 * goto 1
 * keep doing that till we have 1000 images or so
 * 
 *  
 * 
 * 
 */
public class Blinker{
  
  FSC_Basic composer=new FSC_Basic(0.02,1.0);
  
  private static final int FRAMECOUNT=100;
  
  ForsythiaComposition composition;
  
  public void createFrames(){
    initComposition();
    for(int i=0;i<FRAMECOUNT;i++){
      exportFrame(i);
      prune();
      prune();
      rebuild();
      }}
  
  /*
   * ################################
   * PRUNE
   * ################################
   */
  
  private void prune(){
    //get leaf grids
    Set<FGrid> leafgrids=new HashSet<FGrid>();
    for(FPolygon p:composition.getLeafPolygons())
      leafgrids.add((FGrid)p.getParent());
    //remove them
    for(FGrid g:leafgrids)
      g.getParent().clearChildren();}
  
  /*
   * ################################
   * REBUILD
   * ################################
   */
  
  private void rebuild(){
    composer.compose(composition);}
  
  /*
   * ################################
   * RENDER AND EXPORT
   * ################################
   */
  
  private static final Color BACKGROUNDCOLOR=new Color(255,255,255);
  private static final float STROKEWIDTH=0.001f;
  private static final Color STROKECOLOR=new Color(0,0,0);
  
  //private static Color[] COLOR0={
  //new Color(254,67,101),
  //new Color(252,157,154)};
  //private static Color[] COLOR1={
  //new Color(249,205,173),
  //new Color(200,200,169),
  //new Color(131,175,155)};
  
  //private static Color[] COLOR0={
  //new Color(78,205,196),
  //new Color(199,244,100)};
  //private static Color[] COLOR1={
  //new Color(255,107,107),
  //new Color(196,77,88)};
  
  //private static Color[] COLOR0={
  //new Color(189,21,80),
  //new Color(233,127,2)};
  //private static Color[] COLOR1={
  //new Color(248,202,0),
  //new Color(138,155,15)};
  
  //greeny blue yellow
  //private static Color[] COLOR0={
  //new Color(94,140,106),
  //new Color(136,166,94)};
  //private static Color[] COLOR1={
  //new Color(191,179,90),
  //new Color(242,196,90)};
  
  ////carnival or something
  //private static Color[] COLOR0={
  //new Color(251,215,21),
  //new Color(255,49,131)};
  //private static Color[] COLOR1={
  //new Color(81,211,247),
  //new Color(255,255,255)};
  
  ////thought provoking
  //private static Color[] COLOR0={
  //new Color(236,208,120),
  //new Color(83,119,122)};
  //private static Color[] COLOR1={
  //new Color(217,91,67),
  //new Color(192,41,66)};
  
  ////coup de grâce
  //private static Color[] COLOR0={
  //new Color(153,184,152),
  //new Color(254,206,168)};
  //private static Color[] COLOR1={
  //new Color(255,132,124),
  //new Color(232,74,95)};
  
  //private static Color[] COLOR0={
  //new Color(0,0,0)};
  //private static Color[] COLOR1={
  //new Color(255,255,255)};
  
  //mystery machine
  private static Color[] COLOR0={
  new Color(247,120,37),
  new Color(241,239,165)};
  private static Color[] COLOR1={
  new Color(211,206,61),
  new Color(96,185,154)};
  
  ForsythiaSimpleRenderer renderer=new FSR_EggLevelSplitPaletteWithStrokes(BACKGROUNDCOLOR,0,COLOR0,COLOR1,STROKECOLOR,STROKEWIDTH);
  
  private void exportFrame(int index){
    BufferedImage image=renderer.getImage(600,800,composition);
    Exporter.export(image,index);
  }
  
  /*
   * ################################
   * INIT COMPOSITION
   * ################################
   */
  
  private void initComposition(){
    ForsythiaGrammar grammar=loadGrammar();
    composition=composer.compose(grammar);}
  
  /*
   * ################################
   * GET GRAMMAR
   * #####################
   */
  
  private static final String GRAMMARPATH="/home/john/projects/code/Fleen/grammars/2016_02_02/g003_hexroot_simple";
  
  private static final ForsythiaGrammar loadGrammar(){
    File file=new File(GRAMMARPATH);
    FileInputStream fis;
    ObjectInputStream ois;
    ForsythiaGrammar fg=null;
    try{
      fis=new FileInputStream(file);
      ois=new ObjectInputStream(fis);
      fg=(ForsythiaGrammar)ois.readObject();
      ois.close();
    }catch(Exception e){
      System.out.println("#^#^# EXCEPTION IN EXTRACT GRAMMAR FROM FILE FOR IMPORT #^#^#");
      e.printStackTrace();
      return null;}
    return fg;}
  
  /*
   * ################################
   * TEST
   * ################################
   */
  
  public static final void main(String[] a){
    Blinker blinker=new Blinker();
    blinker.createFrames();
  }
  
  

}
