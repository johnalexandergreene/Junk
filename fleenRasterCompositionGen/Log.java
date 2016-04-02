package org.fleen.junk.fleenRasterCompositionGen;

public class Log{
  
  /*
   * MESSAGE LEVEL 0
   * Trivial stuff
   */
  public static final void m0(String s){
    FRCG.instance.ui.statusview.report(s,0);}
  
  /*
   * MESSAGE LEVEL 1
   * Relevent to whatever process we're in the midst of
   */
  public static final void m1(String s){
    FRCG.instance.ui.statusview.report(s,1);}
  
  /*
   * MESSAGE LEVEL 2
   * Dire warnings and death threats
   * Exception messages, stack traces and other System.out stuff also gets piped here
   */
  public static final void m2(String s){
    FRCG.instance.ui.statusview.report(s,2);}
  
//private static final void redirectSystemStreams() {
//final OutputStream out = new OutputStream(){
//  //
//  public void write(final int b) throws IOException {
//    m2(String.valueOf((char) b));}
//  //
//  public void write(byte[] b, int off, int len) throws IOException {
//    m2(new String(b, off, len));}
//  //
//  public void write(byte[] b) throws IOException {
//    write(b, 0, b.length);}};
//System.setOut(new PrintStream(out,true));
//System.setErr(new PrintStream(out,true));}
  
}
