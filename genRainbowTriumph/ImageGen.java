package org.fleen.junk.genRainbowTriumph;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.fleen.junk.genRainbowTriumph.palette.Palette_Abstract;

public class ImageGen{
  
  private int 
    imagewidth,
    imageheight,
    blockdensity,
    maincycleperiod,
    fadeincycleperiod,
    fadeincycles;
  private Palette_Abstract palette;
  double scannercreationprobability;
  int maxscanners;
  private ImageGenListener listener;
  
  public void init(
    int imagewidth,
    int imageheight,
    int blockdensity,
    int maincycleperiod,
    int fadeincycleperiod,
    int fadeincycles,
    Palette_Abstract palette,
    double scannercreationprobability,
    int maxscanners,
    ImageGenListener listener){
    this.imagewidth=imagewidth;
    this.imageheight=imageheight;
    this.blockdensity=blockdensity;
    this.maincycleperiod=maincycleperiod;
    this.fadeincycleperiod=fadeincycleperiod;
    this.fadeincycles=fadeincycles;
    this.palette=palette;
    this.scannercreationprobability=scannercreationprobability;
    this.maxscanners=maxscanners;
    this.listener=listener;}
  
  /*
   * ################################
   * CYCLE CONTROL
   * ################################
   */
  
  private boolean run=true;
  
  public void start(){
    run=true;
    try{
      initBlock();
      initImage();
      while(run){
        Thread.sleep(maincycleperiod);
        block.scannersCreate();
        for(int i=0;i<fadeincycles;i++){
          Thread.sleep(fadeincycleperiod);
          block.scannersApply();
          render();
          listener.notify(image);}
        block.scannersMove();
        block.scannersDestroy();}
    //on exception we dump the stack and restart 
    }catch(Throwable e){
      System.out.println("X-X-X IMAGE GENERATOR CRASHED X-X-X");
      e.printStackTrace();
      System.out.println("O-O-O RESTARTING IMAGE GENERATOR O-O-O");
      start();}}
  
  //TODO what else?
  public void stop(){
    run=false;}
  
  /*
   * ################################
   * CELL BLOCK
   * ################################
   */
  
  private RTCellBlock block; 
  private int blockwidth,blockheight;
  
  /*
   * TODO
   * note the +1 on the dimensions there
   * with the +1 we fill the image but we overlap a bit, without the +1 we get a gap. 
   * Which is prettier?
   */
  private void initBlock(){
    double diw=(double)imagewidth,dih=(double)imageheight;
    //glean block dimensions and block image scale
    if(diw<dih){
      blockwidth=blockdensity; 
      blockheight=(int)((dih/diw)*(RTCellBlock.UR6CWIDTH/RTCellBlock.UR6CHEIGHT)*blockwidth)+1;
      blockscale=diw/(blockwidth*RTCellBlock.UR6CWIDTH);
    }else{
      blockheight=blockdensity;
      blockwidth=(int)((diw/dih)*(RTCellBlock.UR6CHEIGHT/RTCellBlock.UR6CWIDTH)*blockheight)+1;
      blockscale=dih/(blockheight*RTCellBlock.UR6CHEIGHT);}
    //create the block
    block=new RTCellBlock(
      blockwidth,
      blockheight,
      blockscale,
      palette,
      scannercreationprobability,
      maxscanners);}
  
  /*
   * ################################
   * IMAGE
   * ################################
   */
  
  private BufferedImage image;
  private Graphics2D imagegraphics;
  private double blockscale;
  
  private void initImage(){
    image=new BufferedImage(imagewidth,imageheight,BufferedImage.TYPE_INT_RGB);
    imagegraphics=image.createGraphics();
    imagegraphics.setColor(palette.getColor(0));
    imagegraphics.fillRect(0,0,imagewidth,imageheight);}
  
  private void render(){
    for(RTCell c:block.cells)
      c.render(imagegraphics);}

}
