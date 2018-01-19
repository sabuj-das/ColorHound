/** ---------------------------------------------------------------------------*
 * Copyright Sabuj Das | sabuj.das@gmail.com all rights reserved.
 * <br/>
 * This document cannot be copied, modified or re-distributed without prior 
 * permission from the author.
 *  ---------------------------------------------------------------------------* 
 * Type     : com.gs.tools.colorhound.ui.SettingsDialog
 * Date     : May 28, 2013
 */

package com.gs.tools.colorhound.ui;

import com.gs.tools.colorhound.ApplicationContext;
import com.gs.tools.colorhound.ApplicationSettings;
import com.gs.tools.colorhound.event.AppSettingsChangedEvent;
import com.gs.tools.colorhound.event.ApplicationEventManager;
import com.gs.utils.swing.file.FileBrowserUtil;
import com.gs.utils.swing.window.WindowUtil;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Sabuj Das | sabuj.das@gmail.com
 */
public class SettingsDialog extends javax.swing.JDialog {

    private final ApplicationSettings applicationSettings;
    private ApplicationSettings tempSettings;
    
    /** Creates new form SettingsDialog */
    public SettingsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        tempSettings = new ApplicationSettings();
        applicationSettings = ApplicationContext.getContext()
                .getApplicationSettings();
        tempSettings.copySettings(applicationSettings);
        initComponents();
        WindowUtil.bringCenterTo(this, parent);
        alwaysOnTopCheckBox.setSelected(tempSettings.isAlwaysOnTop());
        closeToHideCheckBox.setSelected(tempSettings.isCloseToHide());
        hideWhenMinimizedCheckBox.setSelected(tempSettings.isHideWhenMinimized());
        promptExitConfirmCheckBox.setSelected(tempSettings.isDoNotShowExitDialog());
        
        appDirTextField.setText(tempSettings.getAppSettingsPath());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        settingsTabbedPane = new javax.swing.JTabbedPane();
        generalSettingsPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        closeToHideCheckBox = new javax.swing.JCheckBox();
        hideWhenMinimizedCheckBox = new javax.swing.JCheckBox();
        promptExitConfirmCheckBox = new javax.swing.JCheckBox();
        alwaysOnTopCheckBox = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        appDirTextField = new javax.swing.JTextField();
        browseAppDirButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        applyButton = new javax.swing.JButton();

        FormListener formListener = new FormListener();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("i18n/Message"); // NOI18N
        setTitle(bundle.getString("lbl.settings.dialog.title")); // NOI18N
        addWindowListener(formListener);

        jLabel5.setText(bundle.getString("lbl.app.dir")); // NOI18N

        closeToHideCheckBox.setSelected(true);
        closeToHideCheckBox.setText(bundle.getString("lbl.close.to.hide.ckhbx")); // NOI18N
        closeToHideCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        closeToHideCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        closeToHideCheckBox.addActionListener(formListener);

        hideWhenMinimizedCheckBox.setSelected(true);
        hideWhenMinimizedCheckBox.setText(bundle.getString("lbl.hide.when.minimized.chkbx")); // NOI18N
        hideWhenMinimizedCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        hideWhenMinimizedCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        hideWhenMinimizedCheckBox.addActionListener(formListener);

        promptExitConfirmCheckBox.setText(bundle.getString("lbl.prompt.exit.confirm.dialog")); // NOI18N
        promptExitConfirmCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        promptExitConfirmCheckBox.addActionListener(formListener);

        alwaysOnTopCheckBox.setSelected(true);
        alwaysOnTopCheckBox.setText(bundle.getString("lbl.always.on.top.chkbx")); // NOI18N
        alwaysOnTopCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        alwaysOnTopCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        alwaysOnTopCheckBox.addActionListener(formListener);

        browseAppDirButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/folder_open_file.png"))); // NOI18N
        browseAppDirButton.setToolTipText(bundle.getString("lbl.browse.app.dir")); // NOI18N
        browseAppDirButton.addActionListener(formListener);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 204));
        jLabel1.setText(bundle.getString("lbl.app.dir.note")); // NOI18N

        javax.swing.GroupLayout generalSettingsPanelLayout = new javax.swing.GroupLayout(generalSettingsPanel);
        generalSettingsPanel.setLayout(generalSettingsPanelLayout);
        generalSettingsPanelLayout.setHorizontalGroup(
            generalSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalSettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(generalSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(generalSettingsPanelLayout.createSequentialGroup()
                        .addGroup(generalSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(alwaysOnTopCheckBox)
                            .addComponent(hideWhenMinimizedCheckBox)
                            .addGroup(generalSettingsPanelLayout.createSequentialGroup()
                                .addComponent(closeToHideCheckBox)
                                .addGap(18, 18, 18)
                                .addComponent(promptExitConfirmCheckBox)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(generalSettingsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(generalSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                            .addComponent(appDirTextField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseAppDirButton)))
                .addContainerGap())
            .addComponent(jSeparator1)
        );

        generalSettingsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {alwaysOnTopCheckBox, closeToHideCheckBox, hideWhenMinimizedCheckBox});

        generalSettingsPanelLayout.setVerticalGroup(
            generalSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalSettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(generalSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(closeToHideCheckBox)
                    .addComponent(promptExitConfirmCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hideWhenMinimizedCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(alwaysOnTopCheckBox)
                .addGap(15, 15, 15)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(generalSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(appDirTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseAppDirButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(69, Short.MAX_VALUE))
        );

        settingsTabbedPane.addTab(bundle.getString("lbl.general.settings.tab.title"), generalSettingsPanel); // NOI18N

        cancelButton.setText(bundle.getString("lbl.cancel.settings.button")); // NOI18N
        cancelButton.addActionListener(formListener);

        applyButton.setText(bundle.getString("lbl.apply.settings.button")); // NOI18N
        applyButton.addActionListener(formListener);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(settingsTabbedPane)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cancelButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(applyButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(settingsTabbedPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(applyButton))
                .addContainerGap())
        );

        pack();
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener, java.awt.event.WindowListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == closeToHideCheckBox) {
                SettingsDialog.this.closeToHideCheckBoxActionPerformed(evt);
            }
            else if (evt.getSource() == hideWhenMinimizedCheckBox) {
                SettingsDialog.this.hideWhenMinimizedCheckBoxActionPerformed(evt);
            }
            else if (evt.getSource() == alwaysOnTopCheckBox) {
                SettingsDialog.this.alwaysOnTopCheckBoxActionPerformed(evt);
            }
            else if (evt.getSource() == applyButton) {
                SettingsDialog.this.applyButtonActionPerformed(evt);
            }
            else if (evt.getSource() == cancelButton) {
                SettingsDialog.this.cancelButtonActionPerformed(evt);
            }
            else if (evt.getSource() == browseAppDirButton) {
                SettingsDialog.this.browseAppDirButtonActionPerformed(evt);
            }
            else if (evt.getSource() == promptExitConfirmCheckBox) {
                SettingsDialog.this.promptExitConfirmCheckBoxActionPerformed(evt);
            }
        }

        public void windowActivated(java.awt.event.WindowEvent evt) {
        }

        public void windowClosed(java.awt.event.WindowEvent evt) {
        }

        public void windowClosing(java.awt.event.WindowEvent evt) {
            if (evt.getSource() == SettingsDialog.this) {
                SettingsDialog.this.formWindowClosing(evt);
            }
        }

        public void windowDeactivated(java.awt.event.WindowEvent evt) {
        }

        public void windowDeiconified(java.awt.event.WindowEvent evt) {
        }

        public void windowIconified(java.awt.event.WindowEvent evt) {
        }

        public void windowOpened(java.awt.event.WindowEvent evt) {
        }
    }// </editor-fold>//GEN-END:initComponents

    private void closeToHideCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeToHideCheckBoxActionPerformed
        if(closeToHideCheckBox.isSelected()){
            tempSettings.setCloseToHide(true);
        } else {
            tempSettings.setCloseToHide(false);
        }
    }//GEN-LAST:event_closeToHideCheckBoxActionPerformed

    private void hideWhenMinimizedCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hideWhenMinimizedCheckBoxActionPerformed
        if(hideWhenMinimizedCheckBox.isSelected()){
            tempSettings.setHideWhenMinimized(true);
        } else {
            tempSettings.setHideWhenMinimized(false);
        }
    }//GEN-LAST:event_hideWhenMinimizedCheckBoxActionPerformed

    private void alwaysOnTopCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alwaysOnTopCheckBoxActionPerformed
        if(alwaysOnTopCheckBox.isSelected()){
            tempSettings.setAlwaysOnTop(true);
        } else {
            tempSettings.setAlwaysOnTop(false);
        }
    }//GEN-LAST:event_alwaysOnTopCheckBoxActionPerformed

    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyButtonActionPerformed
        applicationSettings.copySettings(tempSettings);
        AppSettingsChangedEvent event = new AppSettingsChangedEvent(
                null, applicationSettings, this);
        ApplicationEventManager.getInstance().fireEvent(event);
        dispose();
    }//GEN-LAST:event_applyButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        tempSettings.copySettings(applicationSettings);
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        tempSettings.copySettings(applicationSettings);
        dispose();
    }//GEN-LAST:event_formWindowClosing

    private void browseAppDirButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseAppDirButtonActionPerformed
        File dir = FileBrowserUtil.openSingleFile(this, null, true);
        if(null != dir && dir.exists()){
            try {
                appDirTextField.setText(dir.getCanonicalPath());
            } catch (IOException ex) {
                appDirTextField.setText("");
            }
        }
    }//GEN-LAST:event_browseAppDirButtonActionPerformed

    private void promptExitConfirmCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_promptExitConfirmCheckBoxActionPerformed
        if(promptExitConfirmCheckBox.isSelected()){
            tempSettings.setDoNotShowExitDialog(true);
        } else {
            tempSettings.setDoNotShowExitDialog(false);
        }
    }//GEN-LAST:event_promptExitConfirmCheckBoxActionPerformed

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox alwaysOnTopCheckBox;
    private javax.swing.JTextField appDirTextField;
    private javax.swing.JButton applyButton;
    private javax.swing.JButton browseAppDirButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox closeToHideCheckBox;
    private javax.swing.JPanel generalSettingsPanel;
    private javax.swing.JCheckBox hideWhenMinimizedCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JCheckBox promptExitConfirmCheckBox;
    private javax.swing.JTabbedPane settingsTabbedPane;
    // End of variables declaration//GEN-END:variables

}
