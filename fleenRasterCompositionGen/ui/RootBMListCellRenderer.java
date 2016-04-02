package org.fleen.junk.fleenRasterCompositionGen.ui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import org.fleen.core.grammaticOLD.BubbleModel;
import org.fleen.junk.fleenRasterCompositionGen.FRCG;

@SuppressWarnings("serial")
class RootBMListCellRenderer extends JLabel implements ListCellRenderer{
  
  RootBMListCellRenderer(){
    setOpaque(true);
    setSize(new Dimension(FRCG.BMLIST_CELLSPAN,FRCG.BMLIST_CELLSPAN));
    setHorizontalTextPosition(SwingConstants.CENTER);
    setVerticalTextPosition(SwingConstants.BOTTOM);
    setHorizontalAlignment(SwingConstants.CENTER);
    setForeground(FRCG.BMLIST_COLOR_TEXT);}

  public Component getListCellRendererComponent(
    JList list,Object value,int index,boolean isselected,boolean cellHasFocus){
    if(isselected){
      setBackground(FRCG.BMLIST_COLOR_SELECTED);
    }else{
      setBackground(FRCG.BMLIST_COLOR_DEFAULT);}
    BubbleModel bm=(BubbleModel)value;
    setIcon(new ImageIcon(new BMImage(bm)));
    setText(bm.id);
    return this;}
  
}
