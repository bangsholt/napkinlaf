package hexgo;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Jan 6, 2003
 * Time: 1:37:59 PM
 * To change this template use Options | File Templates.
 */
public interface Snapable extends Serializable {
    public Snapable snapshot();
}
