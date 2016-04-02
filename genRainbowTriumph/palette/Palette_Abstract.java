package org.fleen.junk.genRainbowTriumph.palette;

import java.awt.Color;

public abstract class Palette_Abstract{
  
  public int getColorCount(){
    return getColors().length;}
  
  public Color getColor(int c){
    return getColors()[c];}
  
  protected abstract Color[] getColors();
  
}
