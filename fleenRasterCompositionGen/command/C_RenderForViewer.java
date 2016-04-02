package org.fleen.junk.fleenRasterCompositionGen.command;

import org.fleen.junk.fleenRasterCompositionGen.FRCG;
import org.fleen.junk.fleenRasterCompositionGen.renderer.Renderer_Abstract;

public class C_RenderForViewer implements Command{

  public void execute(){
    Renderer_Abstract r=FRCG.instance.config.getRenderer();
    FRCG.instance.viewerimage=
      r.renderForViewer(FRCG.instance.composition,FRCG.instance.ui.panView);
    FRCG.instance.ui.panView.update();}

  public String getDescription(){
    return "rendering for viewer";}

}
