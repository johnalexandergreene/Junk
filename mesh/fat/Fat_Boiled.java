package org.fleen.junk.mesh.fat;

import java.util.ArrayList;
import java.util.List;

import org.fleen.junk.mesh.MPoint;
import org.fleen.junk.mesh.MShape;

public class Fat_Boiled implements Fat{
    
  MShape shape;
  double idealthickness;
  List<FatController> fatcontrollers;

  /*
   * the shape is this fat's shape
   * the point lists are clockwise, in parallel, 1-to-1
   * the ideal tickness is in real terms
   */
  public Fat_Boiled(MShape shape,List<MPoint> outeredgepoints,List<MPoint> inneredgepoints,double idealthickness){
    this.shape=shape;
    this.idealthickness=idealthickness;
    initControllers(outeredgepoints,inneredgepoints,idealthickness);
  }
  
  private void initControllers(List<MPoint> outeredgepoints,List<MPoint> inneredgepoints,double idealthickness){
    int s=outeredgepoints.size();
    fatcontrollers=new ArrayList<FatController>(s); 
    FatController fcseg;
    for(int i=0;i<s;i++){
      fcseg=new FatController_Seg(this,outeredgepoints,inneredgepoints,i);
      fatcontrollers.add(fcseg);}}
  
  public MShape getShape(){
    return shape;}

  public List<FatController> getFatControllers(){
    return fatcontrollers;}

  public FatController getFatController(MPoint point){
    for(FatController fc:fatcontrollers)
      if(fc.controls(point))
        return fc;
    return null;}

}
