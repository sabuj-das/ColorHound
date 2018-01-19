/** ---------------------------------------------------------------------------*
 * Copyright Sabuj Das | sabuj.das@gmail.com all rights reserved.
 * <br/>
 * This document cannot be copied, modified or re-distributed without prior 
 * permission from the author.
 *  ---------------------------------------------------------------------------* 
 * Type     : com.gs.tools.colorhound.ui.ColorHoundBaseFrame
 * Date     : May 23, 2013
 */

package com.gs.tools.colorhound.ui;

import com.gs.tools.colorhound.ApplicationContext;
import com.gs.tools.colorhound.ColorData;
import com.gs.tools.colorhound.ColorPalette;
import com.gs.tools.colorhound.WorkerTaskConstants;
import com.gs.tools.colorhound.event.AppSettingsChangedEvent;
import com.gs.tools.colorhound.event.AppSettingsChangedListener;
import com.gs.tools.colorhound.event.ApplicationEventManager;
import com.gs.tools.colorhound.event.ColorDetectEvent;
import com.gs.tools.colorhound.event.ColorDetectListener;
import com.gs.tools.colorhound.event.ColorGrabEvent;
import com.gs.tools.colorhound.event.ColorGrabListener;
import com.gs.tools.colorhound.event.ColorPanelSelectedEvent;
import com.gs.tools.colorhound.event.ColorPanelSelectedEventListener;
import com.gs.tools.colorhound.event.ExternalEventListener;
import com.gs.tools.colorhound.event.MouseInfoChangeEvent;
import com.gs.tools.colorhound.event.MouseInfoChangedListener;
import com.gs.tools.colorhound.util.GraphicsUtil;
import com.gs.utils.enums.DisplayTypeEnum;
import com.gs.utils.swing.display.DisplayUtils;
import com.gs.utils.swing.file.ExtensionFileFilter;
import com.gs.utils.swing.file.FileBrowserUtil;
import com.gs.utils.swing.window.WindowUtil;
import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Sabuj Das | sabuj.das@gmail.com
 */
public class ColorHoundBaseFrame extends javax.swing.JFrame 
    implements ColorGrabListener, ColorPanelSelectedEventListener,
        AppSettingsChangedListener, ClipboardOwner, ColorDetectListener,
        MouseInfoChangedListener, PropertyChangeListener{

    private final ImageIcon frameIcon = new ImageIcon(getClass().getResource(
               "/images/color-hound-24x24_1.png"));
    private static ApplicationContext appContext 
            = ApplicationContext.getContext();
    private static ApplicationEventManager eventManager 
            = ApplicationEventManager.getInstance();
    private final ActionListener exitListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exitMenuItemActionPerformed(e);
            }
        };
    private static final int PALETTE_CONTENT_MAX_WIDTH = 1005;
    public static final String ENLARGE_RATIO_SLIDER = "ENLARGE_RATIO_SLIDER";
    
    private ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n/Message");
    private static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();
    //private MouseWatcher mouseWatcher = new MouseWatcher();
    private Timer mouseWatchTimer = new Timer();
    private boolean enlargePanelEnabled = true;
    private EnlargedImagePanel enlargedImagePanel;
    private long timerPeriod = 500;
    private TextScanWorker textScanWorker;
    
    /** Creates new form ColorHoundBaseFrame */
    public ColorHoundBaseFrame() {
        initComponents();
        ratioSlider.setValue(14);
        enlargedImagePanel = new EnlargedImagePanel();
        ratioSlider.addChangeListener(enlargedImagePanel);
        enlargedPanel.add(enlargedImagePanel, BorderLayout.CENTER);
        setIconImage(frameIcon.getImage());
        textScanProgressBar.setVisible(false);
        colorPickedJList.setModel(
                    new ColorPaletteListModel(new ArrayList<ColorData>(0)));
        colorPickedJList.setCellRenderer(new ColorPaletteListCellRenderer());
        
        WindowUtil.bringToCenter(this);
        ExternalEventListener externalEventListener
                = new ExternalEventListener();
        TOOLKIT.addAWTEventListener(
            externalEventListener, 
            AWTEvent.KEY_EVENT_MASK + AWTEvent.MOUSE_EVENT_MASK
                + AWTEvent.MOUSE_MOTION_EVENT_MASK);
        
        
        
        ApplicationEventManager.getInstance().registerListener(
                ColorPanelSelectedEvent.class, this);
        ApplicationEventManager.getInstance().registerListener(
                ColorDetectEvent.class, this);
        ApplicationEventManager.getInstance().registerListener(
                ColorGrabEvent.class, this);
        ApplicationEventManager.getInstance().registerListener(
                AppSettingsChangedEvent.class, this);
        ApplicationEventManager.getInstance().registerListener(
                MouseInfoChangeEvent.class, this);
        
        appContext.load();
        List<ColorPalette> colorPalettes = appContext.getColorPalettes();
        if(null != colorPalettes && colorPalettes.size() > 0){
            for (ColorPalette colorPalette : colorPalettes) {
                ColorPaletteManager.getInstance().addPalette(
                        colorPalette.getName());
                paletteListComboBox.addItem(colorPalette.getName());
                List<ColorData> colorDatas = colorPalette.getColorDataList();
                if(null != colorDatas && colorDatas.size() > 0){
                    for (ColorData colorData : colorDatas) {
                        Color color = Color.decode(colorData.getColorCode());
                        ColorPanel colorPanel = new ColorPanel(paletteContentPanel, 
                            colorPalette.getName());
                        colorPanel.setSelected(false);
                        colorPanel.setColorGrabbed(true);
                        colorPanel.setSelectedColor(color);
                        ColorPaletteManager.getInstance().addPanel(
                                colorPalette.getName(), colorPanel);
                    }
                } 
            }
            paletteListComboBox.setSelectedIndex(0);
            resizePaletteContentPanel();
            paletteContentPanel.updateUI();
        }
        
        setAlwaysOnTop(appContext.getApplicationSettings().isAlwaysOnTop());
        
        redRgbTextField.getDocument().addDocumentListener(
                new DocumentChangeListener(rgbCopyButton, redRgbTextField));
        greenRgbTextField.getDocument().addDocumentListener(
                new DocumentChangeListener(rgbCopyButton, greenRgbTextField));
        blueRgbTextField.getDocument().addDocumentListener(
                new DocumentChangeListener(rgbCopyButton, blueRgbTextField));
        
        hexColorTextField.getDocument().addDocumentListener(
                new DocumentChangeListener(hexCopyButton, hexColorTextField));
        cssRgbTextField.getDocument().addDocumentListener(
                new DocumentChangeListener(cssCopyButton, cssRgbTextField));
        
        
        DocumentChangeListener sourceTextDocumentChangeListener
                = new DocumentChangeListener(scanTextButton, 
                sourceTextPaletteNameTextField, sourceTextTextArea);
        sourceTextPaletteNameTextField.getDocument().addDocumentListener(
                sourceTextDocumentChangeListener);
        sourceTextTextArea.getDocument().addDocumentListener(
                sourceTextDocumentChangeListener);
    }

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        baseToolBar = new javax.swing.JToolBar();
        jLabel5 = new javax.swing.JLabel();
        paletteListComboBox = new javax.swing.JComboBox();
        addPaletteButton = new javax.swing.JButton();
        deletePaletteButton = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        baseContentPanel = new javax.swing.JPanel();
        baseSplitPane = new javax.swing.JSplitPane();
        topPanel = new javax.swing.JPanel();
        leftPanel = new javax.swing.JPanel();
        paletteToolBar = new javax.swing.JToolBar();
        selectedPaletteNameLabel = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        addColorButton = new javax.swing.JButton();
        editColorButton = new javax.swing.JButton();
        deleteColorButton = new javax.swing.JButton();
        paletteContentScrollPane = new javax.swing.JScrollPane();
        paletteContentPanel = new javax.swing.JPanel();
        colorDetailsPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        redRgbTextField = new javax.swing.JTextField();
        greenRgbTextField = new javax.swing.JTextField();
        blueRgbTextField = new javax.swing.JTextField();
        cssRgbTextField = new javax.swing.JTextField();
        rgbCopyButton = new javax.swing.JButton();
        hexCopyButton = new javax.swing.JButton();
        cssCopyButton = new javax.swing.JButton();
        hexColorTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JSeparator();
        jLabel11 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        enlargedPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        ratioSlider = new javax.swing.JSlider();
        ratioLabel = new javax.swing.JLabel();
        colorSourceTabbedPane = new javax.swing.JTabbedPane();
        imageViewerPanel = new javax.swing.JPanel();
        imageControlToolBar = new javax.swing.JToolBar();
        openImageButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        captureDesktopButton = new javax.swing.JButton();
        imageContainerPanel = new javax.swing.JPanel();
        textPanel = new javax.swing.JPanel();
        textPanelToolBar = new javax.swing.JToolBar();
        openTextFileButton = new javax.swing.JButton();
        cleatTextButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        sourceTextTextArea = new javax.swing.JTextArea();
        jLabel9 = new javax.swing.JLabel();
        sourceTextPaletteNameTextField = new javax.swing.JTextField();
        textScanProgressBar = new javax.swing.JProgressBar();
        scanTextButton = new javax.swing.JButton();
        sourceTextUseSamePaletteChkbx = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        colorPickedJList = new javax.swing.JList();
        logContentPanel = new javax.swing.JPanel();
        baseMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        saveMenuItem = new javax.swing.JMenuItem();
        savePaletteAsMenuItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        importPalettesMenuItem = new javax.swing.JMenuItem();
        exportPalettesMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        hideMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        undoMenuItem = new javax.swing.JMenuItem();
        redoMenuItem = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        renameMenuItem = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        settingsMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        enableEnlargeAreaChkMenuItem = new javax.swing.JCheckBoxMenuItem();
        enableRealtimeViewChkMenuItem = new javax.swing.JCheckBoxMenuItem();

        FormListener formListener = new FormListener();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("i18n/Message"); // NOI18N
        setTitle(bundle.getString("lbl.frame.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(730, 480));
        setPreferredSize(new java.awt.Dimension(730, 480));
        addWindowListener(formListener);
        addKeyListener(formListener);

        baseToolBar.setFloatable(false);
        baseToolBar.setRollover(true);

        jLabel5.setText(bundle.getString("lbl.palette.list")); // NOI18N
        baseToolBar.add(jLabel5);

        paletteListComboBox.setMaximumSize(new java.awt.Dimension(280, 32767));
        paletteListComboBox.setMinimumSize(new java.awt.Dimension(100, 20));
        paletteListComboBox.setPreferredSize(new java.awt.Dimension(100, 20));
        paletteListComboBox.addItemListener(formListener);
        paletteListComboBox.addActionListener(formListener);
        paletteListComboBox.addPropertyChangeListener(formListener);
        baseToolBar.add(paletteListComboBox);

        addPaletteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new_palette.png"))); // NOI18N
        addPaletteButton.setToolTipText(bundle.getString("tip.add.palette.button")); // NOI18N
        addPaletteButton.setFocusable(false);
        addPaletteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addPaletteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addPaletteButton.addActionListener(formListener);
        baseToolBar.add(addPaletteButton);

        deletePaletteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete_palette.png"))); // NOI18N
        deletePaletteButton.setToolTipText(bundle.getString("tip.delete.palette.button")); // NOI18N
        deletePaletteButton.setEnabled(false);
        deletePaletteButton.setFocusable(false);
        deletePaletteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deletePaletteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        deletePaletteButton.addActionListener(formListener);
        baseToolBar.add(deletePaletteButton);
        baseToolBar.add(jSeparator8);

        getContentPane().add(baseToolBar, java.awt.BorderLayout.PAGE_START);

        baseSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        baseSplitPane.addComponentListener(formListener);
        baseSplitPane.addPropertyChangeListener(formListener);

        leftPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("lbl.palette.panel.header"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(153, 153, 255))); // NOI18N

        paletteToolBar.setFloatable(false);
        paletteToolBar.setRollover(true);

        selectedPaletteNameLabel.setMaximumSize(new java.awt.Dimension(100, 20));
        selectedPaletteNameLabel.setMinimumSize(new java.awt.Dimension(20, 20));
        selectedPaletteNameLabel.setPreferredSize(new java.awt.Dimension(20, 20));
        paletteToolBar.add(selectedPaletteNameLabel);

        jLabel8.setForeground(new java.awt.Color(0, 51, 204));
        jLabel8.setText(" >> ");
        paletteToolBar.add(jLabel8);

        addColorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add_color.png"))); // NOI18N
        addColorButton.setText(bundle.getString("lbl.add.color.button")); // NOI18N
        addColorButton.setToolTipText(bundle.getString("tip.add.color.button")); // NOI18N
        addColorButton.setEnabled(false);
        addColorButton.setFocusable(false);
        addColorButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        addColorButton.addActionListener(formListener);
        paletteToolBar.add(addColorButton);

        editColorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/edit_color.png"))); // NOI18N
        editColorButton.setText(bundle.getString("lbl.edit.color.button")); // NOI18N
        editColorButton.setToolTipText(bundle.getString("tip.edit.selected.color.button")); // NOI18N
        editColorButton.setEnabled(false);
        editColorButton.setFocusable(false);
        editColorButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        editColorButton.addActionListener(formListener);
        paletteToolBar.add(editColorButton);

        deleteColorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete_color.png"))); // NOI18N
        deleteColorButton.setText(bundle.getString("lbl.delete.color.button")); // NOI18N
        deleteColorButton.setToolTipText(bundle.getString("tip.delete.color.button")); // NOI18N
        deleteColorButton.setEnabled(false);
        deleteColorButton.setFocusable(false);
        deleteColorButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        deleteColorButton.addActionListener(formListener);
        paletteToolBar.add(deleteColorButton);

        paletteContentPanel.setBackground(new java.awt.Color(0, 0, 0));
        paletteContentPanel.addMouseListener(formListener);
        paletteContentPanel.addKeyListener(formListener);
        paletteContentPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        paletteContentScrollPane.setViewportView(paletteContentPanel);

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paletteToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
            .addComponent(paletteContentScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addComponent(paletteToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(paletteContentScrollPane))
        );

        jLabel1.setText("Red");

        jLabel2.setText("Green");

        jLabel3.setText("Blue");

        jLabel4.setText("HEX");

        redRgbTextField.setEditable(false);
        redRgbTextField.setFont(new java.awt.Font("Monospaced", 1, 11)); // NOI18N
        redRgbTextField.setForeground(java.awt.Color.red);
        redRgbTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        redRgbTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        redRgbTextField.setMinimumSize(new java.awt.Dimension(50, 20));
        redRgbTextField.setPreferredSize(new java.awt.Dimension(50, 20));

        greenRgbTextField.setEditable(false);
        greenRgbTextField.setFont(new java.awt.Font("Monospaced", 1, 11)); // NOI18N
        greenRgbTextField.setForeground(java.awt.Color.green);
        greenRgbTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        greenRgbTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        greenRgbTextField.setMinimumSize(new java.awt.Dimension(50, 20));
        greenRgbTextField.setPreferredSize(new java.awt.Dimension(50, 20));

        blueRgbTextField.setEditable(false);
        blueRgbTextField.setFont(new java.awt.Font("Monospaced", 1, 11)); // NOI18N
        blueRgbTextField.setForeground(java.awt.Color.blue);
        blueRgbTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        blueRgbTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        blueRgbTextField.setMinimumSize(new java.awt.Dimension(50, 20));
        blueRgbTextField.setPreferredSize(new java.awt.Dimension(50, 20));

        cssRgbTextField.setEditable(false);
        cssRgbTextField.setFont(new java.awt.Font("Monospaced", 1, 11)); // NOI18N
        cssRgbTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cssRgbTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        cssRgbTextField.addActionListener(formListener);

        rgbCopyButton.setText(bundle.getString("lbl.copy.button")); // NOI18N
        rgbCopyButton.setEnabled(false);
        rgbCopyButton.addActionListener(formListener);

        hexCopyButton.setText(bundle.getString("lbl.copy.button")); // NOI18N
        hexCopyButton.setEnabled(false);
        hexCopyButton.addActionListener(formListener);

        cssCopyButton.setText(bundle.getString("lbl.copy.button")); // NOI18N
        cssCopyButton.setEnabled(false);
        cssCopyButton.addActionListener(formListener);

        hexColorTextField.setEditable(false);
        hexColorTextField.setFont(new java.awt.Font("Monospaced", 1, 11)); // NOI18N
        hexColorTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        hexColorTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        hexColorTextField.addActionListener(formListener);

        jLabel7.setText("CSS");

        jLabel11.setText("jLabel11");

        javax.swing.GroupLayout colorDetailsPanelLayout = new javax.swing.GroupLayout(colorDetailsPanel);
        colorDetailsPanel.setLayout(colorDetailsPanelLayout);
        colorDetailsPanelLayout.setHorizontalGroup(
            colorDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, colorDetailsPanelLayout.createSequentialGroup()
                .addGroup(colorDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator9, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(colorDetailsPanelLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(colorDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(colorDetailsPanelLayout.createSequentialGroup()
                                .addGroup(colorDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel4))
                                .addGap(2, 2, 2)
                                .addGroup(colorDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(hexColorTextField)
                                    .addComponent(cssRgbTextField))
                                .addGap(2, 2, 2)
                                .addGroup(colorDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(hexCopyButton, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(cssCopyButton, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addGroup(colorDetailsPanelLayout.createSequentialGroup()
                                .addGroup(colorDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(redRgbTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(colorDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(greenRgbTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, colorDetailsPanelLayout.createSequentialGroup()
                                        .addGap(1, 1, 1)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(colorDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(blueRgbTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rgbCopyButton)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(2, 2, 2))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, colorDetailsPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        colorDetailsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel4, jLabel7});

        colorDetailsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cssCopyButton, hexCopyButton, rgbCopyButton});

        colorDetailsPanelLayout.setVerticalGroup(
            colorDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(colorDetailsPanelLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(colorDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(redRgbTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(greenRgbTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(blueRgbTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rgbCopyButton))
                .addGap(4, 4, 4)
                .addGroup(colorDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(colorDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hexColorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(hexCopyButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(colorDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cssRgbTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(cssCopyButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        colorDetailsPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {blueRgbTextField, cssCopyButton, cssRgbTextField, greenRgbTextField, hexColorTextField, hexCopyButton, jLabel4, jLabel7, redRgbTextField, rgbCopyButton});

        colorDetailsPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3});

        enlargedPanel.setBackground(new java.awt.Color(255, 255, 255));
        enlargedPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        enlargedPanel.setMaximumSize(new java.awt.Dimension(200, 100));
        enlargedPanel.setMinimumSize(new java.awt.Dimension(200, 100));
        enlargedPanel.setPreferredSize(new java.awt.Dimension(200, 100));
        enlargedPanel.setLayout(new java.awt.BorderLayout());

        jLabel6.setText("Size");

        ratioSlider.setMajorTickSpacing(4);
        ratioSlider.setMaximum(32);
        ratioSlider.setMinimum(2);
        ratioSlider.setMinorTickSpacing(2);
        ratioSlider.setPaintTicks(true);
        ratioSlider.setSnapToTicks(true);
        ratioSlider.addChangeListener(formListener);

        ratioLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        ratioLabel.setText("2x");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ratioSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ratioLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(enlargedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jLabel6)
                        .addGap(0, 6, Short.MAX_VALUE))
                    .addComponent(ratioSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(ratioLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2)
                .addComponent(enlargedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        ratioSlider.getAccessibleContext().setAccessibleName(ENLARGE_RATIO_SLIDER);

        javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(colorDetailsPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(topPanelLayout.createSequentialGroup()
                        .addComponent(colorDetailsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        baseSplitPane.setTopComponent(topPanel);

        colorSourceTabbedPane.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        imageViewerPanel.setLayout(new java.awt.BorderLayout());

        imageControlToolBar.setFloatable(false);
        imageControlToolBar.setRollover(true);

        openImageButton.setText(bundle.getString("lbl.open.image.button")); // NOI18N
        openImageButton.setFocusable(false);
        openImageButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openImageButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openImageButton.addActionListener(formListener);
        imageControlToolBar.add(openImageButton);
        imageControlToolBar.add(jSeparator3);

        captureDesktopButton.setText(bundle.getString("lbl.capture.desktop.button")); // NOI18N
        captureDesktopButton.setFocusable(false);
        captureDesktopButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        captureDesktopButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        captureDesktopButton.addActionListener(formListener);
        imageControlToolBar.add(captureDesktopButton);

        imageViewerPanel.add(imageControlToolBar, java.awt.BorderLayout.PAGE_START);

        imageContainerPanel.setMaximumSize(new java.awt.Dimension(1280, 2147483647));
        imageContainerPanel.setLayout(new java.awt.BorderLayout());
        imageViewerPanel.add(imageContainerPanel, java.awt.BorderLayout.CENTER);

        colorSourceTabbedPane.addTab(bundle.getString("lbl.image.tab.title"), imageViewerPanel); // NOI18N

        textPanelToolBar.setFloatable(false);
        textPanelToolBar.setRollover(true);

        openTextFileButton.setText(bundle.getString("lbl.open.text.file.button")); // NOI18N
        openTextFileButton.setFocusable(false);
        openTextFileButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        openTextFileButton.setInheritsPopupMenu(true);
        openTextFileButton.addActionListener(formListener);
        textPanelToolBar.add(openTextFileButton);

        cleatTextButton.setText(bundle.getString("lbl.clear.text.area.button")); // NOI18N
        cleatTextButton.setFocusable(false);
        cleatTextButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cleatTextButton.addActionListener(formListener);
        textPanelToolBar.add(cleatTextButton);

        sourceTextTextArea.setColumns(20);
        sourceTextTextArea.setRows(5);
        jScrollPane1.setViewportView(sourceTextTextArea);

        jLabel9.setText(bundle.getString("lbl.palette.nale.prompt")); // NOI18N

        sourceTextPaletteNameTextField.setEditable(false);

        textScanProgressBar.setFocusable(false);
        textScanProgressBar.setIndeterminate(true);
        textScanProgressBar.setString(bundle.getString("lbl.please.wait")); // NOI18N
        textScanProgressBar.setStringPainted(true);

        scanTextButton.setText(bundle.getString("lbl.scan.text.button")); // NOI18N
        scanTextButton.setEnabled(false);
        scanTextButton.addActionListener(formListener);

        sourceTextUseSamePaletteChkbx.setSelected(true);
        sourceTextUseSamePaletteChkbx.setText(bundle.getString("lbl.sourceTextUseSamePaletteChkbx")); // NOI18N
        sourceTextUseSamePaletteChkbx.addActionListener(formListener);

        jLabel10.setText("Color Picked");

        colorPickedJList.setFont(new java.awt.Font("Monospaced", 1, 13)); // NOI18N
        jScrollPane2.setViewportView(colorPickedJList);

        javax.swing.GroupLayout textPanelLayout = new javax.swing.GroupLayout(textPanel);
        textPanel.setLayout(textPanelLayout);
        textPanelLayout.setHorizontalGroup(
            textPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(textPanelToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(textPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(textPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(textPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(textScanProgressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, textPanelLayout.createSequentialGroup()
                            .addComponent(jLabel9)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(textPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(sourceTextPaletteNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(sourceTextUseSamePaletteChkbx)))
                        .addComponent(scanTextButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(textPanelLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addGap(2, 2, 2))
        );
        textPanelLayout.setVerticalGroup(
            textPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(textPanelLayout.createSequentialGroup()
                .addComponent(textPanelToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(textPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(textPanelLayout.createSequentialGroup()
                        .addGroup(textPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(sourceTextPaletteNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sourceTextUseSamePaletteChkbx)
                        .addGap(6, 6, 6)
                        .addGroup(textPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(textPanelLayout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(0, 106, Short.MAX_VALUE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textScanProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scanTextButton))
                    .addComponent(jScrollPane1))
                .addGap(2, 2, 2))
        );

        colorSourceTabbedPane.addTab(bundle.getString("lbl.text.tab.title"), textPanel); // NOI18N

        javax.swing.GroupLayout logContentPanelLayout = new javax.swing.GroupLayout(logContentPanel);
        logContentPanel.setLayout(logContentPanelLayout);
        logContentPanelLayout.setHorizontalGroup(
            logContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 678, Short.MAX_VALUE)
        );
        logContentPanelLayout.setVerticalGroup(
            logContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );

        colorSourceTabbedPane.addTab(bundle.getString("lbl.log.tab.title"), logContentPanel); // NOI18N

        baseSplitPane.setRightComponent(colorSourceTabbedPane);

        javax.swing.GroupLayout baseContentPanelLayout = new javax.swing.GroupLayout(baseContentPanel);
        baseContentPanel.setLayout(baseContentPanelLayout);
        baseContentPanelLayout.setHorizontalGroup(
            baseContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(baseSplitPane)
        );
        baseContentPanelLayout.setVerticalGroup(
            baseContentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(baseSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
        );

        getContentPane().add(baseContentPanel, java.awt.BorderLayout.CENTER);

        fileMenu.setText(bundle.getString("lbl.file.menu")); // NOI18N

        newMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new_palette.png"))); // NOI18N
        newMenuItem.setText(bundle.getString("lbl.new.menu.item")); // NOI18N
        newMenuItem.addActionListener(formListener);
        fileMenu.add(newMenuItem);
        fileMenu.add(jSeparator1);

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save_edit.gif"))); // NOI18N
        saveMenuItem.setText(bundle.getString("lbl.save.menu.item")); // NOI18N
        fileMenu.add(saveMenuItem);

        savePaletteAsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/saveas_edit.gif"))); // NOI18N
        savePaletteAsMenuItem.setText(bundle.getString("lbl.save.palette.as.menu.item")); // NOI18N
        fileMenu.add(savePaletteAsMenuItem);
        fileMenu.add(jSeparator5);

        importPalettesMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/import_wiz.gif"))); // NOI18N
        importPalettesMenuItem.setText(bundle.getString("lbl.import.palettes.menu.item")); // NOI18N
        fileMenu.add(importPalettesMenuItem);

        exportPalettesMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/export_wiz.gif"))); // NOI18N
        exportPalettesMenuItem.setText(bundle.getString("lbl.export.palettes.menu.item")); // NOI18N
        fileMenu.add(exportPalettesMenuItem);
        fileMenu.add(jSeparator2);

        hideMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK));
        hideMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/hide.png"))); // NOI18N
        hideMenuItem.setText(bundle.getString("lbl.hide.menu.item")); // NOI18N
        hideMenuItem.addActionListener(formListener);
        fileMenu.add(hideMenuItem);

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        exitMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit.png"))); // NOI18N
        exitMenuItem.setText(bundle.getString("lbl.exit.menu.item")); // NOI18N
        exitMenuItem.addActionListener(formListener);
        fileMenu.add(exitMenuItem);

        baseMenuBar.add(fileMenu);

        editMenu.setText(bundle.getString("lbl.edit.menu")); // NOI18N

        undoMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        undoMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/undo.png"))); // NOI18N
        undoMenuItem.setText(bundle.getString("lbl.undo.menu.item")); // NOI18N
        editMenu.add(undoMenuItem);

        redoMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        redoMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/redo.png"))); // NOI18N
        redoMenuItem.setText(bundle.getString("lbl.redo.menu.item")); // NOI18N
        editMenu.add(redoMenuItem);
        editMenu.add(jSeparator6);

        cutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        cutMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cut_edit.gif"))); // NOI18N
        cutMenuItem.setText(bundle.getString("lbl.cut.menu.item")); // NOI18N
        editMenu.add(cutMenuItem);

        copyMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        copyMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/copy_edit.gif"))); // NOI18N
        copyMenuItem.setText(bundle.getString("lbl.copy.menu.item")); // NOI18N
        editMenu.add(copyMenuItem);

        pasteMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        pasteMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/paste.png"))); // NOI18N
        pasteMenuItem.setText(bundle.getString("lbl.paste.menu.item")); // NOI18N
        editMenu.add(pasteMenuItem);
        editMenu.add(jSeparator7);

        renameMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        renameMenuItem.setText(bundle.getString("lbl.rename.menu.item")); // NOI18N
        editMenu.add(renameMenuItem);

        baseMenuBar.add(editMenu);

        toolsMenu.setText(bundle.getString("lbl.tools.menu")); // NOI18N

        settingsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/settings.png"))); // NOI18N
        settingsMenuItem.setText(bundle.getString("lbl.settings.menu.item")); // NOI18N
        settingsMenuItem.addActionListener(formListener);
        toolsMenu.add(settingsMenuItem);
        toolsMenu.add(jSeparator4);

        enableEnlargeAreaChkMenuItem.setSelected(true);
        enableEnlargeAreaChkMenuItem.setText(bundle.getString("lbl.enableEnlargeAreaChkMenuItem")); // NOI18N
        enableEnlargeAreaChkMenuItem.addActionListener(formListener);
        toolsMenu.add(enableEnlargeAreaChkMenuItem);

        enableRealtimeViewChkMenuItem.setText(bundle.getString("lbl.enableRealtimeViewChkMenuItem")); // NOI18N
        enableRealtimeViewChkMenuItem.addActionListener(formListener);
        toolsMenu.add(enableRealtimeViewChkMenuItem);

        baseMenuBar.add(toolsMenu);

        setJMenuBar(baseMenuBar);

        pack();
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener, java.awt.event.ComponentListener, java.awt.event.ItemListener, java.awt.event.KeyListener, java.awt.event.MouseListener, java.awt.event.WindowListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == paletteListComboBox) {
                ColorHoundBaseFrame.this.paletteListComboBoxActionPerformed(evt);
            }
            else if (evt.getSource() == addPaletteButton) {
                ColorHoundBaseFrame.this.addPaletteButtonActionPerformed(evt);
            }
            else if (evt.getSource() == deletePaletteButton) {
                ColorHoundBaseFrame.this.deletePaletteButtonActionPerformed(evt);
            }
            else if (evt.getSource() == addColorButton) {
                ColorHoundBaseFrame.this.addColorButtonActionPerformed(evt);
            }
            else if (evt.getSource() == editColorButton) {
                ColorHoundBaseFrame.this.editColorButtonActionPerformed(evt);
            }
            else if (evt.getSource() == deleteColorButton) {
                ColorHoundBaseFrame.this.deleteColorButtonActionPerformed(evt);
            }
            else if (evt.getSource() == cssRgbTextField) {
                ColorHoundBaseFrame.this.cssRgbTextFieldActionPerformed(evt);
            }
            else if (evt.getSource() == rgbCopyButton) {
                ColorHoundBaseFrame.this.rgbCopyButtonActionPerformed(evt);
            }
            else if (evt.getSource() == hexCopyButton) {
                ColorHoundBaseFrame.this.hexCopyButtonActionPerformed(evt);
            }
            else if (evt.getSource() == cssCopyButton) {
                ColorHoundBaseFrame.this.cssCopyButtonActionPerformed(evt);
            }
            else if (evt.getSource() == hexColorTextField) {
                ColorHoundBaseFrame.this.hexColorTextFieldActionPerformed(evt);
            }
            else if (evt.getSource() == openImageButton) {
                ColorHoundBaseFrame.this.openImageButtonActionPerformed(evt);
            }
            else if (evt.getSource() == captureDesktopButton) {
                ColorHoundBaseFrame.this.captureDesktopButtonActionPerformed(evt);
            }
            else if (evt.getSource() == openTextFileButton) {
                ColorHoundBaseFrame.this.openTextFileButtonActionPerformed(evt);
            }
            else if (evt.getSource() == cleatTextButton) {
                ColorHoundBaseFrame.this.cleatTextButtonActionPerformed(evt);
            }
            else if (evt.getSource() == scanTextButton) {
                ColorHoundBaseFrame.this.scanTextButtonActionPerformed(evt);
            }
            else if (evt.getSource() == sourceTextUseSamePaletteChkbx) {
                ColorHoundBaseFrame.this.sourceTextUseSamePaletteChkbxActionPerformed(evt);
            }
            else if (evt.getSource() == newMenuItem) {
                ColorHoundBaseFrame.this.newMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == hideMenuItem) {
                ColorHoundBaseFrame.this.hideMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == exitMenuItem) {
                ColorHoundBaseFrame.this.exitMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == settingsMenuItem) {
                ColorHoundBaseFrame.this.settingsMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == enableEnlargeAreaChkMenuItem) {
                ColorHoundBaseFrame.this.enableEnlargeAreaChkMenuItemActionPerformed(evt);
            }
            else if (evt.getSource() == enableRealtimeViewChkMenuItem) {
                ColorHoundBaseFrame.this.enableRealtimeViewChkMenuItemActionPerformed(evt);
            }
        }

        public void componentHidden(java.awt.event.ComponentEvent evt) {
        }

        public void componentMoved(java.awt.event.ComponentEvent evt) {
        }

        public void componentResized(java.awt.event.ComponentEvent evt) {
            if (evt.getSource() == baseSplitPane) {
                ColorHoundBaseFrame.this.baseSplitPaneComponentResized(evt);
            }
        }

        public void componentShown(java.awt.event.ComponentEvent evt) {
        }

        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            if (evt.getSource() == paletteListComboBox) {
                ColorHoundBaseFrame.this.paletteListComboBoxItemStateChanged(evt);
            }
        }

        public void keyPressed(java.awt.event.KeyEvent evt) {
            if (evt.getSource() == ColorHoundBaseFrame.this) {
                ColorHoundBaseFrame.this.formKeyPressed(evt);
            }
            else if (evt.getSource() == paletteContentPanel) {
                ColorHoundBaseFrame.this.paletteContentPanelKeyPressed(evt);
            }
        }

        public void keyReleased(java.awt.event.KeyEvent evt) {
            if (evt.getSource() == ColorHoundBaseFrame.this) {
                ColorHoundBaseFrame.this.formKeyReleased(evt);
            }
        }

        public void keyTyped(java.awt.event.KeyEvent evt) {
        }

        public void mouseClicked(java.awt.event.MouseEvent evt) {
            if (evt.getSource() == paletteContentPanel) {
                ColorHoundBaseFrame.this.paletteContentPanelMouseClicked(evt);
            }
        }

        public void mouseEntered(java.awt.event.MouseEvent evt) {
        }

        public void mouseExited(java.awt.event.MouseEvent evt) {
        }

        public void mousePressed(java.awt.event.MouseEvent evt) {
        }

        public void mouseReleased(java.awt.event.MouseEvent evt) {
        }

        public void windowActivated(java.awt.event.WindowEvent evt) {
        }

        public void windowClosed(java.awt.event.WindowEvent evt) {
        }

        public void windowClosing(java.awt.event.WindowEvent evt) {
            if (evt.getSource() == ColorHoundBaseFrame.this) {
                ColorHoundBaseFrame.this.formWindowClosing(evt);
            }
        }

        public void windowDeactivated(java.awt.event.WindowEvent evt) {
        }

        public void windowDeiconified(java.awt.event.WindowEvent evt) {
        }

        public void windowIconified(java.awt.event.WindowEvent evt) {
            if (evt.getSource() == ColorHoundBaseFrame.this) {
                ColorHoundBaseFrame.this.formWindowIconified(evt);
            }
        }

        public void windowOpened(java.awt.event.WindowEvent evt) {
        }

        public void propertyChange(java.beans.PropertyChangeEvent evt) {
            if (evt.getSource() == paletteListComboBox) {
                ColorHoundBaseFrame.this.paletteListComboBoxPropertyChange(evt);
            }
            else if (evt.getSource() == baseSplitPane) {
                ColorHoundBaseFrame.this.baseSplitPanePropertyChange(evt);
            }
        }

        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            if (evt.getSource() == ratioSlider) {
                ColorHoundBaseFrame.this.ratioSliderStateChanged(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        
    }//GEN-LAST:event_formKeyPressed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        
    }//GEN-LAST:event_formKeyReleased

    private void deleteColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteColorButtonActionPerformed
        if(null != ColorPaletteManager.getInstance().getSelectPanel()){
            paletteContentPanel.remove(ColorPaletteManager.getInstance().getSelectPanel());
            resizePaletteContentPanel();
            paletteContentPanel.updateUI();
            ColorPaletteManager.getInstance().removeSelectedPanel(getPaletteName());
            ColorPanelSelectedEvent event = new ColorPanelSelectedEvent
                (true, false, this);
            ApplicationEventManager.getInstance().fireEvent(event);
        }
    }//GEN-LAST:event_deleteColorButtonActionPerformed

    private void addColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addColorButtonActionPerformed
        String palette = getPaletteName();
        if(!"".equals(palette)){
            ColorPanel colorPanel = new ColorPanel(paletteContentPanel, getPaletteName());
            ColorPaletteManager.getInstance().addPanel(getPaletteName(), colorPanel);
            paletteContentPanel.add(colorPanel, FlowLayout.LEFT);
            paletteContentPanel.setPreferredSize(paletteContentPanel.getSize());
            paletteContentPanel.validate();
            paletteContentPanel.repaint();
            paletteContentPanel.updateUI();
            paletteContentScrollPane.updateUI();
            resizePaletteContentPanel();
        }
        else {
            JOptionPane.showMessageDialog(this, 
                    resourceBundle.getString("lbl.add.palette.name"));
        }
        
    }//GEN-LAST:event_addColorButtonActionPerformed

    private void editColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editColorButtonActionPerformed
    	ColorPaletteManager.getInstance().makeSelectedColorPanelEditable(getPaletteName());
        if(enlargePanelEnabled 
                    && ColorPaletteManager.getInstance().isSelectedPanelEditable(getPaletteName())){
                startMouseWatch();
        } else {
            mouseWatchTimer.cancel();
        }
    }//GEN-LAST:event_editColorButtonActionPerformed

    private void rgbCopyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rgbCopyButtonActionPerformed
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection( 
                "rgb( " 
                + redRgbTextField.getText() + ", "
                + greenRgbTextField.getText() + ", "
                + blueRgbTextField.getText() + " )");
        clipboard.setContents(stringSelection , this );
    }//GEN-LAST:event_rgbCopyButtonActionPerformed

    private void hexCopyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hexCopyButtonActionPerformed
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection( hexColorTextField.getText() );
        clipboard.setContents(stringSelection , this );
    }//GEN-LAST:event_hexCopyButtonActionPerformed

    private void cssCopyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cssCopyButtonActionPerformed
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection( cssRgbTextField.getText() );
        clipboard.setContents(stringSelection , this );
    }//GEN-LAST:event_cssCopyButtonActionPerformed

    private void cssRgbTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cssRgbTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cssRgbTextFieldActionPerformed

    private void newMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMenuItemActionPerformed
        String name = JOptionPane.showInputDialog(this, 
                resourceBundle.getString("lbl.enter.palette.name"));
        ColorPaletteManager.getInstance().addPalette(name);
        paletteListComboBox.addItem(name);
        paletteListComboBox.setSelectedItem(name);
    }//GEN-LAST:event_newMenuItemActionPerformed

    private void paletteListComboBoxPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_paletteListComboBoxPropertyChange
        //System.out.println("paletteListComboBoxPropertyChange");
    }//GEN-LAST:event_paletteListComboBoxPropertyChange

    private void paletteListComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_paletteListComboBoxItemStateChanged
        //System.out.println("paletteListComboBoxItemStateChanged");
    }//GEN-LAST:event_paletteListComboBoxItemStateChanged

    private void paletteListComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paletteListComboBoxActionPerformed
        if(null != paletteListComboBox.getSelectedItem()){
            String selectedPaletteName = paletteListComboBox.getSelectedItem().toString();
            selectedPaletteNameLabel.setText("  "+selectedPaletteName+"  ");
            final List<ColorPanel> cps = 
                ColorPaletteManager.getInstance().getAllColorPanels(
                selectedPaletteName
                );
            if(null != cps){
                paletteContentPanel.removeAll();
                for (ColorPanel cp : cps) {
                    paletteContentPanel.add(cp);
                }
            }
            addColorButton.setEnabled(true);
            deletePaletteButton.setEnabled(true);
            if(sourceTextUseSamePaletteChkbx.isSelected()){
                sourceTextPaletteNameTextField.setText(selectedPaletteName);
            }
        } else {
            addColorButton.setEnabled(false);
            deletePaletteButton.setEnabled(false);
            editColorButton.setEnabled(false);
            deleteColorButton.setEnabled(false);
            paletteContentPanel.removeAll();
            paletteContentPanel.updateUI();
            ColorPaletteManager.getInstance().removeSelection();
        }
        resizePaletteContentPanel();
        paletteContentPanel.updateUI();
        
    }//GEN-LAST:event_paletteListComboBoxActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if(appContext.getApplicationSettings().isCloseToHide()){
            minimizeToTray(this, frameIcon.getImage(), exitListener);
        }
        else {
            if(appContext.getApplicationSettings().isDoNotShowExitDialog()){
                int opt = DisplayUtils.confirmOkCancel(this, 
                        resourceBundle.getString("lbl.exit.prompt"), 
                        DisplayTypeEnum.WARN);
                if(JOptionPane.YES_OPTION == opt){
                    appContext.save();
                    System.exit(0);
                }
            } else {
                appContext.save();
                System.exit(0);
            }
        }
    }//GEN-LAST:event_formWindowClosing

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        if(appContext.getApplicationSettings().isDoNotShowExitDialog()){
            int opt = DisplayUtils.confirmOkCancel(this, 
                    resourceBundle.getString("lbl.exit.prompt"), 
                    DisplayTypeEnum.WARN);
            if(JOptionPane.YES_OPTION == opt){
                appContext.save();
                System.exit(0);
            }
        } else {
            appContext.save();
            System.exit(0);
        }
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void hideMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hideMenuItemActionPerformed
        minimizeToTray(this, frameIcon.getImage(), exitListener);
    }//GEN-LAST:event_hideMenuItemActionPerformed

    private void addPaletteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPaletteButtonActionPerformed
        newMenuItemActionPerformed(evt);
    }//GEN-LAST:event_addPaletteButtonActionPerformed

    private void deletePaletteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deletePaletteButtonActionPerformed
        int opt = DisplayUtils.confirmOkCancel(this, 
                resourceBundle.getString("lbl.delete.palette.cofn"), 
                DisplayTypeEnum.WARN);
        if(JOptionPane.OK_OPTION == opt){
            String name = getPaletteName();
            paletteListComboBox.removeItemAt(paletteListComboBox.getSelectedIndex());
            ColorPaletteManager.getInstance().deletePalette(name);
            resizePaletteContentPanel();
            if(paletteListComboBox.getItemCount() >= 1){
                paletteListComboBox.setSelectedIndex(0);
            } else {
                paletteListComboBox.setSelectedIndex(-1);
            }
            paletteListComboBox.updateUI();
            paletteContentPanel.updateUI();
        }
    }//GEN-LAST:event_deletePaletteButtonActionPerformed

    private void settingsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsMenuItemActionPerformed
        SettingsDialog dialog = new SettingsDialog(this, true);
        dialog.setVisible(true);
    }//GEN-LAST:event_settingsMenuItemActionPerformed

    private void formWindowIconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowIconified
        if(appContext.getApplicationSettings().isHideWhenMinimized()){
            minimizeToTray(this, frameIcon.getImage(), exitListener);
        }
    }//GEN-LAST:event_formWindowIconified

    private void hexColorTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hexColorTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hexColorTextFieldActionPerformed

    private void openImageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openImageButtonActionPerformed
        File file = FileBrowserUtil.openSingleFile(this, 
                new ExtensionFileFilter(new String[]{"jpeg","jpg", 
                    "png", "gif"}, "Image files..."), 
                false);
        if(null != file && file.exists()){
            try {
                imageContainerPanel.removeAll();
                ImagePanel imagePanel = new ImagePanel(file.getCanonicalPath());

                final JScrollPane imagePanelScrollPane = new JScrollPane(imagePanel);
        		final int increment = 50;
        		imagePanelScrollPane.getVerticalScrollBar().setUnitIncrement(increment);
        		KeyStroke kUp = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
        		KeyStroke kDown = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
        		imagePanelScrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
        				kUp, "actionWhenKeyUp");
        		imagePanelScrollPane.getActionMap().put("actionWhenKeyUp",
        				new AbstractAction("keyUpAction") {

        					private static final long serialVersionUID = 1L;

        					public void actionPerformed(ActionEvent e) {
        						final JScrollBar bar = imagePanelScrollPane.getVerticalScrollBar();
        						int currentValue = bar.getValue();
        						bar.setValue(currentValue - increment);
        					}
        				});
        		imagePanelScrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
        				kDown, "actionWhenKeyDown");
        		imagePanelScrollPane.getActionMap().put("actionWhenKeyDown",
        				new AbstractAction("keyDownAction") {

        					private static final long serialVersionUID = 1L;

        					public void actionPerformed(ActionEvent e) {
        						final JScrollBar bar = imagePanelScrollPane.getVerticalScrollBar();
        						int currentValue = bar.getValue();
        						bar.setValue(currentValue + increment);
        					}
        				});
        		imageContainerPanel.add(imagePanelScrollPane, BorderLayout.CENTER);
                imageContainerPanel.updateUI();
            } catch (IOException ex) {
                
            }
        }
        
    }//GEN-LAST:event_openImageButtonActionPerformed

    private void captureDesktopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_captureDesktopButtonActionPerformed
        final TrayIcon trayIcon = minimizeToTray(this, 
                frameIcon.getImage(), exitListener);
        try {
			Thread.sleep(500);
			BufferedImage image = null;
			Toolkit toolkit = Toolkit.getDefaultToolkit();
            
			Rectangle screenRectangle=new Rectangle(toolkit.getScreenSize());
			Robot robot=new Robot();
			image = robot.createScreenCapture(screenRectangle);
			if(null != image){
                
                imageContainerPanel.removeAll();
                ImagePanel imagePanel = new ImagePanel(image);

                final JScrollPane imagePanelScrollPane = new JScrollPane(imagePanel);
                final int increment = 50;
                imagePanelScrollPane.getVerticalScrollBar().setUnitIncrement(increment);
                KeyStroke kUp = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
                KeyStroke kDown = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
                imagePanelScrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                        kUp, "actionWhenKeyUp");
                imagePanelScrollPane.getActionMap().put("actionWhenKeyUp",
                        new AbstractAction("keyUpAction") {

                            private static final long serialVersionUID = 1L;

                            public void actionPerformed(ActionEvent e) {
                                final JScrollBar bar = imagePanelScrollPane.getVerticalScrollBar();
                                int currentValue = bar.getValue();
                                bar.setValue(currentValue - increment);
                            }
                        });
                imagePanelScrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                        kDown, "actionWhenKeyDown");
                imagePanelScrollPane.getActionMap().put("actionWhenKeyDown",
                        new AbstractAction("keyDownAction") {

                            private static final long serialVersionUID = 1L;

                            public void actionPerformed(ActionEvent e) {
                                final JScrollBar bar = imagePanelScrollPane.getVerticalScrollBar();
                                int currentValue = bar.getValue();
                                bar.setValue(currentValue + increment);
                            }
                        });
                imageContainerPanel.add(imagePanelScrollPane, BorderLayout.CENTER);
                imageContainerPanel.updateUI();
                
            }
			Thread.sleep(500);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		if (SystemTray.isSupported() && null != trayIcon) {
			// get the SystemTray instance
			SystemTray tray = SystemTray.getSystemTray();
			tray.remove(trayIcon);
			this.setVisible(true);
		}
    }//GEN-LAST:event_captureDesktopButtonActionPerformed

    private void enableEnlargeAreaChkMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enableEnlargeAreaChkMenuItemActionPerformed
        synchronized(this){
            if(enableEnlargeAreaChkMenuItem.isSelected()){
                enlargePanelEnabled = true;
                mouseWatchTimer.schedule(new MouseWatcher(), 0, timerPeriod);
            } else {
                enlargePanelEnabled = false;
                mouseWatchTimer.cancel();
            }
        }
    }//GEN-LAST:event_enableEnlargeAreaChkMenuItemActionPerformed

    private void ratioSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ratioSliderStateChanged
        ratioLabel.setText(""+ratioSlider.getValue()+"x");
    }//GEN-LAST:event_ratioSliderStateChanged

    private void enableRealtimeViewChkMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enableRealtimeViewChkMenuItemActionPerformed
        if(enableRealtimeViewChkMenuItem.isSelected()){
            //mouseWatchTimer.cancel();
            timerPeriod = 1;
            //mouseWatchTimer.schedule(mouseWatcher, 0, timerPeriod);
        } else {
            timerPeriod = 500;
        }
    }//GEN-LAST:event_enableRealtimeViewChkMenuItemActionPerformed

    private void baseSplitPaneComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_baseSplitPaneComponentResized
        
    }//GEN-LAST:event_baseSplitPaneComponentResized

    private void baseSplitPanePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_baseSplitPanePropertyChange
        resizePaletteContentPanel();
        paletteContentPanel.updateUI();
    }//GEN-LAST:event_baseSplitPanePropertyChange

    private void sourceTextUseSamePaletteChkbxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sourceTextUseSamePaletteChkbxActionPerformed
        if(sourceTextUseSamePaletteChkbx.isSelected()){
            if(!"".equals(getPaletteName())){
                sourceTextPaletteNameTextField.setText(getPaletteName());
                sourceTextPaletteNameTextField.setEditable(false);
            } else {
                sourceTextPaletteNameTextField.setEditable(true);
            }
        } else {
            sourceTextPaletteNameTextField.setEditable(true);
        }
    }//GEN-LAST:event_sourceTextUseSamePaletteChkbxActionPerformed

    private void scanTextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scanTextButtonActionPerformed
        synchronized (this) {
            textScanWorker = new TextScanWorker(
                    sourceTextTextArea.getText().trim(),
                    sourceTextPaletteNameTextField.getText());
            textScanWorker.addPropertyChangeListener(this);
            textScanWorker.execute();
        }
    }//GEN-LAST:event_scanTextButtonActionPerformed

    private void openTextFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openTextFileButtonActionPerformed
        File file = FileBrowserUtil.openSingleFile(this, 
             new ExtensionFileFilter(
                new String[]{"txt", "html", "xml",
                "css", "xhtml", "htm"}, "text/html/xml")
             , false);
        if(null != file && file.exists() && file.canRead()){
            try {
                sourceTextTextArea.setText(FileUtils.readFileToString(file));
            } catch (IOException ex) {
                sourceTextTextArea.setText("");
            }
        }
    }//GEN-LAST:event_openTextFileButtonActionPerformed

    private void cleatTextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleatTextButtonActionPerformed
        sourceTextTextArea.setText("");
        colorPickedJList.setModel(new ColorPaletteListModel(new ArrayList<ColorData>()));
        colorPickedJList.updateUI();
    }//GEN-LAST:event_cleatTextButtonActionPerformed

    private void paletteContentPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_paletteContentPanelMouseClicked
        paletteContentPanel.requestFocus();
        ColorPanelSelectedEvent event = new ColorPanelSelectedEvent(false, false,this);
        event.setOldSelectedPanel(ColorPaletteManager.getInstance().getSelectPanel());
        ApplicationEventManager.getInstance().fireEvent(event);
    }//GEN-LAST:event_paletteContentPanelMouseClicked

    private void paletteContentPanelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_paletteContentPanelKeyPressed
        // if CTRL+<key>
        if(KeyEvent.CTRL_MASK == evt.getModifiers()){
            if(evt.getKeyCode() == KeyEvent.VK_A){
                ColorPaletteManager.getInstance().selectAll(paletteListComboBox.getSelectedItem().toString());
            }
        }
        
        
    }//GEN-LAST:event_paletteContentPanelKeyPressed

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addColorButton;
    private javax.swing.JButton addPaletteButton;
    private javax.swing.JPanel baseContentPanel;
    private javax.swing.JMenuBar baseMenuBar;
    private javax.swing.JSplitPane baseSplitPane;
    private javax.swing.JToolBar baseToolBar;
    private javax.swing.JTextField blueRgbTextField;
    private javax.swing.JButton captureDesktopButton;
    private javax.swing.JButton cleatTextButton;
    private javax.swing.JPanel colorDetailsPanel;
    private javax.swing.JList colorPickedJList;
    private javax.swing.JTabbedPane colorSourceTabbedPane;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JButton cssCopyButton;
    private javax.swing.JTextField cssRgbTextField;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JButton deleteColorButton;
    private javax.swing.JButton deletePaletteButton;
    private javax.swing.JButton editColorButton;
    private javax.swing.JMenu editMenu;
    private javax.swing.JCheckBoxMenuItem enableEnlargeAreaChkMenuItem;
    private javax.swing.JCheckBoxMenuItem enableRealtimeViewChkMenuItem;
    private javax.swing.JPanel enlargedPanel;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenuItem exportPalettesMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JTextField greenRgbTextField;
    private javax.swing.JTextField hexColorTextField;
    private javax.swing.JButton hexCopyButton;
    private javax.swing.JMenuItem hideMenuItem;
    private javax.swing.JPanel imageContainerPanel;
    private javax.swing.JToolBar imageControlToolBar;
    private javax.swing.JPanel imageViewerPanel;
    private javax.swing.JMenuItem importPalettesMenuItem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel logContentPanel;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JButton openImageButton;
    private javax.swing.JButton openTextFileButton;
    private javax.swing.JPanel paletteContentPanel;
    private javax.swing.JScrollPane paletteContentScrollPane;
    private javax.swing.JComboBox paletteListComboBox;
    private javax.swing.JToolBar paletteToolBar;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JLabel ratioLabel;
    private javax.swing.JSlider ratioSlider;
    private javax.swing.JTextField redRgbTextField;
    private javax.swing.JMenuItem redoMenuItem;
    private javax.swing.JMenuItem renameMenuItem;
    private javax.swing.JButton rgbCopyButton;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JMenuItem savePaletteAsMenuItem;
    private javax.swing.JButton scanTextButton;
    private javax.swing.JLabel selectedPaletteNameLabel;
    private javax.swing.JMenuItem settingsMenuItem;
    private javax.swing.JTextField sourceTextPaletteNameTextField;
    private javax.swing.JTextArea sourceTextTextArea;
    private javax.swing.JCheckBox sourceTextUseSamePaletteChkbx;
    private javax.swing.JPanel textPanel;
    private javax.swing.JToolBar textPanelToolBar;
    private javax.swing.JProgressBar textScanProgressBar;
    private javax.swing.JMenu toolsMenu;
    private javax.swing.JPanel topPanel;
    private javax.swing.JMenuItem undoMenuItem;
    // End of variables declaration//GEN-END:variables

    public TrayIcon minimizeToTray(final Frame frame, final Image image, ActionListener exitListener) {
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(image, frame.getTitle(), popup);

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                trayIcon.displayMessage("Action Event",
                        "An Action Event Has Been Performed!",
                        TrayIcon.MessageType.INFO);
            }
        };
        if (SystemTray.isSupported()) {
            // get the SystemTray instance
            final SystemTray tray = SystemTray.getSystemTray();
            MouseListener mouseListener = new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    if (MouseEvent.BUTTON1 == e.getButton() 
                            && e.getClickCount() == 2) {
                        tray.remove(trayIcon);
                        frame.setVisible(true);
                    }
                }

                public void mouseEntered(MouseEvent e) {
                    
                }

                public void mouseExited(MouseEvent e) {
                    
                }

                public void mousePressed(MouseEvent e) {
                    
                    
                }

                public void mouseReleased(MouseEvent e) {
                    
                    
                }
            };




            MenuItem restoreItem = new MenuItem("Restore");
            restoreItem.setShortcut(new MenuShortcut('R'));
            restoreItem.addActionListener(
                    new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    tray.remove(trayIcon);
                    frame.setVisible(true);
                }
            });
            popup.add(restoreItem);

            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(exitListener);
            popup.add(exitItem);

            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(actionListener);
            trayIcon.addMouseListener(mouseListener);

            try {
                tray.add(trayIcon);
                frame.setVisible(false);
            } catch (AWTException e) {
                System.err.println("TrayIcon could not be added.");
            }

            return trayIcon;
        } else {
            // disable tray option in your application or
            // perform other actions
        }
        return null;
    }
    
    public String getClipboardContents() {
        String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //odd: the Object param of getContents is not currently used
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText =
                (contents != null)
                && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasTransferableText) {
            try {
                result = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException ex) {
                //highly unlikely since we are using a standard DataFlavor
                System.out.println(ex);
                ex.printStackTrace();
            } catch (IOException ex) {
                System.out.println(ex);
                ex.printStackTrace();
            }
        }
        return result;
    }
    
    @Override
    public void colorGrabbed(ColorGrabEvent event) {
        synchronized(this){
            mouseWatchTimer.cancel();
        }
    }

    
    public String getPaletteName(){
        return (null != paletteListComboBox.getSelectedItem())
                ? paletteListComboBox.getSelectedItem().toString() : "";
    }
    
    public synchronized void startMouseWatch() {
        try {
            mouseWatchTimer = new Timer();
            mouseWatchTimer.schedule(new MouseWatcher(), 0, timerPeriod);
        } catch (IllegalStateException ise) {
            mouseWatchTimer.cancel();
            //mouseWatchTimer.schedule(new MouseWatcher(), 0, timerPeriod);
        }
    }
    
    @Override
    public void colorPanelSelected(ColorPanelSelectedEvent event) {
        if(event.getNewValue()){
            if(enlargePanelEnabled 
                    && ColorPaletteManager.getInstance().isSelectedPanelEditable(getPaletteName())){
                startMouseWatch();
            }
            editColorButton.setEnabled(true);
            deleteColorButton.setEnabled(true);
            if(event.getSelectedColor() != null){
                redRgbTextField.setText(""+event.getSelectedColor().getRed());
                greenRgbTextField.setText(""+event.getSelectedColor().getGreen());
                blueRgbTextField.setText(""+event.getSelectedColor().getBlue());
                hexColorTextField.setText(GraphicsUtil.encodeColor(event.getSelectedColor()));
                cssRgbTextField.setText(GraphicsUtil.encodeColorForCss(event.getSelectedColor()));
            }
            
        } else {
            if(event.getOldSelectedPanel() != null){
                event.getOldSelectedPanel().colorPanelSelectionRemoved();
            }
            mouseWatchTimer.cancel();
            editColorButton.setEnabled(false);
            deleteColorButton.setEnabled(false);
        }
    }

    @Override
    public void appSettingsChanged(AppSettingsChangedEvent event) {
        
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        
    }

    @Override
    public void colorDetected(ColorDetectEvent event) {
        
    }

    @Override
    public void mouseInfoChanged(MouseInfoChangeEvent event) {
        if(null != event.getNewLocation()
                && enlargePanelEnabled
                && ColorPaletteManager.getInstance().isSelectedPanelEditable(getPaletteName())){
            
            try {
                Robot robot = new Robot();
                int W = EnlargedImagePanel.WIDTH, 
                        H = EnlargedImagePanel.HEIGHT;
                int m, n;
                int r = ratioSlider.getValue();
                
                int x = (int)event.getNewLocation().getX();
                int y = (int)event.getNewLocation().getY();
                int w = W / r;
                int h = H / r;
                m = Math.max(0, x - (w/2));
                n = Math.max(0, y - (h/2));
                Color color = robot.getPixelColor(x, y);
                if(null != color){
                    ColorDetectEvent cde = new ColorDetectEvent(null, color, this);
                    eventManager.fireEvent(cde);
                }
                
                BufferedImage img = robot.createScreenCapture(
                        new Rectangle(m, n, w, h)
                     );
                if(null != img){
                    enlargedImagePanel.setImage(img);
                }
            } catch (AWTException ex) {
                
            }
        }
    }

    private void resizePaletteContentPanel() {
        int totalColorPanels = ColorPaletteManager.getInstance()
                .getColorPanelCount(getPaletteName());
        int maxColorsInRow = PALETTE_CONTENT_MAX_WIDTH / ColorPanel.MAX_WIDTH;
        if(totalColorPanels <= maxColorsInRow){
            paletteContentPanel.setPreferredSize(
                new Dimension(100,100));
            return;
        }
        int rowCount = (int)(totalColorPanels/maxColorsInRow)+1;
        int height = rowCount * (ColorPanel.MAX_HEIGHT+5);
        paletteContentPanel.setPreferredSize(
                new Dimension(PALETTE_CONTENT_MAX_WIDTH, height+10));
        
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
        if(evt.getSource().equals(textScanWorker)){
            processTextScanProperty(evt);
        }
    }

    private void processTextScanProperty(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if(WorkerTaskConstants.TASK_STATUS_START.equals(propertyName)){
            scanTextButton.setEnabled(false);
            sourceTextPaletteNameTextField.setEditable(false);
            sourceTextTextArea.setEditable(false);
            sourceTextUseSamePaletteChkbx.setEnabled(false);
            addPaletteButton.setEnabled(false);
            deletePaletteButton.setEnabled(false);
            paletteListComboBox.setEnabled(false);
            newMenuItem.setEnabled(false);
            colorPickedJList.setModel(
                    new ColorPaletteListModel(new ArrayList<ColorData>(0)));
            textScanProgressBar.setVisible(true);
            openTextFileButton.setEnabled(false);
            cleatTextButton.setEnabled(false);
        } else if(WorkerTaskConstants.TASK_STATUS_FAILED.equals(propertyName)){
            scanTextButton.setEnabled(true);
            if(sourceTextUseSamePaletteChkbx.isSelected()){
                sourceTextPaletteNameTextField.setEditable(false);
            } else {
                sourceTextPaletteNameTextField.setEditable(true);
            }
            sourceTextTextArea.setEditable(true);
            sourceTextUseSamePaletteChkbx.setEnabled(true);
            addPaletteButton.setEnabled(true);
            deletePaletteButton.setEnabled(true);
            paletteListComboBox.setEnabled(true);
            newMenuItem.setEnabled(true);
            colorPickedJList.setModel(
                    new ColorPaletteListModel(new ArrayList<ColorData>(0)));
            textScanProgressBar.setVisible(false);
            openTextFileButton.setEnabled(true);
            cleatTextButton.setEnabled(true);
        } else if(WorkerTaskConstants.TASK_STATUS_DONE.equals(propertyName)
                || WorkerTaskConstants.TASK_STATUS_ABORT.equals(propertyName)){
            scanTextButton.setEnabled(true);
            if(sourceTextUseSamePaletteChkbx.isSelected()){
                sourceTextPaletteNameTextField.setEditable(false);
            } else {
                sourceTextPaletteNameTextField.setEditable(true);
            }
            sourceTextTextArea.setEditable(true);
            sourceTextUseSamePaletteChkbx.setEnabled(true);
            addPaletteButton.setEnabled(true);
            deletePaletteButton.setEnabled(true);
            paletteListComboBox.setEnabled(true);
            newMenuItem.setEnabled(true);
            textScanProgressBar.setVisible(false);
            openTextFileButton.setEnabled(true);
            cleatTextButton.setEnabled(true);
            ColorPalette colorPalette = (ColorPalette) evt.getNewValue();
            ColorPaletteListModel cplm = new ColorPaletteListModel(
                    colorPalette.getColorDataList());
            addColorPalette(colorPalette);
            colorPickedJList.setModel(cplm);
            colorPickedJList.updateUI();
        }
    }
    
    private void addColorPalette(ColorPalette colorPalette) {
        if(null != colorPalette && colorPalette.getColorDataList().size() > 0){
            boolean added = ColorPaletteManager.getInstance().addPalette(colorPalette.getName());
            for(ColorData code: colorPalette.getColorDataList()){
                ColorPanel colorPanel = new ColorPanel(
                        paletteContentPanel, colorPalette.getName());
                colorPanel.setColorGrabbed(true);
                colorPanel.setSelectedColor(Color.decode(code.getColorCode()));
                colorPanel.setSelected(false);
                ColorPaletteManager.getInstance().addPanel(colorPalette.getName(), 
                        colorPanel);
            }
            if(added){
                paletteListComboBox.addItem(colorPalette.getName());
            }
            paletteListComboBox.setSelectedItem(colorPalette.getName());
            paletteListComboBox.updateUI();
            resizePaletteContentPanel();
            paletteContentPanel.updateUI();
        }
    }
    
    
    private class DocumentChangeListener implements DocumentListener{

        private final JTextComponent[] sourceFields;
        private final JComponent targetComponent;

        public DocumentChangeListener(JComponent target, JTextComponent ... sourceFields) {
            this.sourceFields = sourceFields;
            this.targetComponent = target;
        }
        
        public void insertUpdate(DocumentEvent e) {
            changeComponentState();
        }

        public void removeUpdate(DocumentEvent e) {
            changeComponentState();
        }

        public void changedUpdate(DocumentEvent e) {
            changeComponentState();
        }

        private void changeComponentState(){
            if(allFieldsHasText()){
                targetComponent.setEnabled(true);
            } else {
                targetComponent.setEnabled(false);
            }
        }
        
        private boolean allFieldsHasText() {
            if(null != sourceFields && sourceFields.length > 0){
                for (JTextComponent field : sourceFields) {
                    if(null == field.getText() 
                            || "".equals(field.getText().trim())){
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        
    }
}
