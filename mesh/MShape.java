package org.fleen.junk.mesh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.fleen.forsythia.composition.FPolygon;
import org.fleen.junk.mesh.collisionMap.CollisionMap;
import org.fleen.junk.mesh.fat.Fat;
import org.fleen.util.tag.TagManager;

/*
 * a 2d area composed of one outer edge polygon and 0..n inner edge polygons
 */
public class MShape extends ArrayList<MPolygon>{
  
  private static final long serialVersionUID=-1994594131551015855L;

  /*
   * ################################
   * CONSTRUCTOR
   * ################################
   */
  
  //init using an npolygon
  //this creates an mshape with a single outer edge
  //we copy the npolygon's tags
  //we copy create parent-child relationships afterwards 
  public MShape(FPolygon np,CollisionMap cm,Mesh mesh,List<MPoint> rawpoints){
    super(1);
    this.mesh=mesh;
    //copy chorus index and tags
    chorusindex=np.getChorusIndex();
    tagmanager.setTags(np.getTags());
    //derive polygon
    add(new MPolygon(np,cm,this,rawpoints));}
  
  public MShape(List<MPolygon> polygons){
    super(polygons);}
  
  public MShape(MPolygon... polygons){
    this(Arrays.asList(polygons));}
  
  /*
   * ################################
   * MESH
   * ################################
   */
  
  public Mesh mesh;
  
  /*
   * ################################
   * GEOMETRY
   * ################################
   */
  
  public MPolygon getOuterEdge(){
    return get(0);}
  
  public List<MPolygon> getInnerEdges(){
    if(size()<2)return new ArrayList<MPolygon>(0);
    return subList(1,size());}
  
  public int getInnerEdgesCount(){
    return size()-1;}
  
  public List<MPoint> getPoints(){
    List<MPoint> a=new ArrayList<MPoint>();
    for(MPolygon p:this)
      a.addAll(p.getPoints());
    return a;}
  
  /*
   * ################################
   * CHORUS INDEX
   * ################################
   */
  
  public int chorusindex;
  
  public int getChorusIndex(){
    return chorusindex;}
  
  /*
   * ################################
   * TAGS
   * ################################
   */
  
  private TagManager tagmanager=new TagManager();
  
  public String[] getTags(){
    return tagmanager.getTags();}
  
  public void setTags(String[] tags){
    tagmanager.setTags(tags);}
  
  public boolean hasTag(String tag){
    return tagmanager.hasTag(tag);}
  
  /*
   * ################################
   * FLUFFER ID
   * ################################
   */
  
  public static final int 
    FLUFFER_NOTHING=0,
    FLUFFER_BOIL=1,
    FLUFFER_CRUSH=2;//has a level value specifying children, grandchildren, etc (??)
  
  /*
   * pre-fluffing we just tag each shape for one of our 3 fluffing treatments 
   * boil, crush or nothing
   */
  private int fluffertreatment=FLUFFER_NOTHING;
  
  public void setFlufferTreatment(int ft){
    fluffertreatment=ft;}
  
  public int getFlufferTreatment(){
    return fluffertreatment;}
  
  public void setFlufferTreatment_Nothing(){
    fluffertreatment=FLUFFER_NOTHING;}
  
  public void setFlufferTreatment_Boil(){
    fluffertreatment=FLUFFER_BOIL;}
  
  public void setFlufferTreatment_Crush(){
    fluffertreatment=FLUFFER_CRUSH;}
  
  public boolean isFlufferTreatment_Nothing(){
    return fluffertreatment==FLUFFER_NOTHING;}
  
  public boolean isFlufferTreatment_Boil(){
    return fluffertreatment==FLUFFER_BOIL;}
  
  public boolean isFlufferTreatment_Crush(){
    return fluffertreatment==FLUFFER_CRUSH;}
  
  //a skinny shape has no fat
  //that is, there's no in-between area, between the inner an outer edges. 
  //  There's just the outer edge. Because there are no inner edges.
  //this is derived from size of course
  public boolean isSkinny(){
    return size()==1;}
  
  //this is a fatty shape. it's got fat.
  //it has 1 outer edge and 1 inner edge. thus 2 edges
  public boolean isBoiled(){
    return size()==2;}
  
  //this is a fatty shape. it's got fat
  //it has 1 outer edge and 2..n inner edges (holes)
  public boolean isCrushed(){
    return size()>2;}
  
  /*
   * ################################
   * FAT
   * A shape with 1 edge is thin, a shape with 2..n edges is fat
   * the space between the outer edge and the 1..n inner edges is called fat
   * Fat is a system of segs and polygons, called FatControllers, for managing the fat geometry
   * ################################
   */
  
  private Fat fat=null;
  
  public boolean isFat(){
    return size()>1;}
  
  public boolean isThin(){
    return !isFat();}
  
  public void setFat(Fat fat){
    this.fat=fat;}
  
  public Fat getFat(){
    if(isThin())
      throw new IllegalArgumentException("this shape has no fat because it is thin");
    if(fat==null)
      throw new IllegalArgumentException("fat has not been initialized");
    return fat;}
  
  /*
   * ################################
   * TREE STUFF
   * ################################
   */
  
  //--------------------------------
  //PARENT
  
  public MShape parent=null;
  int childindex=0;
  
  public MShape getParent(){
    return parent;}
  
  public void setParent(MShape n){
    parent=n;}
  
  //--------------------------------
  //CHILDREN
  
  public MShape[] children=new MShape[]{};

  public List<MShape> getChildren(){
    return Arrays.asList(children);}
  
  public MShape getChild(int index){
    if(index<children.length)
      return children[index];
    else 
      return null;}
  
  public MShape getChild(){
    return getChild(0);}
  
  public int getChildIndex(MShape shape){
    return (Arrays.asList(children)).indexOf(shape);}

  public int getChildCount(){
    return children.length;}
  
  public void setChildren(List<MShape> c){
    children=c.toArray(new MShape[c.size()]);
    for(int i=0;i<children.length;i++)
      children[i].childindex=i;}
  
  public void setChildren(MShape... c){
    setChildren(Arrays.asList(c));}
  
  //for when there's just the one child. A convenience.
  public void setChild(MShape c){
    children=new MShape[]{c};}
  
  public void clearChildren(){
    children=new MShape[]{};}
  
  //--------------------------------
  //ANALYSIS
  
  public boolean isRoot(){
    return parent==null;}
  
  public boolean isLeaf(){
    return children.length==0;}
  
  /**
   * @return The next child (ascending indexwise) in this shape's parent's child array. 
   * null if there is no next child. 
   * Exception if this shape has no parent.
   */
  public MShape getNextSibling(){
    if(parent==null)
      throw new IllegalArgumentException("This shape has no parent");
    return parent.getChild(childindex+1);}
  
  public boolean isLastSibling(){
    return childindex==getParent().getChildCount()-1;}
  
  /**
   * @return This number of shapes encountered when traversing the tree from this shape to the root.
   */
  public int getDepth(){
    int c=0;
    MShape n=this;
    while(n!=null){
      n=n.getParent();
      if(n!=null)c++;}
    return c;}
  
  public MShape getRoot(){
    if(isRoot()){
      return this;
    }else{
      return getParent().getRoot();}}
  
  //--------------------------------
  //BRANCH ACCESS
  
  //returns iteration of branch rooted at this mshape
  public Iterator<MShape> getBranchShapeIterator(){
    return new MShapeIterator(this);}
  
  //returns all shapes in branch rooted at this mshape in a list
  public List<MShape> getBranchShapes(){
    List<MShape> shapes=new ArrayList<MShape>();
    Iterator<MShape> i=getBranchShapeIterator();
    while(i.hasNext())
      shapes.add(i.next());
    return shapes;}
  
}
