/*

FLEEN : SHAPE GARDENING TECHNOLOGY
Copyright (C) 2003,2004 John Greene
 
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

Contact the project leader via email: john@fleen.org
Visit the fleen home page: www.fleen.org

################################################################--------------*/

package org.fleen.junk.genLoopingTunnelFlowFrameSeqImage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import com.sun.imageio.plugins.png.PNGMetadata;

public class FileWriter{
  
  FileWriter thisAlias=this;
	/*
	 * To get our pixelsPerUnitXAxis value for the PNG image metadata we multiply this by our 
	 * specified DPI value.
	 */
	public static final double INCHES_IN_A_METER=39.3700787;
  
	public BufferedImage image;
	public File file;
	public int dpi;
	
	/*
	 * ------------
	 * INTERFACE
	 * ------------
	 */
	
	public void write(BufferedImage image,File file,int dpi){
	  this.image=image;
	  this.file=file;
	  this.dpi=dpi;
	  new WriteThread().start();}
	
	public void write(BufferedImage image,String exportdirpath,int dpi){
		this.image=image;
	  this.file=getSaveFile(exportdirpath);
	  this.dpi=dpi;
	  this.
	  new WriteThread().start();}
	
	/*
	 * ------------
	 * FILE INIT
	 * ------------
	 */
	
	static File getSaveFile(String path){
		File test=null;
		boolean nameIsUsed=true;
		int index=0;
		while(nameIsUsed){
			test=new File(path+"_"+index+".png");
			if(test.exists()){
				index++;
			}else{
				nameIsUsed=false;}}
		return test;}
	
	/*
	 * ----------
	 * CLASSES
	 * ----------
	 */
	
	class WriteThread extends Thread{
	  public void run(){
	    write();}}
	
    void write(){
			Iterator<ImageWriter> i=ImageIO.getImageWritersBySuffix("png");
			ImageWriter writer=(ImageWriter)i.next();
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
  
}