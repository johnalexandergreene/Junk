package org.fleen.junk.mesh;

import java.util.Iterator;

class MPointIterator implements Iterator<MPoint>{

  MPointIterator(MPolygon polygon,MPoint head){
    this.polygon=polygon;
    this.head=head;}

  MPolygon polygon;
  MPoint
    head,
    present=null,
    prior=null;
  
  public boolean hasNext(){
    if(present==null)return true;
    if(prior==null)return true;
    MPointRole r=present.getRole(polygon);
    if(r==null)
      throw new IllegalArgumentException(
        "We arrived at a point that does not have a role in this iterator's polygon : "+polygon);
    if(r.getOtherAdjacent(prior)==head)return false;
    return true;}

  public MPoint next(){
    if(present==null){
      present=head;
    }else{
      MPointRole r=present.getRole(polygon);
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
