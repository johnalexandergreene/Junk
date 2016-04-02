package org.fleen.junk.azalea;

import org.fleen.geom_2D.DPoint;

/*
 * id interface for the primary elements of the azalea : Yards and Polygons
 * 
 * a lattice is not considered a primary element, 
 * it is considered an agglomeration of one type of primary element (polygons), 
 * partitioned by another primary element (yards)
 */
public interface ZElement{
  
  boolean containsPoint(DPoint p);

}
