package com.gs.tools.colorhound;

import com.gs.tools.colorhound.ui.ColorHoundBaseFrame;
import java.awt.EventQueue;
import javax.swing.UIManager;

/**
 * Hello world!
 *
 */
public class ColorHound 
{
    public static void main( String[] args )
    {
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex){}
        //Locale.setDefault(new Locale("es"));
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ColorHoundBaseFrame().setVisible(true);
            }
        });
    }
}
