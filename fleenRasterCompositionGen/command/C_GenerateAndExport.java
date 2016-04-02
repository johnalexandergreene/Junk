package org.fleen.junk.fleenRasterCompositionGen.command;

import org.fleen.junk.fleenRasterCompositionGen.FRCG;

public class C_GenerateAndExport implements Command{

  public void execute(){
    int c=FRCG.instance.config.getGenExpImageCount();
    for(int i=0;i<c;i++){
      CQ.generate();
      CQ.renderForViewer();
      CQ.renderForExport();
      CQ.export();}}

  public String getDescription(){
    return "generating & exporting : "+FRCG.instance.config.getGenExpImageCount();}

}
