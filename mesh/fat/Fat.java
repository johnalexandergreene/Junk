package org.fleen.junk.mesh.fat;

import java.util.List;

import org.fleen.junk.mesh.MPoint;
import org.fleen.junk.mesh.MShape;

//id interface for fat 
//we have 2 kinds of fat, one for boiled shapes and another for crushed shapes
public interface Fat{
  
  MShape getShape();
  
  List<FatController> getFatControllers();
  
  /*
   * return the fatcontroller in this fat that addresses the specified point
   */
  FatController getFatController(MPoint point);
  

}
