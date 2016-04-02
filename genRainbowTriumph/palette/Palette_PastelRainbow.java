package org.fleen.junk.genRainbowTriumph.palette;

import java.awt.Color;

/*
 * a full pastel rainbow
 * hue is scaled, saturation is constant, brightness is constant
 */
public class Palette_PastelRainbow extends Palette_Abstract{

  private static final int COLORCOUNT=96;
  static final float 
    SATURATION=0.3f,
    BRIGHTNESS=1.0f;
  
  private Color[] colors;
  
  public Palette_PastelRainbow(){
    colors=new Color[COLORCOUNT];
    float h;
    for(int i=0;i<COLORCOUNT;i++){
      h=((float)i)/((float)COLORCOUNT);
      colors[i]=Color.getHSBColor(h,SATURATION,BRIGHTNESS);}}
  
  protected Color[] getColors(){
    return colors;}

}
