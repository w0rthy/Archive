package archiveinterface;

import javax.swing.JFrame;
import javax.swing.JRadioButton;

public class AddColumnFrame extends javax.swing.JFrame {

    ArchiveFrame MASTER;
    
    int NUMBUTTONS = 7;
    JRadioButton[] buttons;
    
    public AddColumnFrame(ArchiveFrame frame) {
        initComponents();
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        MASTER = frame;
        buttons = new JRadioButton[] {fileNameButton,fileNumButton,dheapNumButton,dheapNameButton,fileSizeButton,filePosButton,propertyButton};
        
        setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileNameButton = new javax.swing.JRadioButton();
        fileNumButton = new javax.swing.JRadioButton();
        dheapNameButton = new javax.swing.JRadioButton();
        dheapNumButton = new javax.swing.JRadioButton();
        fileSizeButton = new javax.swing.JRadioButton();
        filePosButton = new javax.swing.JRadioButton();
        propertyButton = new javax.swing.JRadioButton();
        propertyField = new javax.swing.JTextField();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setUndecorated(true);
        setResizable(false);

        fileNameButton.setText("File Name");
        fileNameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileNameButtonActionPerformed(evt);
            }
        });

        fileNumButton.setText("File Number");
        fileNumButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileNumButtonActionPerformed(evt);
            }
        });

        dheapNameButton.setText("DataHeap Name");
        dheapNameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dheapNameButtonActionPerformed(evt);
            }
        });

        dheapNumButton.setText("DataHeap Number");
        dheapNumButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dheapNumButtonActionPerformed(evt);
            }
        });

        fileSizeButton.setText("File Size");
        fileSizeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileSizeButtonActionPerformed(evt);
            }
        });

        filePosButton.setText("File Pos");
        filePosButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filePosButtonActionPerformed(evt);
            }
        });

        propertyButton.setSelected(true);
        propertyButton.setText("Property");
        propertyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                propertyButtonActionPerformed(evt);
            }
        });

        okButton.setText("Add");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Done");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fileNameButton)
                    .addComponent(fileNumButton)
                    .addComponent(dheapNameButton)
                    .addComponent(dheapNumButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fileSizeButton)
                    .addComponent(filePosButton)
                    .addComponent(propertyButton)
                    .addComponent(propertyField)
                    .addComponent(okButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fileNameButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fileNumButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dheapNameButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dheapNumButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fileSizeButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filePosButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(propertyButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(propertyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(okButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /*0: fileName
    1: fileNum
    2: dheapNum
    3: dheapName
    4: fileSize
    5: filePos
    6: property
    */
    int currentselection = 6;
    void onCheck(int button){
        currentselection = button;
        for(int i = 0; i < NUMBUTTONS; i++){
            if(i==button)
                continue;
            buttons[i].setSelected(false);
        }
        buttons[button].setSelected(true);
    }
    
    private void fileNameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileNameButtonActionPerformed
        onCheck(0);
    }//GEN-LAST:event_fileNameButtonActionPerformed

    private void fileNumButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileNumButtonActionPerformed
        onCheck(1);
    }//GEN-LAST:event_fileNumButtonActionPerformed

    private void dheapNumButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dheapNumButtonActionPerformed
        onCheck(2);
    }//GEN-LAST:event_dheapNumButtonActionPerformed

    private void dheapNameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dheapNameButtonActionPerformed
        onCheck(3);
    }//GEN-LAST:event_dheapNameButtonActionPerformed

    private void fileSizeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileSizeButtonActionPerformed
        onCheck(4);
    }//GEN-LAST:event_fileSizeButtonActionPerformed

    private void filePosButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filePosButtonActionPerformed
        onCheck(5);
    }//GEN-LAST:event_filePosButtonActionPerformed

    private void propertyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_propertyButtonActionPerformed
        onCheck(6);
    }//GEN-LAST:event_propertyButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        switch(currentselection){
            case 0:
                MASTER.addColumn(ArchColumn.name);break;
            case 1:
                MASTER.addColumn(ArchColumn.filenum);break;
            case 2:
                MASTER.addColumn(ArchColumn.heapnum);break;
            case 3:
                MASTER.addColumn(ArchColumn.heapname);break;
            case 4:
                MASTER.addColumn(ArchColumn.filesize);break;
            case 5:
                MASTER.addColumn(ArchColumn.heappos);break;
            case 6:
                MASTER.addColumn(ArchColumn.genPropCol(propertyField.getText()));break;
        }
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JRadioButton dheapNameButton;
    private javax.swing.JRadioButton dheapNumButton;
    private javax.swing.JRadioButton fileNameButton;
    private javax.swing.JRadioButton fileNumButton;
    private javax.swing.JRadioButton filePosButton;
    private javax.swing.JRadioButton fileSizeButton;
    private javax.swing.JButton okButton;
    private javax.swing.JRadioButton propertyButton;
    private javax.swing.JTextField propertyField;
    // End of variables declaration//GEN-END:variables
}
