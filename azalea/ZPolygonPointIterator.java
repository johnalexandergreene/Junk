package org.fleen.junk.azalea;

import java.util.Iterator;

/*
 * Iterates throught the points of a ZPolygon
 * start with a head point and a reference to the polygon we're traversing
 * the head point should have a role referring to our polygon and 2 other points
 * pick one of those 2 points, which one doesn't matter
 * continue picking pointd from there.
 *   every point should have a pointrole referring to our polygon and 2 points
 *   every time we pick the point that we have not yet traversed
 * stop when we return to our head point
 */
class ZPolygonPointIterator implements Iterator<ZPoint>{

  ZPolygonPointIterator(ZPolygon polygon,ZPoint head){
    this.polygon=polygon;
    this.head=head;}

  ZPolygon polygon;
  ZPoint
    head,
    present=null,
    prior=null;
  
  public boolean hasNext(){
    if(present==null)return true;
    if(prior==null)return true;
    ZPointRole r=present.getRole(polygon);
    if(r==null)
      throw new IllegalArgumentException(
        "We arrived at a point that does not have a role in this iterator's polygon : "+polygon);
    if(r.getOtherAdjacent(prior)==head)return false;
    return true;}

  public ZPoint next(){
    if(present==null){
      present=head;
    }else{
      ZPointRole r=present.getRole(polygon);
      if(r==null)
        throw new IllegalArgumentException(
          "We arrived at a point that does not have a role in this iterator's polygon : "+polygon);
      if(r.hasNullConnection())
        throw new IllegalArgumentException("Null connection in role discovered @ point iterator. Present="+present+" Prior="+prior);
      if(r.a0==prior){
        prior=present;
        present=r.a1;
      }else{
        prior=present;
        present=r.a0;}}
    return present;}

  //unimplemented
  public void remove(){}

}
