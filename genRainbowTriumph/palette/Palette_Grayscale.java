package org.fleen.junk.genRainbowTriumph.palette;

import java.awt.Color;

/*
 * grayscale gradient
 * colors go like 0 1 2 3 4 5 4 3 2 1 for smooth max:0 transition
 */
public class Palette_Grayscale extends Palette_Abstract{
  
  private static final int SHADESOFGREYCOUNT=96;
  
  private static final float 
    BRIGHTNNESSMIN=0.3f,
    BRIGHTNNESSMAX=0.7f;
  
  private Color[] colors;
  
  public Palette_Grayscale(){
    int colorcount=SHADESOFGREYCOUNT*2-2;
    colors=new Color[colorcount];
    float b;
    float r=BRIGHTNNESSMAX-BRIGHTNNESSMIN;
    for(int i=0;i<SHADESOFGREYCOUNT;i++){
      b=((float)i)/((float)SHADESOFGREYCOUNT)*r+BRIGHTNNESSMIN;
      colors[i]=Color.getHSBColor(0,0,b);}
    for(int i=SHADESOFGREYCOUNT;i<colorcount;i++)
      colors[i]=colors[colorcount-i];}
  
  protected Color[] getColors(){
    return colors;}

}
