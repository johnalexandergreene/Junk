package org.fleen.junk.genCurtainFlow;

import org.fleen.forsythia.tree.PGFTree;

/*
 * Cultivate up a nice diamond composition with a root bubble that uses one of our base stripe bubblemodels.
 * Get leaf bubbles. from that derive a list of pixels
 *   use line draw, rect draw.
 *   respect foam levels for color control
 * that list of Pixels is our graphical stuff 
 * 
 */
public abstract class Stripe_Abstract{
  
  int xoff,yoff;
  Pixel[] pixels;
  
  Stripe_Abstract(){
    PGFTree dgc=getDGComposition();
    initPixels(dgc);}
  
  //grid origin at 0,0
  //scaled by fish spec
  //assume the y is flipped
  protected abstract PGFTree getDGComposition();
  
  protected abstract int getLength();
  
  /*
   * get leaf bubbles
   * get all segs in leaf bubbles
   * convert segs into pixels
   * TODO 
   * cull redundant segs? Maybe not worth it.
   * gotta take scroll direction into account. this is prolly grid foreward.
   * 
   */
  private void initPixels(PGFTree dgc){
    
  }
  
  class Pixel{
    int x,y,c;
  }

}
