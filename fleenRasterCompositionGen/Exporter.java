package org.fleen.junk.fleenRasterCompositionGen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOWriteProgressListener;
import javax.imageio.stream.ImageOutputStream;

import com.sun.imageio.plugins.png.PNGMetadata;

/*
 * writes a PNG to the export dir
 */
public class Exporter{
  
  //To get our pixelsPerUnitXAxis value for the PNG image metadata we multiply this by our 
  //specified DPI value.
  private static final double INCHES_IN_A_METER=39.3700787;
  private static final int DPI=300;
  
  public BufferedImage image;
  public File file;
  public int dpi;
  
  public void export(BufferedImage image,File exportdir){
    this.image=image;
    this.file=getExportFile(exportdir);
    this.dpi=DPI;
    write();}
  
  static File getExportFile(File exportdir){
    File test=null;
    boolean nameIsUsed=true;
    int index=0;
    while(nameIsUsed){
      test=new File(exportdir.getPath()+"/fleen_"+index+".png");
      if(test.exists()){
        index++;
      }else{
        nameIsUsed=false;}}
    return test;}
  
  void write(){
    Iterator<ImageWriter> i=ImageIO.getImageWritersBySuffix("png");
    ImageWriter writer=(ImageWriter)i.next();
    writer.addIIOWriteProgressListener(new WriterListener());
    ImageOutputStream imageOutputstream=null;
    try{
      imageOutputstream=ImageIO.createImageOutputStream(file);
    }catch(Exception e){
      e.printStackTrace();}
    writer.setOutput(imageOutputstream);
    PNGMetadata metaData=
      (PNGMetadata)writer.getDefaultImageMetadata(new ImageTypeSpecifier(image),null);
    metaData.pHYs_pixelsPerUnitXAxis=(int)(dpi*INCHES_IN_A_METER);
    metaData.pHYs_pixelsPerUnitYAxis=(int)(dpi*INCHES_IN_A_METER);
    metaData.pHYs_present=true;
    metaData.pHYs_unitSpecifier=PNGMetadata.PHYS_UNIT_METER;
    try{
      writer.write(null,new IIOImage(image,null,metaData),null);
      imageOutputstream.flush();
      imageOutputstream.close();
    }catch(Exception e){
      e.printStackTrace();}}
  
  private class WriterListener implements IIOWriteProgressListener{

    static final float PROGRESSINCREMENT=10f;
    float previousprogress=0f;
    
    public void imageStarted(ImageWriter source,int imageIndex){}

    public void imageProgress(ImageWriter source,float percentageDone){
      if(percentageDone>(previousprogress+PROGRESSINCREMENT)){
        Log.m1(".");
        previousprogress+=PROGRESSINCREMENT;}}

    public void imageComplete(ImageWriter source){
      Log.m1("[finished exporting]");}

    public void thumbnailStarted(ImageWriter source,int imageIndex,
        int thumbnailIndex){}

    public void thumbnailProgress(ImageWriter source,float percentageDone){}

    public void thumbnailComplete(ImageWriter source){}

    public void writeAborted(ImageWriter source){}}

}
