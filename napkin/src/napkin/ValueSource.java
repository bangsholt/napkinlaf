// $Id$

package napkin;

public interface ValueSource {

    void randomize();

    double get();

    double generate();

    double getMid();

    double getRange();

    double getAdjust();
}

