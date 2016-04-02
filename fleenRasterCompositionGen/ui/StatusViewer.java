package org.fleen.junk.fleenRasterCompositionGen.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

/*
 * it has it's own thread
 * we have a linked list of characters. Displaystring is a substring of that list.
 * at intervals ( quarter seconds) we increment the beginning-of-viewstring pointer
 * and then copy the indicated string to the text display
 */
@SuppressWarnings("serial")
public class StatusViewer extends JPanel{
  
  /*
   * ################################
   * CONSTRUCTOR
   * ################################
   */
  
  StatusViewer(){
    super();
    setBackground(COLOR_DEFAULT_BACKGROUND);
    setForeground(COLOR_DEFAULT_FOREGROUND);
    addHierarchyBoundsListener(
      new HierarchyBoundsListener(){
        public void ancestorMoved(HierarchyEvent e){}
        public void ancestorResized(HierarchyEvent e){
          metricsvalid=false;}});}
  
  /*
   * ################################
   * METRICS
   * ################################
   */
  
  private boolean metricsvalid=false;
  private Font font=null;
  private int fontwidth,fontheight;
  private static final int FONT_PADDING=3;
  private int textwidth;
  
  private void validateMetrics(){
    if(!metricsvalid){
      fontheight=getHeight()-FONT_PADDING*2;
      font=new Font("DejaVu Sans Mono", Font.PLAIN,getHeight()-FONT_PADDING*2);
      fontwidth=getFontMetrics(font).getMaxAdvance();
      textwidth=getWidth()/(fontwidth+FONT_PADDING);
      metricsvalid=true;}}
  
  /*
   * ################################
   * TICK
   * ################################
   */
  
  private static final ScheduledExecutorService sched=Executors.newSingleThreadScheduledExecutor();
  
  private static final long 
    INITIAL_DELAY=1000,
    PERIODIC_DELAY=30;
  
  public void start(){
    sched.scheduleWithFixedDelay(
      new Tick(),
      INITIAL_DELAY,
      PERIODIC_DELAY, 
      TimeUnit.MILLISECONDS);}
  
  public void stop(){
    sched.shutdown();}
  
  private class Tick extends Thread{
    public void run(){
      if(textstartindex>0){
        textstartindex--;
        repaint();}}}
  
  /*
   * ################################
   * CHARACTERS
   * ################################
   */
  
  LinkedList<SVChar> characters=new LinkedList<SVChar>();
  int textstartindex=0;
  private static final double TRIMLIMIT=0.2;
  
  /*
   * string is any string
   * significance is an integer in range [0,1,2]
   */
  public void report(String s,int significance){
    s=" "+s;
    textstartindex+=s.length();
    Color color=COLORS[significance];
    char[] a=s.toCharArray();
    for(int i=a.length-1;i>-1;i--)
      characters.addFirst(new SVChar(a[i],color));
    //trim characters list when it gets too big
    int z=characters.size();
    int trimlimit=(int)(z*TRIMLIMIT);
    if(textstartindex+textwidth<trimlimit)
      characters=new LinkedList<SVChar>(characters.subList(0,trimlimit));}
  
  class SVChar{
    
    char character;
    Color color;
    
    SVChar(char c,Color a){
      character=c;
      color=a;}}
  
  /*
   * ################################
   * IMAGE
   * ################################
   */
  
  private static final Color 
  COLOR_DEFAULT_BACKGROUND=Color.black,
  COLOR_DEFAULT_FOREGROUND=Color.magenta;

  private static final Color[] COLORS={
    Color.green,
    Color.orange,
    Color.red};

  public void paint(Graphics g){
    BufferedImage i=getStatusImage();
    if(i!=null)
      g.drawImage(getStatusImage(),0,0,null);}
  
  private char[] chararray=new char[1];
  
  private BufferedImage getStatusImage(){
    if(getWidth()<1)return null;
    BufferedImage image=new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
    Graphics g=image.getGraphics();
    validateMetrics();
    g.setFont(font);
    g.setColor(COLOR_DEFAULT_BACKGROUND);
    g.fillRect(0,0,getWidth(),getHeight());
    SVChar svchar;
    int actualindex;
    for(int j=0;j<textwidth;j++){
      actualindex=j+textstartindex;
      if(actualindex>=characters.size())break;
      svchar=characters.get(actualindex);
      chararray[0]=svchar.character;
      g.setColor(svchar.color);
      g.drawChars(chararray,0,1,j*(fontwidth+FONT_PADDING),fontheight);}
    return image;}
  
}
