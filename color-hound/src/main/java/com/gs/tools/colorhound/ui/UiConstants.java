/** ---------------------------------------------------------------------------*
 * Copyright Sabuj Das | sabuj.das@gmail.com all rights reserved.
 * <br/>
 * This document cannot be copied, modified or re-distributed without prior 
 * permission from the author.
 *  ---------------------------------------------------------------------------* 
 * Type     : com.gs.tools.colorhound.ui.UiConstants
 * Date     : Jun 5, 2013
 */

package com.gs.tools.colorhound.ui;

import java.awt.Color;
import java.awt.Font;
import java.io.File;

/**
 *
 * @author Sabuj Das | sabuj.das@gmail.com
 */
public interface UiConstants {

    public static class Fonts{
        public static final String FONT_PATH = "/fonts";
        public static final Font DEFAULT_FONT;
        public static final Font TAHOMA_PLAIN;
        public static final Font TAHOMA_BOLD;
        public static final Font TAHOMA_PLAIN_8;
        public static final Font TAHOMA_PLAIN_9;
        public static final Font TAHOMA_PLAIN_10_5;
        public static final Font TAHOMA_BOLD_9_5;
        public static final Font VERA_MONO;
        public static final Font VERA_MONO_PLAIN_9_5;
        static{
            Font font = Font.decode(Font.SANS_SERIF);
            try {
                 font = Font.createFont(Font.TRUETYPE_FONT,
                        UiConstants.class.getResourceAsStream(FONT_PATH 
                         + File.separator + "TAHOMA.TTF" ));
            } catch (Exception ex) { }
            TAHOMA_PLAIN = font;
            DEFAULT_FONT = TAHOMA_PLAIN.deriveFont(Font.PLAIN, 11.0F);
            TAHOMA_PLAIN_8 = TAHOMA_PLAIN.deriveFont(Font.PLAIN, 8.0F);
            TAHOMA_PLAIN_9 = TAHOMA_PLAIN.deriveFont(Font.PLAIN, 9.0F);
            TAHOMA_PLAIN_10_5 = TAHOMA_PLAIN.deriveFont(Font.PLAIN, 10.5F);
            
            try {
                 font = Font.createFont(Font.TRUETYPE_FONT,
                        UiConstants.class.getResourceAsStream(FONT_PATH 
                         + File.separator + "TAHOMABD.TTF" ));
            } catch (Exception ex) { }
            TAHOMA_BOLD = font;
            TAHOMA_BOLD_9_5 = TAHOMA_BOLD.deriveFont(Font.PLAIN, 9.5F);
            
            try {
                 font = Font.createFont(Font.TRUETYPE_FONT,
                        UiConstants.class.getResourceAsStream(FONT_PATH 
                         + File.separator + "VeraMono.ttf" ));
            } catch (Exception ex) { }
            VERA_MONO = font;
            VERA_MONO_PLAIN_9_5 = VERA_MONO.deriveFont(Font.PLAIN, 9.5F);
        }
        
    }
    
    public interface Colors{
        Color TAG_TXT_FG = Color.decode("0x5CE89C");
    }
}
