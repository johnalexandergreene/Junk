package org.fleen.junk.fleenRasterCompositionGen.command;

import org.fleen.junk.fleenRasterCompositionGen.FRCG;
import org.fleen.junk.fleenRasterCompositionGen.renderer.Renderer_Abstract;

public class C_RenderForExport implements Command{

  public void execute(){
    Renderer_Abstract r=FRCG.instance.config.getRenderer();
    FRCG.instance.exportimage=
      r.renderForExport(FRCG.instance.composition,FRCG.instance.config.getExportImageScale());}

  public String getDescription(){
    return "rendering for export";}

}
