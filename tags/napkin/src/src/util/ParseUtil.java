package util;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class holds the static <CODE>parseGroups</CODE> method.
 */
public class ParseUtil {
    /**
     * Break up a comma-separated list of groups into an array of strings.
     *
     * @param groupDesc	A comma-separated list of groups.
     * @return		An array of strings (empty if none were specified).
     */
    public static String[] parseGroups(String groupDesc) {
	if (groupDesc.equals(""))
	    return new String[] {""};
	Set groups = new HashSet();
	StringTokenizer strs = new StringTokenizer(groupDesc, ", \t\n");
	while (strs.hasMoreTokens())
	    groups.add(strs.nextToken());
	return (String[]) groups.toArray(new String[groups.size()]);
    }
}
