package net.sourceforge.napkinlaf.util;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicBoolean;

/** @author Alex Lam Sze Lok */
@SuppressWarnings({"JavaDoc"})
public abstract class SmartStickyListener<T> implements PropertyChangeListener {

    private SmartStickyListener<?> nextNode = null;

    private final AtomicBoolean overriding = new AtomicBoolean(false);

    private String recordKey;
    private String propKey;

    private static final String LISTENER_KEY = "SmartStickyListeners";

    /**
     * Subclasses would supply their keys for setter-value backup (record) and
     * property name to listen changes for.
     */
    public SmartStickyListener(String recordKey, String propKey) {
        this.recordKey = recordKey;
        this.propKey = propKey;
    }

    /**
     * Adds the specified smart listener to the given component. <BR/><BR/>
     * <B>Note:</B><BR/> This method is not thread-safe; so ensure it to operate
     * correctly in the case when multiple threads might call this method, you
     * will need to synchronise with respect to the JComponent parameter:<PRE>
     * synchronized(c) { SmartStickyListener.hookListener(c, listener); }</PRE>
     */
    public static void hookListener(JComponent c,
            SmartStickyListener<?> listener) {

        if (listener.nextNode != null) {
            throw new IllegalArgumentException(
                    "listener cannot hook to multiple components");
        }
        listener.nextNode = (SmartStickyListener<?>) c.getClientProperty(
                LISTENER_KEY);
        c.putClientProperty(LISTENER_KEY, listener);
        c.addPropertyChangeListener(listener.propKey, listener);
    }

    /**
     * Removes all smart listeners from the given component. <BR/><BR/>
     * <B>Note:</B><BR/> This method is not thread-safe; so ensure it to operate
     * correctly in the case when multiple threads might call this method, you
     * will need to synchronise with respect to the JComponent parameter:<PRE>
     * synchronized(c) { SmartStickyListener.unhookListeners(c); }</PRE>
     */
    public static void unhookListeners(JComponent c) {
        SmartStickyListener<?> listener =
                (SmartStickyListener<?>) c.getClientProperty(LISTENER_KEY);
        c.putClientProperty(LISTENER_KEY, null);
        while (listener != null) {
            c.removePropertyChangeListener(listener.propKey, listener);
            listener = listener.nextNode;
        }
    }

    /** Specifies whether we should record the new value from the setter call. */
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
