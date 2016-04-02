package org.fleen.junk.fleenRasterCompositionGen.ui;

import java.awt.Font;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.fleen.junk.fleenRasterCompositionGen.FRCG;
import org.fleen.junk.fleenRasterCompositionGen.Log;
import org.fleen.junk.fleenRasterCompositionGen.Log.LogBox;

@SuppressWarnings("serial")
public class LogBoxTextArea extends JTextArea implements Log.LogBox{
  
  LogBoxTextArea thislbtp;
  int trimcharslimit;
  private static final double TRIM_CHARS_FACTOR=1.5;
  
  LogBoxTextArea(){
    super();
    thislbtp=this;
    setEditable(false);
    setLineWrap(true);
    trimcharslimit=(int)(FRCG.LOGBOX_MAX_CHARS*TRIM_CHARS_FACTOR);
    setBackground(FRCG.LOGBOX_BACKGROUND);
    setForeground(FRCG.LOGBOX_FOREGROUND);
    setFont(FRCG.LOGBOX_FONT);}
  
  public void appendText(final String text) {
    SwingUtilities.invokeLater(new Runnable(){
      public void run(){
        Document doc=thislbtp.getDocument();
        try {
          doc.insertString(0,text,null);
          trim(doc);
        }catch(BadLocationException x){
          x.printStackTrace();}
        thislbtp.setCaretPosition(0);}});}
  
  private void trim(Document doc){
    int a=doc.getLength();
    if(a>=trimcharslimit){
      try{
        doc.remove(FRCG.LOGBOX_MAX_CHARS,a-FRCG.LOGBOX_MAX_CHARS);
      }catch(Exception x){
        x.printStackTrace();}}}

}
