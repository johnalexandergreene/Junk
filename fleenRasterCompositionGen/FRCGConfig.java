package org.fleen.junk.fleenRasterCompositionGen;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;

import org.fleen.core.grammaticOLD.BubbleModel;
import org.fleen.core.grammaticOLD.Grammar;
import org.fleen.junk.fleenRasterCompositionGen.renderer.Renderer_000;
import org.fleen.junk.fleenRasterCompositionGen.renderer.Renderer_001;
import org.fleen.junk.fleenRasterCompositionGen.renderer.Renderer_002;
import org.fleen.junk.fleenRasterCompositionGen.renderer.Renderer_003;
import org.fleen.junk.fleenRasterCompositionGen.renderer.Renderer_004;
import org.fleen.junk.fleenRasterCompositionGen.renderer.Renderer_005;
import org.fleen.junk.fleenRasterCompositionGen.renderer.Renderer_006;
import org.fleen.junk.fleenRasterCompositionGen.renderer.Renderer_Abstract;
import org.fleen.junk.fleenRasterCompositionGen.symmetryController.SC_BigRandomSmallSymmetry;
import org.fleen.junk.fleenRasterCompositionGen.symmetryController.SC_PureRandom;
import org.fleen.junk.fleenRasterCompositionGen.symmetryController.SC_PureSymmetry;
import org.fleen.junk.fleenRasterCompositionGen.symmetryController.SymmetryControlFunction_Abstract;
import org.fleen.junk.fleenRasterCompositionGen.ui.RootBMListModel;

/*
 * Configuration for Fleen Raster Composition Generator
 * get the local directory
 * look there for the config object file
 *   if it's there then load it
 *   if it isn't then create a new one
 * init the various ui to reflect the new config
 *  
 * 
 */
public class FRCGConfig implements Serializable{
  
  private static final long serialVersionUID=8345027552779619871L;

  /*
   * ################################
   * GRAMMAR
   * We store the grammar file. Extract as necessary.
   * ################################
   */
  
  private static final String 
    DEFAULT_GRAMMAR_FILE_NAME="defaultgrammar.g",
    DEFAULT_GRAMMAR_FILE_SUFFIX=".g";
  
  private File grammarfile;
  private volatile ForsythiaGrammar grammar=null;
  
  public File getGrammarFile(){
    if(grammarfile==null)
      initGrammarFile();
    return grammarfile;}
  
  public ForsythiaGrammar getGrammar(){
    if(grammar==null)
      grammar=extractGrammarFromFile(getGrammarFile());
  return grammar;}
  
  public String getGrammarName(){
  if(grammarfile==null)
    initGrammarFile();
  return grammarfile.getName();}
  
  /*
   * get defaultgrammar.g from the local directory
   * if it isn't there then try every .g file in the local directory
   * if that fails then throw exception
   */
  private void initGrammarFile(){
    setGrammarFile(getDefaultGrammarFile());}
  
  private File getDefaultGrammarFile(){
    File localdir=FRCG.getLocalDir();
    File grammarfile=null;
    ForsythiaGrammar testgrammar=null;
    //try to get the default grammar file from the local directory
    File[] localfiles=localdir.listFiles(new FileFilter(){
      public boolean accept(File a){
        return a.getName().equals(DEFAULT_GRAMMAR_FILE_NAME);}});
    if(localfiles.length>0){
      grammarfile=localfiles[0];
      try{
        testgrammar=extractGrammarFromFile(grammarfile);
      }catch(Exception x){}}
    //if that didn't work then try to get any grammar file from the local directory
    if(testgrammar==null){
      localfiles=localdir.listFiles(new FileFilter(){
        public boolean accept(File a){
          return a.getName().endsWith(DEFAULT_GRAMMAR_FILE_SUFFIX);}});
      for(File b:localfiles){
        grammarfile=b;
        try{
          testgrammar=extractGrammarFromFile(grammarfile);
        }catch(Exception x){}
        if(testgrammar!=null)break;}}
    //if we have a working grammar then return that file, otherwise we have a null grammar file.
    if(testgrammar==null)
      Log.m2("We appear to be missing a default grammar file in the local directory. Grammar set to null.");
    return grammarfile;}
  
  public void setGrammarFile(){
    JFileChooser fc=new JFileChooser("Select Grammar");
    if(grammarfile!=null){
      fc.setCurrentDirectory(grammarfile);
    }else{
      fc.setCurrentDirectory(FRCG.getLocalDir());}
    int r=fc.showOpenDialog(FRCG.instance.ui.frame);
    if(r!=JFileChooser.APPROVE_OPTION)
      return;
    File f=fc.getSelectedFile();
    setGrammarFile(f);}
  
  public void setGrammarFile(File f){
    grammar=null;
    grammarfile=f;
    invalidateRootBubbleModel();}

  public void initUIComponent_Grammar(){
    String p=getGrammarFile().getPath();
    FRCG.instance.ui.txtGrammarPath.setText(p);
    FRCG.instance.ui.txtGrammarPath.setToolTipText("Grammar File : "+p);}
  
  private ForsythiaGrammar extractGrammarFromFile(File file){
    FileInputStream fis;
    ObjectInputStream ois;
    ForsythiaGrammar grammar=null;
    try{
      fis=new FileInputStream(file);
      ois=new ObjectInputStream(fis);
      grammar=(ForsythiaGrammar)ois.readObject();
      ois.close();
    }catch(Exception x){
      throw new IllegalArgumentException(x);}
    return grammar;}
  
  /*
   * ################################
   * ROOT BUBBLE MODEL
   * ################################
   */
  
  //an index within the grammar's bubblemodels list
  private static final int ROOTBUBBLEMODELINDEX_DEFAULT=0;
  private int rootbubblemodelindex=-1;
  
  public int getRootBubbleModelIndex(){
    if(rootbubblemodelindex==-1)
      initRootBubbleModel();
    return rootbubblemodelindex;}
  
  public BubbleModel getRootBubbleModel(){
    return getGrammar().getBubbleModels().get(getRootBubbleModelIndex());}
  
  public void setRootBubbleModel(BubbleModel m){
    rootbubblemodelindex=getGrammar().getBubbleModels().indexOf(m);}
  
  private void initRootBubbleModel(){
    rootbubblemodelindex=ROOTBUBBLEMODELINDEX_DEFAULT;
    initUIComponent_RootBubbleModel();}
  
  public void invalidateRootBubbleModel(){
    rootbubblemodelindex=-1;}
  
  public void initUIComponent_RootBubbleModelsList(){
    List<BubbleModel> m=getGrammar().getBubbleModels();
    FRCG.instance.ui.lstRootBubbleModel.setModel(
      new DefaultComboBoxModel(m.toArray(new BubbleModel[m.size()])));}
  
  public void initUIComponent_RootBubbleModel(){
    FRCG.instance.ui.lstRootBubbleModel.setModel(new RootBMListModel());
    FRCG.instance.ui.lstRootBubbleModel.setSelectedValue(getRootBubbleModel(),true);}
  
  /*
   * ################################
   * DETAIL SIZE LIMIT
   * unscaled floor for cultivatable bubble detail size
   * ################################
   */
  
  private static final double DETAIL_SIZE_LIMIT_DEFAULT=0.123;
  private double detailsizelimit=-1;
  
  public double getDetailSizeLimit(){
    if(detailsizelimit==-1)
      detailsizelimit=DETAIL_SIZE_LIMIT_DEFAULT;
    return detailsizelimit;}
  
  public void setDetailSizeLimit(String s){
    try{
      Double d=new Double(s);
      setDetailSizeLimit(d);
    }catch(Exception e){}}
  
  public void setDetailSizeLimit(double d){
    if(d>0)
      detailsizelimit=d;}
  
  public void initUIComponent_DetailSizeLimit(){
    FRCG.instance.ui.txtDetailLimit.setText(
      Double.toString(getDetailSizeLimit()));}
  
  /*
   * ################################
   * SYMMETRY CONTROL FUNTION
   * A function of detail size
   * Given bubble.detailsize, the function controls the probability of using the symmetric cultivation algorithm
   * TODO we're just using the default "all symmetric function" right now
   * what we need to do is offer a way to draw a curve. Specify curve control points and such.
   * ################################
   */
  
  private static final SymmetryControlFunction_Abstract[] SYMMETRYCONTROLFUNCTIONS={
    new SC_PureSymmetry(),
    new SC_PureRandom(),
    new SC_BigRandomSmallSymmetry()};
  
  private SymmetryControlFunction_Abstract symmetrycontrolfunction=null;
  
  public SymmetryControlFunction_Abstract getSymmetryControlFunction(){
    if(symmetrycontrolfunction==null)
      initSymmetryControlFunction();
    return symmetrycontrolfunction;}
  
  private void initSymmetryControlFunction(){
    symmetrycontrolfunction=new SC_PureSymmetry();}
  
  public void setSymmetryControlFunction(SymmetryControlFunction_Abstract scf){
    symmetrycontrolfunction=scf;}
  
  public void initUIComponent_SymmetryControlFunctionList(){
    FRCG.instance.ui.cmbSymConFun.setModel(new DefaultComboBoxModel(SYMMETRYCONTROLFUNCTIONS));}
  
  public void initUIComponent_SymmetryControlFunction(){
    FRCG.instance.ui.cmbSymConFun.getModel().setSelectedItem(getSymmetryControlFunction());}
  
  /*
   * ################################
   * RENDERER
   * We'll have a few of these
   * A simple one that we can create multiple versions of and set the colors for
   * A couple of fancy ones. A smart colorspace thing. A curve smoothing thing.
   * ################################
   */
  
  private static final Renderer_Abstract[] RENDERERS={
    new Renderer_000(),
    new Renderer_001(),
    new Renderer_002(),
    new Renderer_003(),
    new Renderer_004(),
    new Renderer_005(),
    new Renderer_006()
    };
  
  private Renderer_Abstract renderer=null;
  
  public Renderer_Abstract getRenderer(){
    if(renderer==null)
      initRenderer();
    return renderer;}
  
  private void initRenderer(){
    renderer=new Renderer_000();}
  
  public void setRenderer(Renderer_Abstract r){
    renderer=r;}
  
  public void initUIComponent_RendererList(){
    FRCG.instance.ui.cmbRenderer.setModel(new DefaultComboBoxModel(RENDERERS));}
  
  public void initUIComponent_Renderer(){
    FRCG.instance.ui.cmbRenderer.getModel().setSelectedItem(getRenderer());}
  
  /*
   * ################################
   * EXPORT IMAGE SCALE
   * we generally scale up
   * ################################
   */
   
  private static final double EXPORT_IMAGE_SCALE_DEFAULT=150.0;
  private double exportimagescale=-1;
  
  public double getExportImageScale(){
    if(exportimagescale==-1)
      initExportImageScale();
    return exportimagescale;}
  
  private void initExportImageScale(){
    exportimagescale=EXPORT_IMAGE_SCALE_DEFAULT;}
  
  public void setExportImageScale(String s){
    try{
      Double d=new Double(s);
      setExportImageScale(d);
    }catch(Exception e){}}
  
  public void setExportImageScale(double d){
    if(d<=0)return;
    exportimagescale=d;}
  
  public void initUIComponent_ExportImageScale(){
    FRCG.instance.ui.txtExpScale.setText(
      Double.toString(getExportImageScale()));}
  
  /*
   * ################################
   * EXPORT DIR
   * path to the directory to which we export our generated fleen raster composition PNG files
   * init to default working directory
   * ################################
   */
  
  private File exportdir=null;
  
  public File getExportDir(){
    if(exportdir==null)
      initExportDir();
    return exportdir;}
  
  private void initExportDir(){
    exportdir=FRCG.getLocalDir();}
  
  public void setExportDir(){
    JFileChooser fc=new JFileChooser();
    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fc.setCurrentDirectory(FRCG.getLocalDir());
    int r=fc.showOpenDialog(FRCG.instance.ui.frame);
    if(r!=JFileChooser.APPROVE_OPTION)
      return;
    File f=fc.getSelectedFile();
    setExportDir(f);}
  
  public void setExportDir(File f){
    if(f.isDirectory())
      exportdir=f;}
  
  public void initUIComponent_ExportDir(){
    String s=getExportDir().toString();
    FRCG.instance.ui.txtExportDir.setText(s);
    FRCG.instance.ui.txtExportDir.setToolTipText("Export Dir : "+s);}
  
  /*
   * ################################
   * GENEXP IMAGE COUNT
   * ################################
   */
  
  private static final int GENEXP_IMAGE_COUNT_DEFAULT=3;
  private int genexpimagecount=-1;
  
  public int getGenExpImageCount(){
    if(genexpimagecount==-1)
      initGenExpImageCount();
    return genexpimagecount;}
  
  private void initGenExpImageCount(){
    genexpimagecount=GENEXP_IMAGE_COUNT_DEFAULT;}
  
  public void setGenExpImageCount(int i){
    if(i>0)
      genexpimagecount=i;}
  
  public void initUIComponent_GenExpImageCount(){
    FRCG.instance.ui.spiGenExpCount.setValue(getGenExpImageCount());}
  
  /*
   * ################################
   * UTIL
   * ################################
   */
  
  public void initUIComponents(){
    initUIComponent_Grammar();
    initUIComponent_RootBubbleModelsList();
    initUIComponent_RootBubbleModel();
    initUIComponent_DetailSizeLimit();
    initUIComponent_SymmetryControlFunctionList();
    initUIComponent_SymmetryControlFunction();
    initUIComponent_RendererList();
    initUIComponent_Renderer();
    initUIComponent_ExportImageScale();
    initUIComponent_ExportDir();
    initUIComponent_GenExpImageCount();}

}
