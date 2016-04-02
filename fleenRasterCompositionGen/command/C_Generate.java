package org.fleen.junk.fleenRasterCompositionGen.command;

import org.fleen.junk.fleenRasterCompositionGen.Composition;
import org.fleen.junk.fleenRasterCompositionGen.FRCG;

public class C_Generate implements Command{

  public void execute(){
    FRCG.instance.composition=new Composition();
    FRCG.instance.ui.panView.centerAndFit();
    CQ.renderForViewer();}

  public String getDescription(){
    return "GENERATING";}

}
