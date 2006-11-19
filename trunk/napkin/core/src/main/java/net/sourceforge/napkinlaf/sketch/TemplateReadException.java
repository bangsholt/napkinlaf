package net.sourceforge.napkinlaf.sketch;

/**
 * Thrown when there is an exception while trying to read a <tt>Template</tt>.
 *
 * @author Peter Goodspeed
 */
@SuppressWarnings({"WeakerAccess"})
public class TemplateReadException extends Exception {
    public TemplateReadException(Throwable cause) {
        super(cause);
    }
}
