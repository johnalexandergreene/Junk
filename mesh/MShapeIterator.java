package org.fleen.junk.mesh;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MShapeIterator implements Iterator<MShape>{
  
  MShape 
    root,
    wormhead,
    wormtail=null;
  
  //TODO
  //this is not ideal. need something cleverer. will do for now.
  Set<MShape> touched=new HashSet<MShape>();
  
  public MShapeIterator(MShape root){
    this.root=root;
    wormhead=root;
    if(!filter(wormhead))gleanNextMShape();}
  
  public boolean hasNext(){
    return wormhead!=null;}

  public MShape next(){
    MShape n=wormhead;
    gleanNextMShape();
    touched.add(n);
    return n;}

  //nope
  public void remove(){}
  
  public void gleanNextMShape(){
    if(wormhead==null)return;
    do{
      advanceWorm();
      testFinished();
      if(filter(wormhead)&&(!touched.contains(wormhead)))return;
    }while(wormhead!=null);}
  
  /**
   * @param shape The shape getting filtered
   * @return true if shape passes the filter, false if it is rejected.
   * override for filtration
   */
  protected boolean filter(MShape shape){
    return true;}
  
  //if we're finished then set wormhead to null
  private void testFinished(){
    if(wormhead==root&&wormtail.isLastSibling())
      wormhead=null;}
  
  private void advanceWorm(){
    //if we just started
    if(wormhead==root&&wormtail==null){
      wormhead=root.getChild(0);
      wormtail=root;
    //we're in mid traverse
    }else{
      //if the worm is pointing up
      if(wormhead==wormtail.getParent()){
        //if we just left the last sibling then go up 
        if(wormtail.isLastSibling()){
          wormtail=wormhead;
          wormhead=wormhead.getParent();
        //otherwise flip the worm down and address the next sibling
        }else{
          wormhead=wormtail.getNextSibling();
          wormtail=wormhead.getParent();}
      //the worm is pointing down
      }else{
        //if we hit a leaf then flip the worm up
        if(wormhead.isLeaf()){
          wormtail=wormhead;
          wormhead=wormhead.getParent();
        //head on down
        }else{
          wormhead=wormhead.getChild(0);
          wormtail=wormhead.getParent();}}}}
  
}
