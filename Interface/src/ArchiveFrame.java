package archiveinterface;

import javax.swing.JFrame;
import archive.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.table.DefaultTableModel;
import archive.IOTools;
import java.io.File;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public final class ArchiveFrame extends javax.swing.JFrame {

    Archive MASTER;
    DefaultTableModel tm = null;
    ArrayList<ArchColumn> cols = new ArrayList<>();
    ArrayList<FileEntry> fileOrder = new ArrayList<>();
    
    public ArchiveFrame(Archive archive) {
        MASTER = archive;
        
        initComponents();
        
        //Sets proper editability for cells
        table.setModel(new DefaultTableModel(){
            public boolean isCellEditable(int r, int c){
                if(cols == null){
                    return false;
                }
                return cols.get(c).getEditable();
            }
        });
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        //DEFAULT COLUMN SETUP
        cols.add(ArchColumn.name);
        //cols.add(ArchColumn.filenum);
        cols.add(ArchColumn.heapname);
        //cols.add(ArchColumn.heappos);
        cols.add(ArchColumn.filesize);
        //END DEFAULT COLUMN SETUP
        
        //Setup table
        tm = (DefaultTableModel)table.getModel();
        tm.setColumnIdentifiers(getColNames()); //Set default columns
        
        //Allows detecting when cell changes are made
        tm.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                //TYPE 0 IS ALL THAT MATTERS FOR EDITING
                if(e.getType()==0 && e.getFirstRow()>=0 && e.getColumn()>=0)
                    onCellEdited(e.getFirstRow(),e.getColumn());
            }
        });
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        //Add the files to fileOrder and the table
        for(FileEntry fe : archive.entries){
            fileOrder.add(fe);
            addFileEntry(fe);
        }
        
        setVisible(true);
    }
    
    //Returns an array with column names; to be used with setColumnIdentifiers()
    String[] getColNames(){
        String[] arr = new String[cols.size()];
        for(int i = 0; i < arr.length; i++)
            arr[i] = cols.get(i).getName();
        
        return arr;
    }
    
    //Gets the String array of data for a new column
    String[] getColumnData(ArchColumn ac){
        String[] arr = new String[fileOrder.size()];
        for(int i = 0; i < arr.length; i++)
            arr[i] = ac.getData(fileOrder.get(i));
        
        return arr;
    }
    
    //Gets the string array of data for a new row
    String[] getRowData(FileEntry fe){
        String[] arr = new String[cols.size()];
        for(int i = 0; i < arr.length; i++)
            arr[i] = cols.get(i).getData(fe);
        
        return arr;
    }
    
    //Updates ALL data in the table
    void refreshTable(){
        String[][] data = new String[fileOrder.size()][cols.size()];
        for(int r = 0; r < fileOrder.size(); r++){
            for(int c = 0; c < cols.size(); c++){
                data[r][c] = cols.get(c).getData(fileOrder.get(r));
            }
        }
        
        tm.setDataVector(data, getColNames());
    }
    
    //Adds a FileEntry to the table automatically gathering data
    void addFileEntry(FileEntry fe){
        tm.addRow(getRowData(fe));
    }
    
    //Adds a column to the table and fills it in
    void addColumn(ArchColumn ac){
        cols.add(ac);
        tm.addColumn(ac.getName(), getColumnData(ac));
    }
    
    //Removes a column
    void delColumn(int col){
        if(col<0)
            return;
        
        cols.remove(col);
        refreshTable();
    }
    
    //Called when a cell is edited
    void onCellEdited(int r, int c){
        FileEntry fe = fileOrder.get(r);
        cols.get(c).setData(fe, (String)tm.getValueAt(r, c));
    }
    
    //Sorts designated row by the specified type (bubble sort xd)
        //0: String
        //1: Number
    boolean sortDirection = false;
    void sortRows(int col, int type){
        if(col<0)
            return;
        
        for(int i = 0; i < tm.getRowCount()-1; i++){
            for(int j = 0; j < tm.getRowCount()-1; j++){
                String data = (String)tm.getValueAt(j, col);
                String data2 = (String)tm.getValueAt(j+1, col);
                
                boolean swap = false;
                boolean mustswap = false; //Used to push non-number entries towards the back regardless of sort direction
                
                if(type==0){
                    //String mode
                    if(data.compareToIgnoreCase(data2)>0)
                        swap = true;
                }else if(type == 1){
                    //Number mode
                    double num = Double.POSITIVE_INFINITY;
                    double num2 = Double.POSITIVE_INFINITY;
                    try{
                        num = Double.parseDouble(data);
                        num2 = Double.parseDouble(data2);
                        }catch(Exception e){mustswap = true;}
                        if(num>num2)
                            swap = true;
                    
                }
                
                if(sortDirection && !mustswap) //Reverses sort direction
                    swap = !swap;
                
                if(swap){
                    //Swap the FileEntry in the array
                    FileEntry tmp = fileOrder.get(j);
                    fileOrder.set(j, fileOrder.get(j+1));
                    fileOrder.set(j+1,tmp);
                    
                    tm.moveRow(j, j, j+1); //Swap the table rows
                }
            }
        }
        sortDirection = !sortDirection; //Swaps the direction for the next sort
    }
    
    //Attempts to open selected file(s) as an image (curwin = whether to open a new window or use an existing one)
    ViewImageFrame lastvif = null;
    void viewImage(boolean curwin){
        try{
            int[] selected = table.getSelectedRows();
            if(selected.length<=0)
                return; //Nothing selected
            for(int i : selected){
                FileEntry fe = fileOrder.get(i);
                byte[] data = MASTER.extractFromHeap(fe.heapnum, fe.pos, fe.len);
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                BufferedImage bi = ImageIO.read(bais);
                bais.close();
                if(curwin && lastvif != null && lastvif.isVisible()){
                    lastvif.updateImage(fe.name,bi);
                }
                else{
                    lastvif = new ViewImageFrame(fe.name,bi);
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }
    
    //Attempts to open selected file as a plain text file (curwin = whether to open a new window or use an existing one)
    ViewTextFileFrame lastvtff = null;
    void viewTextFile(boolean curwin){
        try{
            int[] selected = table.getSelectedRows();
            if(selected.length<=0)
                return; //Nothing selected
            for(int i : selected){
                FileEntry fe = fileOrder.get(i);
                byte[] data = MASTER.extractFromHeap(fe.heapnum, fe.pos, fe.len);
                String text = IOTools.byte2str(data);
                if(curwin && lastvtff != null && lastvtff.isVisible()){
                    lastvtff.updateText(fe.name,text);
                }
                else{
                    lastvtff = new ViewTextFileFrame(fe.name,text);
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }
    
    //Completely reload all data in the table
    void reloadTable(){
        fileOrder.clear();
        for(FileEntry fe : MASTER.entries)
            fileOrder.add(fe);
        refreshTable();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        AddFileButton = new javax.swing.JButton();
        DeleteButton = new javax.swing.JButton();
        SortStringButton = new javax.swing.JButton();
        SortNumButton = new javax.swing.JButton();
        ViewTextButton = new javax.swing.JButton();
        ViewImgButton = new javax.swing.JButton();
        SaveButton = new javax.swing.JButton();
        AddColButton = new javax.swing.JButton();
        DelColButton = new javax.swing.JButton();
        reloadButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        table.setColumnSelectionAllowed(true);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tableMouseReleased(evt);
            }
        });
        table.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tablePropertyChange(evt);
            }
        });
        jScrollPane2.setViewportView(table);
        table.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        AddFileButton.setText("Add File(s)");
        AddFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddFileButtonActionPerformed(evt);
            }
        });

        DeleteButton.setText("Delete");
        DeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteButtonActionPerformed(evt);
            }
        });

        SortStringButton.setText("Sort By String");
        SortStringButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SortStringButtonActionPerformed(evt);
            }
        });

        SortNumButton.setText("Sort By Num");
        SortNumButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SortNumButtonActionPerformed(evt);
            }
        });

        ViewTextButton.setText("View As Text");
        ViewTextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ViewTextButtonActionPerformed(evt);
            }
        });

        ViewImgButton.setText("View As Image");
        ViewImgButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ViewImgButtonActionPerformed(evt);
            }
        });

        SaveButton.setText("Save");
        SaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveButtonActionPerformed(evt);
            }
        });

        AddColButton.setText("Add Column");
        AddColButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddColButtonActionPerformed(evt);
            }
        });

        DelColButton.setText("Delete Column");
        DelColButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DelColButtonActionPerformed(evt);
            }
        });

        reloadButton.setText("Reload");
        reloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(AddFileButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(SortStringButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ViewTextButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(SaveButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(AddColButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ViewImgButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(SortNumButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(DeleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(DelColButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(reloadButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(AddFileButton)
                            .addComponent(DeleteButton))
                        .addGap(87, 87, 87)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ViewTextButton)
                            .addComponent(ViewImgButton))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(SortStringButton)
                            .addComponent(SortNumButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(AddColButton)
                            .addComponent(DelColButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(SaveButton)
                            .addComponent(reloadButton)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tablePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tablePropertyChange
        //How to get rid of...
    }//GEN-LAST:event_tablePropertyChange

    private void tableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableMouseReleased
        //Ahh, useless
        //viewTextFile(true);
    }//GEN-LAST:event_tableMouseReleased

    private void SortStringButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SortStringButtonActionPerformed
        sortRows(table.getSelectedColumn(), 0);
    }//GEN-LAST:event_SortStringButtonActionPerformed

    private void AddFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddFileButtonActionPerformed
        Deque<File> files = new LinkedList<>(); //Stack to hold files
        Deque<File> folders = new LinkedList<>(); //Stack to hold folders
        boolean recurse = false; //Whether to recurse through folders, will ask user later
        //Let user select a file
        String wd = System.getProperty("user.dir"); //Get current directory
        JFileChooser jfc = new JFileChooser(wd);
        
        jfc.setMultiSelectionEnabled(true);
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jfc.showOpenDialog(null); //Ask for file

        File[] f = jfc.getSelectedFiles(); //Get the chosen files
        boolean answered = false; //Have we prompted them about recursion?
        for(File fi : f){ //Go through selected files and distribute them
            if(!fi.exists())
                continue; //Only consider real files
            if(fi.isDirectory())
                folders.push(fi); //Is a directory, add to folders
            else
                files.push(fi); //Otherwise add to files
        }
        
        while(!folders.isEmpty()){ //Go through the folders
            File fi = folders.pop();
            for(File ftmp : fi.listFiles()){ //Go through each file in folder
                if(ftmp.isDirectory() && !answered){ //Check if they want recursion if a subfolder is detected
                    //Ask if they want to recurse
                    recurse = JOptionPane.showConfirmDialog(null, "Subfolders found, Recurse through folders?", "Subfolders found", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION;
                    System.out.println("recursion is now: "+recurse);
                    answered = true; //They have answered
                }
                if(recurse && ftmp.isDirectory())
                    folders.push(ftmp); //Recursion is on, add to folders
                else if(!ftmp.isDirectory())
                    files.push(ftmp); //Add to files
            }
        }
        
        //Go through and add files to the archive and table
        for(File fi : files){
            FileEntry fe = MASTER.insertFile(fi.getAbsolutePath());
            fileOrder.add(fe);
        }
        refreshTable();
    }//GEN-LAST:event_AddFileButtonActionPerformed

    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
        int[] rows = table.getSelectedRows();
        Arrays.sort(rows);
        for(int i = rows.length-1; i >=0; i--){
            FileEntry fe = fileOrder.get(i);
            MASTER.deleteFile(fe); //Delete from archive
            fileOrder.remove(i); //Remove from frame data
            refreshTable(); //Redraw
        }
    }//GEN-LAST:event_DeleteButtonActionPerformed

    private void SortNumButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SortNumButtonActionPerformed
        sortRows(table.getSelectedColumn(), 1);
    }//GEN-LAST:event_SortNumButtonActionPerformed

    private void ViewTextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ViewTextButtonActionPerformed
        viewTextFile(false);
    }//GEN-LAST:event_ViewTextButtonActionPerformed

    private void ViewImgButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ViewImgButtonActionPerformed
        viewImage(false);
    }//GEN-LAST:event_ViewImgButtonActionPerformed

    private void SaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveButtonActionPerformed
        MASTER.SAVE();
    }//GEN-LAST:event_SaveButtonActionPerformed

    private void AddColButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddColButtonActionPerformed
        new AddColumnFrame(this);
    }//GEN-LAST:event_AddColButtonActionPerformed

    private void DelColButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DelColButtonActionPerformed
        delColumn(table.getSelectedColumn());
    }//GEN-LAST:event_DelColButtonActionPerformed

    private void reloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadButtonActionPerformed
        reloadTable();
    }//GEN-LAST:event_reloadButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddColButton;
    private javax.swing.JButton AddFileButton;
    private javax.swing.JButton DelColButton;
    private javax.swing.JButton DeleteButton;
    private javax.swing.JButton SaveButton;
    private javax.swing.JButton SortNumButton;
    private javax.swing.JButton SortStringButton;
    private javax.swing.JButton ViewImgButton;
    private javax.swing.JButton ViewTextButton;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton reloadButton;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
