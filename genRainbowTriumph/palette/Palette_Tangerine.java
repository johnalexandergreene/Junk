package org.fleen.junk.genRainbowTriumph.palette;

import java.awt.Color;

/*
 * grayscale gradient
 * colors go like 0 1 2 3 4 5 4 3 2 1 for smooth max:0 transition
 */
public class Palette_Tangerine extends Palette_Abstract{
  
  private static final int SHADESCOUNT=66;
  
  private static final float
    HUE=0.13f,
    SATURATION=0.8f,
    BRIGHTNNESSMIN=0.7f,
    BRIGHTNNESSMAX=1.0f;
  
  private Color[] colors;
  
  public Palette_Tangerine(){
    int colorcount=SHADESCOUNT*2-2;
    colors=new Color[colorcount];
    float b;
    float r=BRIGHTNNESSMAX-BRIGHTNNESSMIN;
    for(int i=0;i<SHADESCOUNT;i++){
      b=((float)i)/((float)SHADESCOUNT)*r+BRIGHTNNESSMIN;
      colors[i]=Color.getHSBColor(HUE,SATURATION,b);}
    for(int i=SHADESCOUNT;i<colorcount;i++)
      colors[i]=colors[colorcount-i];}
  
  protected Color[] getColors(){
    return colors;}

}
