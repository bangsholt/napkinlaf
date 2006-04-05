/*
 * FontUIResource.java
 *
 * Created on 05 April 2006, 06:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sourceforge.napkinlaf.fonts;

import java.awt.Font;
import java.lang.reflect.Field;
import javax.swing.plaf.UIResource;

/**
 *
 * @author Alex Lam Sze Lok
 */
public class FontUIResource extends Font implements UIResource {
    
    /** Creates a new instance of FontUIResource */
    public FontUIResource(Font font) {
        super(font.getAttributes());
        /*
         * Bug 6313541 (fixed in Mustang) prevents the bundled fonts loading
         * (because the font2DHandle field is not transferred when calling
         * FontUIResource).  The workaround uses reflection, which might not
         * work for applets and Web Start applications, so here I've put in
         * checks so the workaround is used only when needed.
         */
        if (!getFontName().equals(font.getFontName())) {
            try {
                // transfer private field font2DHandle
                Field field = Font.class.getDeclaredField("font2DHandle");
                field.setAccessible(true);
                field.set(this, field.get(font));
                field.setAccessible(false);
                // transfer private field createdFont
                field = Font.class.getDeclaredField("createdFont");
                field.setAccessible(true);
                field.set(this, field.get(font));
                field.setAccessible(false);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (SecurityException ex) {
                ex.printStackTrace();
            } catch (NoSuchFieldException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }
    
}
