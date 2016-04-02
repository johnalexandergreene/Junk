package org.fleen.junk.fleenRasterCompositionGen.symmetryController;

import org.fleen.core.grammaticOLD.GBubble;

public class SC_BigRandomSmallSymmetry extends SymmetryControlFunction_Abstract{

  private static final long serialVersionUID=1815396810486187364L;

  private double median=-1;
  
  public boolean doSymmetry(GBubble bubble){
    if(median==-1)initMedian();
    double s=bubble.getDetailSize();
    return s<median;}
  
  private void initMedian(){
    median=(maxdetailsize+mindetailsize)/2.0;}


}
