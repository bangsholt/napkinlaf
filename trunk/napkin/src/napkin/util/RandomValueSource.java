// $Id$

package napkin.util;

public interface RandomValueSource {

    void randomize();

    double get();

    double generate();

    double getMid();

    double getRange();

    double getAdjust();
}
