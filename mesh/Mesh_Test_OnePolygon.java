package org.fleen.junk.mesh;

public class Mesh_Test_OnePolygon extends Mesh{
  
  public static MPoint
    p0=new MPoint("p0",0,0),
    p1=new MPoint("p1",0,1),
    p2=new MPoint("p2",0,2),
    p3=new MPoint("p3",1,2),
    p4=new MPoint("p4",2,2),
    p5=new MPoint("p5",2,1),
    p6=new MPoint("p5",1,1),
    p7=new MPoint("p5",1,0);
  
  public static MPolygon 
    g0=new MPolygon("g0",p0,p1,p2,p3,p4,p5,p6,p7);
  
  
  public static MShape 
    s0=new MShape(g0);
  
  public Mesh_Test_OnePolygon(){
    root=s0;}

}
