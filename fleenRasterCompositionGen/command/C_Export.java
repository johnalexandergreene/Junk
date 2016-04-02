package org.fleen.junk.fleenRasterCompositionGen.command;

import org.fleen.junk.fleenRasterCompositionGen.Exporter;
import org.fleen.junk.fleenRasterCompositionGen.FRCG;

public class C_Export implements Command{

  public void execute(){
    new Exporter().export(FRCG.instance.exportimage,FRCG.instance.config.getExportDir());}

  public String getDescription(){
    return "exporting";}

}
