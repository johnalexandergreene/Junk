package org.fleen.junk.fleenRasterCompositionGen.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.fleen.core.grammaticOLD.BubbleModel;
import org.fleen.junk.fleenRasterCompositionGen.FRCG;
import org.fleen.junk.fleenRasterCompositionGen.command.CQ;
import org.fleen.junk.fleenRasterCompositionGen.renderer.Renderer_Abstract;
import org.fleen.junk.fleenRasterCompositionGen.symmetryController.SymmetryControlFunction_Abstract;

public class UI{

  public JFrame frame;
  public ImageViewer panView;
  public StatusViewer statusview;
  public JList lstRootBubbleModel;
  public JTextField txtGrammarPath;
  public JTextField txtExportDir;
  public JTextField txtDetailLimit;
  public JComboBox cmbSymConFun;
  public JComboBox cmbRenderer;
  public JTextField txtExpScale;
  public JSpinner spiGenExpCount;

  /**
   * Launch the application.
   */
  public static void main(String[] args){
    EventQueue.invokeLater(new Runnable(){
      public void run(){
        try{
          UI window=new UI();
          window.frame.setVisible(true);
        }catch(Exception e){
          e.printStackTrace();}}});}

  public UI(){
    //LOOK AND FEEL
    try{
      for(LookAndFeelInfo info:UIManager.getInstalledLookAndFeels()){
        if("Nimbus".equals(info.getName())){
          UIManager.setLookAndFeel(info.getClassName());
          break;}}
    }catch(Exception e){}//go with default
    
    //FRAME
    frame=new JFrame();
    frame.setBounds(100,100,727,568);
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent e){
        FRCG.terminateInstance();}});
    frame.setTitle(FRCG.TITLE+" "+FRCG.VERSION);
    
    //TOP
    JPanel panTop = new JPanel();
    
    //VIEW
    panView = new ImageViewer();
    panView.setBackground(new Color(255, 0, 255));
    
    //STATUS 
    statusview = new StatusViewer();
    
    //FRAME CONTENTPANE LAYOUT
    GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.TRAILING)
        .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
          .addGap(21)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(panView, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE)
            .addComponent(panTop, Alignment.TRAILING, 0, 0, Short.MAX_VALUE)
            .addComponent(statusview, GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE))
          .addGap(18))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.TRAILING)
        .addGroup(groupLayout.createSequentialGroup()
          .addComponent(panTop, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(panView, GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(statusview, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
          .addContainerGap())
    );
    
    //GRAMMAR FILE
    txtGrammarPath = new JTextField("~/foo/bar/null/grammarfoo.g");
    txtGrammarPath.setEditable(false);
    txtGrammarPath.setFont(new Font("Dialog", Font.BOLD, 14));
    //mouse click listener
    //set grammar. If success then update relevant stuff 
    txtGrammarPath.addMouseListener(new MouseAdapter(){
      public void mouseClicked(MouseEvent e){
        try{
          File gf=FRCG.instance.config.getGrammarFile();
          FRCG.instance.config.setGrammarFile();
          if(gf!=FRCG.instance.config.getGrammarFile()){
            FRCG.instance.config.initUIComponent_Grammar();
            FRCG.instance.config.invalidateRootBubbleModel();
            FRCG.instance.config.initUIComponent_RootBubbleModelsList();
            FRCG.instance.config.initUIComponent_RootBubbleModel();}
        }catch(Exception x){}}});
    
    //EXPORT DIR
    txtExportDir = new JTextField("~/foo/bar/null/frcgexport");
    txtExportDir.setEditable(false);
    txtExportDir.setFont(new Font("Dialog", Font.BOLD, 14));
    //mouse click listener
    //set export dir. 
    txtExportDir.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e){
        FRCG.instance.config.setExportDir();
        FRCG.instance.config.initUIComponent_ExportDir();}});
    
    //DETAIL LIMIT
    txtDetailLimit = new JTextField();
    txtDetailLimit.setToolTipText("Detail Size Limit");
    txtDetailLimit.setFont(new Font("Dialog", Font.BOLD, 14));
    txtDetailLimit.setText("0.123");
    txtDetailLimit.setColumns(10);
    //focus lost listener
    //update and validate
    txtDetailLimit.addFocusListener(new FocusAdapter() {
      public void focusLost(FocusEvent e) {
        FRCG.instance.config.setDetailSizeLimit(txtDetailLimit.getText());
        txtDetailLimit.setText(
          Double.toString(FRCG.instance.config.getDetailSizeLimit()));}});
    
    //SYMMETRY CONTROL FUNCTION
    //TODO
    cmbSymConFun = new JComboBox();
    cmbSymConFun.setToolTipText("Symmetry Control Function");
    //selection changed listener
    //when we do it, update the params
    cmbSymConFun.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e){
        try{
          FRCG.instance.config.setSymmetryControlFunction(
            (SymmetryControlFunction_Abstract)cmbSymConFun.getModel().getSelectedItem());
        }catch(Exception x){}}});
    
    //RENDERER
    cmbRenderer = new JComboBox();
    cmbRenderer.setToolTipText("Renderer");
    //selection changed listener
    //when we do it, update the params
    cmbRenderer.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e){
        try{
          FRCG.instance.config.setRenderer(
            (Renderer_Abstract)cmbRenderer.getModel().getSelectedItem());
          CQ.renderForViewer();
        }catch(Exception x){}}});
    
    //EXPORT SCALE
    txtExpScale = new JTextField();
    txtExpScale.setToolTipText("Export Image Scale");
    txtExpScale.setFont(new Font("Dialog", Font.BOLD, 14));
    txtExpScale.setColumns(10);
    //focus lost listener
    //update and validate
    txtExpScale.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        FRCG.instance.config.setExportImageScale(txtExpScale.getText());
        txtExpScale.setText(
          Double.toString(FRCG.instance.config.getExportImageScale()));}});
    
    //GENERATE AND EXPORT COUNT
    spiGenExpCount = new JSpinner();
    spiGenExpCount.setToolTipText("Export Image Count");
    spiGenExpCount.setFont(new Font("Dialog", Font.BOLD, 14));
    //update and validate param on state change
    spiGenExpCount.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e){
        try{
          FRCG.instance.config.setGenExpImageCount(
          (Integer)spiGenExpCount.getModel().getValue());
        }catch(Exception x){}
        spiGenExpCount.getModel().setValue(
          FRCG.instance.config.getGenExpImageCount());}});
    
    //GENERATE
    JButton btnGenerate = new JButton("Generate");
    btnGenerate.setFont(new Font("Dialog", Font.BOLD, 14));
    //mouse click listener
    btnGenerate.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        try{
          CQ.generate();
        }catch(Exception x){
          x.printStackTrace();}}});
    
    //EXPORT
    JButton btnExp = new JButton("Export");
    btnExp.setFont(new Font("Dialog", Font.BOLD, 14));
    //mouse click listener
    btnExp.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        try{
          CQ.renderForExport();
          CQ.export();
        }catch(Exception x){
          x.printStackTrace();}}});
    
    //GENERATE AND EXPORT
    JButton btnGenAndExp = new JButton("G&E");
    btnGenAndExp.setFont(new Font("DejaVu Sans", Font.BOLD, 14));
    //mouse click listener
    btnGenAndExp.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        try{
          CQ.generateAndExport();
        }catch(Exception x){
          x.printStackTrace();}}});
    
    //ROOT BUBBLE MODEL
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    lstRootBubbleModel = new JList();
    lstRootBubbleModel.setBackground(FRCG.BMLIST_COLOR_DEFAULT);
    lstRootBubbleModel.setForeground(FRCG.BMLIST_COLOR_TEXT);
    lstRootBubbleModel.setCellRenderer(new RootBMListCellRenderer());
    lstRootBubbleModel.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    lstRootBubbleModel.setVisibleRowCount(1);  
    lstRootBubbleModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    scrollPane.setViewportView(lstRootBubbleModel);
    lstRootBubbleModel.setToolTipText("Root BubbleModel");
    //selection changed listener
    //when we do it, update the params
    lstRootBubbleModel.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e){
        FRCG.instance.config.setRootBubbleModel(
            (BubbleModel)lstRootBubbleModel.getSelectedValue());}});
    
    //TOP LAYOUT
    GroupLayout gl_panTop = new GroupLayout(panTop);
    gl_panTop.setHorizontalGroup(
      gl_panTop.createParallelGroup(Alignment.LEADING)
        .addGroup(gl_panTop.createSequentialGroup()
          .addGroup(gl_panTop.createParallelGroup(Alignment.LEADING)
            .addGroup(gl_panTop.createSequentialGroup()
              .addComponent(btnGenerate, GroupLayout.PREFERRED_SIZE, 112, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(btnExp, GroupLayout.PREFERRED_SIZE, 112, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(txtExpScale, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(btnGenAndExp, GroupLayout.PREFERRED_SIZE, 112, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spiGenExpCount, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE))
            .addGroup(gl_panTop.createParallelGroup(Alignment.TRAILING, false)
              .addComponent(txtExportDir, Alignment.LEADING)
              .addGroup(Alignment.LEADING, gl_panTop.createSequentialGroup()
                .addComponent(txtDetailLimit, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(cmbSymConFun, GroupLayout.PREFERRED_SIZE, 226, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(cmbRenderer, GroupLayout.PREFERRED_SIZE, 226, GroupLayout.PREFERRED_SIZE))
              .addComponent(txtGrammarPath, Alignment.LEADING)))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE))
    );
    gl_panTop.setVerticalGroup(
      gl_panTop.createParallelGroup(Alignment.LEADING)
        .addGroup(gl_panTop.createSequentialGroup()
          .addContainerGap()
          .addGroup(gl_panTop.createParallelGroup(Alignment.LEADING)
            .addComponent(scrollPane, Alignment.TRAILING)
            .addGroup(gl_panTop.createSequentialGroup()
              .addComponent(txtGrammarPath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(txtExportDir, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addGroup(gl_panTop.createParallelGroup(Alignment.BASELINE)
                .addComponent(txtDetailLimit, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                .addComponent(cmbSymConFun, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                .addComponent(cmbRenderer, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
              .addPreferredGap(ComponentPlacement.RELATED)
              .addGroup(gl_panTop.createParallelGroup(Alignment.BASELINE)
                .addComponent(btnGenerate)
                .addComponent(btnExp)
                .addComponent(btnGenAndExp)
                .addComponent(txtExpScale, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                .addComponent(spiGenExpCount, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))))
          .addContainerGap())
    );
    
    panTop.setLayout(gl_panTop);
    frame.getContentPane().setLayout(groupLayout);
    //avoid textbox grow
    txtGrammarPath.setMaximumSize(new Dimension(568,28));
    txtExportDir.setMaximumSize(new Dimension(568,28));
  }
}
