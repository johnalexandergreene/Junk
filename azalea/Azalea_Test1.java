package org.fleen.junk.azalea;

import java.util.Arrays;

public class Azalea_Test1 extends Azalea{
  
  static ZPoint 
    g0p0=new ZPoint(0,0,"g0p0"),
    g0p1=new ZPoint(6,0,"g0p1"),
    g0p2=new ZPoint(6,6,"g0p2"),
    g0p3=new ZPoint(0,6,"g0p3");
  
  static ZPoint 
    g1p0=new ZPoint(1,1,"g1p0"),
    g1p1=new ZPoint(1,5,"g1p1"),
    g1p2=new ZPoint(5,5,"g1p2"),
    g1p3=new ZPoint(5,1,"g1p3");
  
  static ZPoint 
    g2p0=new ZPoint(1.5,1.5,"g2p0"),
    g2p1=new ZPoint(1.5,2,"g2p1"),
    g2p2=new ZPoint(2,2,"g2p2"),
    g2p3=new ZPoint(2,1.5,"g2p3");
  
  static ZPoint 
    g3p0=new ZPoint(1.5,4,"g3p0"),
    g3p1=new ZPoint(1.5,4.5,"g3p1"),
    g3p2=new ZPoint(2,4.5,"g3p2"),
    g3p3=new ZPoint(2,4,"g3p3");
  
  static ZPoint 
    g4p0=new ZPoint(4,4,"g4p0"),
    g4p1=new ZPoint(4,4.5,"g4p1"),
    g4p2=new ZPoint(4.5,4.5,"g4p2"),
    g4p3=new ZPoint(4.5,4,"g4p3");
  
  static ZPoint 
    g5p0=new ZPoint(4,1.5,"g5p0"),
    g5p1=new ZPoint(4,2,"g5p1"),
    g5p2=new ZPoint(4.5,2,"g5p2"),
    g5p3=new ZPoint(4.5,1.5,"g5p3");
  
  static ZPolygon 
    g0=new ZPolygon(g0p0,g0p1,g0p2,g0p3),
    g1=new ZPolygon(g1p0,g1p1,g1p2,g1p3),
    g2=new ZPolygon(g2p0,g2p1,g2p2,g2p3),
    g3=new ZPolygon(g3p0,g3p1,g3p2,g3p3),
    g4=new ZPolygon(g4p0,g4p1,g4p2,g4p3),
    g5=new ZPolygon(g5p0,g5p1,g5p2,g5p3);
  
  static ZLattice 
    l0=new ZLattice(g0),
    l1=new ZLattice(g1),
    l2=new ZLattice(g2),
    l3=new ZLattice(g3),
    l4=new ZLattice(g4),
    l5=new ZLattice(g5);
  
  static ZYard 
    y0=new ZYard(),
    y1=new ZYard();
  
  static{
    l0.setChild(y0);
    y0.setParent(l0);
    
    y0.setChild(l1);
    l1.setParent(y0);
    
    l1.setChild(y1);
    y1.setParent(l1);
    
    y1.setChildren(Arrays.asList(new ZLattice[]{l2,l3,l4,l5}));
    l2.setParent(y1);
    l3.setParent(y1);
    l4.setParent(y1);
    l5.setParent(y1);
    
    y0.outerpolygon=g0;
    y1.outerpolygon=g1;
    
   
    
  };
  
  public Azalea_Test1(){
    
    
    
    super(l0,0,100,0,100,0.05,0.001);
  }
  
  
  
  

}
