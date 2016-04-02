package org.fleen.junk.mesh;


public class Mesh_Test_SimpleCompositionForBoilingTest extends Mesh{
  
  public MPoint
    p0=new MPoint("p0",0,0),
    p1=new MPoint("p1",0,4),
    p2=new MPoint("p2",1,4),
    p3=new MPoint("p3",2,4),
    p4=new MPoint("p4",3,4),
    p5=new MPoint("p5",3,0),
    p6=new MPoint("p6",2,0),
    p7=new MPoint("p7",1,0),
    p8=new MPoint("p8",1,1),
    p9=new MPoint("p9",1,2),
    p10=new MPoint("p10",1,3),
    p11=new MPoint("p11",2,3),
    p12=new MPoint("p12",2,2),
    p13=new MPoint("p13",2,1);
  
  public MPolygon 
    g0=new MPolygon("g0",p0,p1,p2,p3,p4,p5,p6,p7),
    g1=new MPolygon("g1",p0,p1,p2,p10,p9,p8,p7),
    g2=new MPolygon("g2",p7,p8,p9,p10,p2,p3,p11,p12,p13,p6),
    g3=new MPolygon("g3",p6,p13,p12,p11,p3,p4,p5),
    g4=new MPolygon("g4",p2,p3,p11,p10),
    g5=new MPolygon("g5",p8,p9,p10,p11,p12,p13),
    g6=new MPolygon("g6",p7,p8,p13,p6),
    g7=new MPolygon("g7",p9,p10,p11,p12),
    g8=new MPolygon("g8",p8,p9,p12,p13);
  
  
  public MShape 
    s0=new MShape(g0),
    s1=new MShape(g1),
    s2=new MShape(g2),
    s3=new MShape(g3),
    s4=new MShape(g4),
    s5=new MShape(g5),
    s6=new MShape(g6),
    s7=new MShape(g7),
    s8=new MShape(g8);
  
  public Mesh_Test_SimpleCompositionForBoilingTest(){
    root=s0;
    s0.setChildren(s1,s2,s3);
    s1.parent=s0;
    s2.parent=s0;
    s3.parent=s0;
    s2.setChildren(s4,s5,s6);
    s4.parent=s2;
    s5.parent=s2;
    s6.parent=s2;
    s5.setChildren(s7,s8);
    s7.parent=s5;
    s8.parent=s5;}

}
