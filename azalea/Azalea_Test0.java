package org.fleen.junk.azalea;

public class Azalea_Test0 extends Azalea{
  
  static ZPoint 
    rp0=new ZPoint(0,0,"r0"),
    rp1=new ZPoint(6,0,"r1"),
    rp2=new ZPoint(6,6,"r2"),
    rp3=new ZPoint(0,6,"r3");
  
  static ZPoint 
    cp0=new ZPoint(2,2,"c0"),
    cp1=new ZPoint(4,2,"c1"),
    cp2=new ZPoint(4,4,"c2"),
    cp3=new ZPoint(2,4,"c3");
  
  static ZPolygon 
    root_outeredge=new ZPolygon(rp0,rp1,rp2,rp3),
    child_outeredge=new ZPolygon(cp0,cp1,cp2,cp3);
  
  static ZLattice 
    root=new ZLattice(root_outeredge),
    child=new ZLattice(child_outeredge);
  
  static ZYard yard=new ZYard();
  
  static{
    root.setChild(yard);
    yard.setParent(root);
    yard.setChild(child);
    yard.outerpolygon=root_outeredge;
    child.setParent(yard);
  };
  
  public Azalea_Test0(){
    
    
    
    super(root,0,100,0,100,0.05,0.001);
  }
  
  
  
  

}
