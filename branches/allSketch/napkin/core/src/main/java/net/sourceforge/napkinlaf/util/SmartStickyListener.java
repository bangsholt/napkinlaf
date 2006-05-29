/*
 * SmartStickyListener.java
 *
 * Created on 27 April 2006, 21:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sourceforge.napkinlaf.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JComponent;

/**
 *
 * @author Alex Lam Sze Lok
 */
public abstract class SmartStickyListener<T> implements PropertyChangeListener {
    
    private static final String listenerKey = "SmartStickyListeners";
    private SmartStickyListener<?> nextNode = null;

    private AtomicBoolean overriding = new AtomicBoolean(false);

    private String recordKey;
    private String propKey;

    /**
     * Subclasses would supply their keys for setter-value backup (record) and
     * property name to listen changes for.
     */
    public SmartStickyListener(String recordKey, String propKey) {

        this.recordKey = recordKey;
        this.propKey = propKey;
    }
    
    /**
     * Adds the specified smart listener to the given component.
     * <BR/><BR/>
     * <B>Note:</B><BR/>
     * This method is not thread-safe; so ensure it to operate correctly in the
     * case when multiple threads might call this method, you will need to
     * synchronise with respect to the JComponent parameter:<PRE>
     *   synchronized(c) {
     *       SmartStickyListener.hookListener(c, listener);
     *   }</PRE>
     */
    public static void hookListener(
            JComponent c, SmartStickyListener<?> listener) {

        if (listener.nextNode != null) {
            throw new IllegalArgumentException(
                    "listener cannot hook to multiple components");
        }
        SmartStickyListener<?> listeners =
                (SmartStickyListener<?>) c.getClientProperty(listenerKey);
        listener.nextNode = listeners;
        c.putClientProperty(listenerKey, listener);
        c.addPropertyChangeListener(listener.propKey, listener);
    }
    
    /**
     * Removes all smart listeners from the given component.
     * <BR/><BR/>
     * <B>Note:</B><BR/>
     * This method is not thread-safe; so ensure it to operate correctly in the
     * case when multiple threads might call this method, you will need to
     * synchronise with respect to the JComponent parameter:<PRE>
     *   synchronized(c) {
     *       SmartStickyListener.unhookListeners(c);
     *   }</PRE>
     */
    public static void unhookListeners(JComponent c) {
        SmartStickyListener<?> listener =
                (SmartStickyListener<?>) c.getClientProperty(listenerKey);
        c.putClientProperty(listenerKey, null);
        while (listener != null) {
            c.removePropertyChangeListener(listener.propKey, listener);
            listener = listener.nextNode;
        }
    }

    /**
     * Specifies whether we should record the new value from the setter call.
     */
    protected abstract boolean shouldRecord(T newValue);

    /**
     * Implements any overriding behaviour here; successive calls to setter
     * methods will not double-trigger this smart listener, so it won't fall
     * into infinite loops.
     */
    protected abstract void overrideValue(JComponent c, T newValue);

    /** {@inheritDoc} */
    public void propertyChange(PropertyChangeEvent event) {
        // check if this is an external call
        if (overriding.compareAndSet(false, true)) {
            JComponent c = (JComponent) event.getSource();
            @SuppressWarnings("unchecked") T newValue = (T) event.getNewValue();
            if (shouldRecord(newValue)) {
                c.putClientProperty(recordKey, newValue);
            }
            overrideValue(c, newValue);
            overriding.set(false);
        }
    }
}
