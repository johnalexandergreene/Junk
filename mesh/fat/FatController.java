package org.fleen.junk.mesh.fat;

import org.fleen.junk.mesh.MPoint;

public interface FatController{
  
  /*
   * return true if this fatcontroller controls the specified point
   */
  boolean controls(MPoint point);

}
