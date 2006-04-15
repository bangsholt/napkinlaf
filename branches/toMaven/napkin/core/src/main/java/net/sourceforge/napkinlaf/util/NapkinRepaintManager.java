/*
 * NapkinRepaintManager.java
 *
 * Created on 15 April 2006, 23:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sourceforge.napkinlaf.util;

import java.applet.Applet;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Window;
import javax.swing.JComponent;
import javax.swing.RepaintManager;

/**
 *
 * @author Alex Lam Sze Lok
 */
public class NapkinRepaintManager extends RepaintManager {

    private final RepaintManager manager;
    
    /** Creates a new instance of NapkinRepaintManager */
    public NapkinRepaintManager(RepaintManager manager) {
        this.manager = manager;
    }

    public RepaintManager getManager() {
        return manager;
    }

    /**
     * Search up the hierarchy for a Napkin component (the parent component),
     * and calculates the given region in the parent component's coordinates.
     */
    private void repaintNapkinParent(JComponent component) {
        repaintNapkinParent(component, component.getVisibleRect());
    }

    private void repaintNapkinParent(JComponent component, Rectangle region) {
        Container container = component;
        Rectangle bounds = new Rectangle();
        // loop for a matching Napkin parent
        while (container != null && !NapkinUtil.isNapkinInstalled(container)) {
            container.getBounds(bounds);
            container = container.getParent();
            region.x += bounds.x;
            region.y += bounds.y;
        }
        // repaint the relevant region in parent
        if (container != component &&
                NapkinUtil.isNapkinInstalled(container)) {

            manager.addDirtyRegion((JComponent) container,
                    region.x, region.y, region.width, region.height);
        }
    }

    /**
     * Methods that will cause possible repaints in Napkin parent components
     */

    @Override
    public void markCompletelyDirty(JComponent aComponent) {
        repaintNapkinParent(aComponent);
        manager.markCompletelyDirty(aComponent);
    }

    @Override
    public void addInvalidComponent(JComponent invalidComponent) {
        repaintNapkinParent(invalidComponent);
        manager.addInvalidComponent(invalidComponent);
    }

    @Override
    public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
        repaintNapkinParent(c, new Rectangle(x, y, w, h));
        manager.addDirtyRegion(c, x, y, w, h);
    }

    /**
     * Simple delegations
     */

    @Override
    public void addDirtyRegion(Applet applet, int x, int y, int w, int h) {
        manager.addDirtyRegion(applet, x, y, w, h);
    }

    @Override
    public void addDirtyRegion(Window window, int x, int y, int w, int h) {
        manager.addDirtyRegion(window, x, y, w, h);
    }

    @Override
    public Image getOffscreenBuffer(Component c, int proposedWidth, int proposedHeight) {
        return manager.getOffscreenBuffer(c, proposedWidth, proposedHeight);
    }

    @Override
    public Image getVolatileOffscreenBuffer(Component c, int proposedWidth, int proposedHeight) {
        return manager.getVolatileOffscreenBuffer(c, proposedWidth, proposedHeight);
    }

    @Override
    public void setDoubleBufferMaximumSize(Dimension d) {
        manager.setDoubleBufferMaximumSize(d);
    }

    @Override
    public void setDoubleBufferingEnabled(boolean aFlag) {
        manager.setDoubleBufferingEnabled(aFlag);
    }

    @Override
    public Rectangle getDirtyRegion(JComponent aComponent) {
        return manager.getDirtyRegion(aComponent);
    }

    @Override
    public boolean isCompletelyDirty(JComponent aComponent) {
        return manager.isCompletelyDirty(aComponent);
    }

    @Override
    public void markCompletelyClean(JComponent aComponent) {
        manager.markCompletelyClean(aComponent);
    }

    @Override
    public void removeInvalidComponent(JComponent component) {
        manager.removeInvalidComponent(component);
    }

    @Override
    public void validateInvalidComponents() {
        manager.validateInvalidComponents();
    }

    @Override
    public Dimension getDoubleBufferMaximumSize() {
        return manager.getDoubleBufferMaximumSize();
    }

    @Override
    public boolean isDoubleBufferingEnabled() {
        return manager.isDoubleBufferingEnabled();
    }

    @Override
    public void paintDirtyRegions() {
        manager.paintDirtyRegions();
    }

    /**
     * Object methods override
     */

    @Override
    public String toString() {
        return "NapkinRepaintManager {" + manager.toString() +"}";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NapkinRepaintManager &&
                manager.equals(((NapkinRepaintManager) obj).manager);
    }

    @Override
    public int hashCode() {
        return NapkinRepaintManager.class.hashCode() ^ manager.hashCode();
    }
}
