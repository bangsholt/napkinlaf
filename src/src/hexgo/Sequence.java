// $Header$

/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Sep 10, 2002
 * Time: 9:44:58 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package hexgo;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a (simple) generic container for a sequence of objects indexed
 * starting at an arbitrary value.  Indices may be negative.  The values are
 * assumed to be dense within the range, so this should not be used for sparse
 * sequences.
 */
public class Sequence {
    private List contents = new ArrayList();	// the values
    private int base = 0;			// the base values

    /**
     * Creates a new empty <CODE>Sequence</CODE>.
     */
    public Sequence() {
    }

    /**
     * Sets the value for the given index.
     *
     * @param index The index.
     * @param obj   The value.
     */
    public void set(int index, Object obj) {
        if (index < base) {
            for (int i = index; i < base; i++)
                contents.add(0, null);
            base = index;
        }
        int adjusted = index - base;
        for (int i = contents.size(); i <= adjusted; i++)
            contents.add(null);
        contents.set(adjusted, obj);
    }

    /**
     * Returns the value for the given index.  If the index is out of range,
     * <CODE>null</CODE> is returned.
     *
     * @param index The index.
     *
     * @return The value for the index.
     */
    public Object get(int index) {
        int adjusted = index - base;
        if (index < base || adjusted >= contents.size())
            return null;
        else
            return contents.get(adjusted);
    }

    /**
     * Returns the minimum index.
     *
     * @return The minimum index.
     */
    public int min() {
        return base;
    }

    /**
     * Returns the maximum index.
     *
     * @return The maximum index.
     */
    public int max() {
        return contents.size() + base;
    }

    /**
     * Returns the number of values in the sequence between the min and max.
     *
     * @return The number of values in the sequence between the min and max.
     */
    public int length() {
        return contents.size();
    }
}
