/**
 * ---------------------------------------------------------------------------*
 * Copyright Sabuj Das | sabuj.das@gmail.com all rights reserved.
 * <br/>
 * This document cannot be copied, modified or re-distributed without prior
 * permission from the author.
 * ---------------------------------------------------------------------------*
 * Type : com.gs.tools.colorhound.ui.ColorPanel Date : May 23, 2013
 */
package com.gs.tools.colorhound.ui;

import com.gs.tools.colorhound.event.ApplicationEventManager;
import com.gs.tools.colorhound.event.ColorGrabEvent;
import com.gs.tools.colorhound.event.ColorPanelSelectedEvent;
import com.gs.tools.colorhound.event.ColorPanelSelectedEventListener;
import com.gs.tools.colorhound.util.GraphicsUtil;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/**
 *
 * @author Sabuj Das | sabuj.das@gmail.com
 */
public class ColorPanel extends JPanel implements MouseListener, FocusListener,
        KeyListener
{

    private final JPanel parentPanel;
    private final String paletteName;
    private Color selectedColor;
    private String colorHexCode;
    private String tagName;
    private boolean selected = false;
    private boolean tagged = false;
    public final static int MAX_WIDTH = 85;
    public final static int MAX_HEIGHT = 65;
    public final static int TEXT_HEIGHT = 21;
    private boolean colorGrabbed = false;
    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n/Message");
    private JPopupMenu colorPanelPopupMenu;
    private JMenuItem chooseColorMenuItem;
    private JMenuItem pickColorMenuItem;
    private JMenuItem tagColorMenuItem;
    private JMenuItem remTagColorMenuItem;
    private JMenuItem deleteColorMenuItem;

    public ColorPanel(JPanel parent, final String paletteName) {
        this.parentPanel = parent;
        this.paletteName = paletteName;
        
        selectedColor = Color.WHITE;
        colorHexCode = GraphicsUtil.encodeColor(Color.BLACK);
        Dimension d = new Dimension(MAX_WIDTH, MAX_HEIGHT);
        setMaximumSize(d);
        setMinimumSize(d);
        setPreferredSize(d);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        setLayout(null);
        setDoubleBuffered(true);
        addMouseListener(this);
        addKeyListener(this);
        //addFocusListener(this);
        setToolTipText(resourceBundle.getString("tip.color.panel.info"));

        
        colorPanelPopupMenu = new JPopupMenu();

        chooseColorMenuItem = new JMenuItem();
        chooseColorMenuItem.setText(resourceBundle.getString("lbl.choose.color.menu.item"));
        chooseColorMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(ColorPanel.this,
                        resourceBundle.getString("lbl.color.chooser.title"),
                        selectedColor);
                if (null != newColor) {
                    selectedColor = newColor;
                    colorGrabbed = true;
                    ApplicationEventManager.getInstance().fireEvent(
                            new ColorGrabEvent(selectedColor, selectedColor, ColorPanel.this));
                    updateUI();
                    parentPanel.updateUI();
                }
            }
        });

        pickColorMenuItem = new JMenuItem();
        pickColorMenuItem.setText(resourceBundle.getString("lbl.pick.color.menu.item"));
        pickColorMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selected = true;
                colorGrabbed = false;
                ColorPaletteManager.getInstance().selectPanel(paletteName, ColorPanel.this);
                updateUI();
                parentPanel.updateUI();
                ColorPanelSelectedEvent event = new ColorPanelSelectedEvent(false, selected, ColorPanel.this);
                if (colorGrabbed) {
                    event.setSelectedColor(selectedColor);
                }
                ApplicationEventManager.getInstance().fireEvent(event);
                
            }
        });
        
        tagColorMenuItem = new JMenuItem();
        tagColorMenuItem.setText(resourceBundle.getString("lbl.tag.color.menu.item"));
        remTagColorMenuItem = new JMenuItem();
        remTagColorMenuItem.setText(resourceBundle.getString("lbl.rem.tag.color.menu.item"));
        deleteColorMenuItem = new JMenuItem();
        deleteColorMenuItem.setText(resourceBundle.getString("lbl.delete.color.menu.item"));

        colorPanelPopupMenu.add(chooseColorMenuItem);
        colorPanelPopupMenu.add(pickColorMenuItem);
        colorPanelPopupMenu.add(new JSeparator());
        colorPanelPopupMenu.add(tagColorMenuItem);
        colorPanelPopupMenu.add(remTagColorMenuItem);
        colorPanelPopupMenu.add(new JSeparator());
        colorPanelPopupMenu.add(deleteColorMenuItem);
        setComponentPopupMenu(colorPanelPopupMenu);
    }

    public JPanel getParentPanel() {
        return parentPanel;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (MouseEvent.BUTTON1 == e.getButton()) {
            requestFocus();
            selected = true;
            ColorPaletteManager.getInstance().selectPanel(paletteName, this);
            updateUI();
            parentPanel.updateUI();
            ColorPanelSelectedEvent event = new ColorPanelSelectedEvent(false, selected, this);
            if (colorGrabbed) {
                event.setSelectedColor(selectedColor);
            }
            ApplicationEventManager.getInstance().fireEvent(event);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void focusGained(FocusEvent e) {
        
    }

    @Override
    public void focusLost(FocusEvent e) {
        colorPanelSelectionRemoved();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE && selected == true){
            ColorPanelSelectedEvent event = new ColorPanelSelectedEvent(selected, false, this);
            event.setOldSelectedPanel(this);
            ApplicationEventManager.getInstance().fireEvent(event);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }

    @Override
    public void paint(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        
        GraphicsUtil.addRendererHint(g);
        
        g.setColor((selected ? Color.MAGENTA : Color.BLUE));
        BasicGraphicsUtils.drawDashedRect(g, 0, 0, MAX_WIDTH, MAX_HEIGHT);
        if (null == selectedColor) {
            return;
        }
        int paintAreaX = 4;
        int paintAreaY = 4;
        int paintAreaW = MAX_WIDTH - 8;
        int paintAreaH = MAX_HEIGHT - 8;
        
        g.setColor(selectedColor);
        Rectangle paintAreaRectangle = new Rectangle(
                paintAreaX, paintAreaY, paintAreaW, paintAreaH
                );
        g.draw(paintAreaRectangle);
        
        Rectangle fillAreaRectangle = new Rectangle(
                paintAreaX, paintAreaY, paintAreaW, paintAreaH - TEXT_HEIGHT
                );
        g.fill(fillAreaRectangle);
        
        g.setFont(UiConstants.Fonts.TAHOMA_BOLD_9_5);
        //g.setColor(UiConstants.Colors.TAG_TXT_FG);
        g.setColor((selected ? Color.MAGENTA : UiConstants.Colors.TAG_TXT_FG));
        g.drawString(GraphicsUtil.encodeColor(selectedColor), 8, MAX_HEIGHT-9);
    }


    public void colorPanelSelectionRemoved() {
        selected = false;
        ColorPaletteManager.getInstance().selectPanel(paletteName, this, false);
        updateUI();
        parentPanel.updateUI();
        ColorPanelSelectedEvent event = new ColorPanelSelectedEvent(false, selected, this);
        if (colorGrabbed) {
            event.setSelectedColor(selectedColor);
        }
        ApplicationEventManager.getInstance().fireEvent(event);
    }

    
    
    public void setColorGrabbed(boolean b) {
        this.colorGrabbed = b;
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public String getColorHexCode() {
        return colorHexCode;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isColorGrabbed() {
        return colorGrabbed;
    }
}
