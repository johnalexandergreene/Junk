package org.fleen.junk.fleenRasterCompositionGen.ui;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.fleen.core.grammaticOLD.BubbleModel;
import org.fleen.forsythia.app.grammarEditor.C;
import org.fleen.geom_Kisrhombille.KPolygon;
import org.fleen.geom_Kisrhombille.KVertex;
import org.fleen.junk.fleenRasterCompositionGen.FRCG;
import org.fleen.junk.fleenRasterCompositionGen.renderer.Renderer_Abstract;

public class BMImage extends BufferedImage{
  
  /*
   * ################################
   * CONSTRUCTORS
   * ################################
   */
  
  //for valid
  public BMImage(BubbleModel m){
    super(
      FRCG.BMLIST_CELLSPAN,
      FRCG.BMLIST_CELLSPAN,
      BufferedImage.TYPE_INT_ARGB_PRE);
    render(m);}
  
  /*
   * ################################
   * RENDER VALID BUBBLEMODEL
   * ################################
   */
  
  private void render(BubbleModel bubblemodel){
    //init image and metrics
    Path2D polygon=getImagePath(bubblemodel);
    Rectangle2D pbounds=polygon.getBounds2D();
    double pw=pbounds.getWidth(),ph=pbounds.getHeight(),scale;
    int polygonimagespan=FRCG.BMLIST_CELLSPAN-FRCG.BMLIST_ICONPADDING-FRCG.BMLIST_ICONPADDING;
    Graphics2D g=createGraphics();
    g.setRenderingHints(Renderer_Abstract.RENDERING_HINTS);
    //scale and center
    scale=(pw>ph)?polygonimagespan/pw:polygonimagespan/ph;
    AffineTransform t=new AffineTransform();
    t.scale(scale,-scale);//note y flip
    double 
      xoffset=-pbounds.getMinX()+(((FRCG.BMLIST_CELLSPAN/scale)-pbounds.getWidth())/2.0),
      yoffset=-pbounds.getMaxY()-(((FRCG.BMLIST_CELLSPAN/scale)-pbounds.getHeight())/2.0);
    t.translate(xoffset,yoffset);
    g.transform(t);
    //fill it
    g.setColor(FRCG.BMLIST_COLOR_BUBBLEMODELIMAGEFILL);
    g.fill(polygon);
    //stroke it
    g.setColor(FRCG.BMLIST_COLOR_BUBBLEMODELIMAGESTROKE);
    g.setStroke(new BasicStroke(
      (float)(C.OG_STROKEWIDTH_IMAGEMETAGONVALID/scale),
      BasicStroke.CAP_SQUARE,
      BasicStroke.JOIN_ROUND,
      0,null,0));
    g.draw(polygon);}
  
  public Path2D.Double getImagePath(BubbleModel bm){
    Path2D.Double imagepath;
    KPolygon vertexpath=bm.getVectorPath().getVertexPath();
    if(vertexpath.size()<3)
      throw new IllegalArgumentException("VERTEX COUNT IS <3");
    imagepath=new Path2D.Double();
    KVertex v=vertexpath.get(0);
    double[] p=v.getBasicPoint2D();
    imagepath.moveTo(p[0],p[1]);
    for(int i=1;i<vertexpath.size();i++){
      v=vertexpath.get(i);
      p=v.getBasicPoint2D();
      imagepath.lineTo(p[0],p[1]);}
    imagepath.closePath();
    return imagepath;}
  
}
