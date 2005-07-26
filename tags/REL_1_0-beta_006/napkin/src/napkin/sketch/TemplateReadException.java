// $Id$

package napkin.sketch;

/**
 * TemplateReadException: Thrown when there is an exception while trying to read
 * a Template
 *
 * @author peterg
 */
public class TemplateReadException extends Exception {
    /**
     *
     */
    public TemplateReadException() {
        super();
    }

    /** @param message  */
    public TemplateReadException(String message) {
        super(message);
    }

    /** @param cause  */
    public TemplateReadException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public TemplateReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
