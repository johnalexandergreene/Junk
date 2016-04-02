package org.fleen.junk.fleenRasterCompositionGen.command;

import org.fleen.junk.fleenRasterCompositionGen.FRCG;

public class C_SaveConfig implements Command{

  public void execute(){
    FRCG.saveConfig();}

  public String getDescription(){
    return "saving config";}

}
