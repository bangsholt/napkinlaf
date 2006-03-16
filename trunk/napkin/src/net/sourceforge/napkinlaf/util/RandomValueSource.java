// $Id: RandomValueSource.java 279 2006-02-25 15:15:25Z alexlamsl $

package net.sourceforge.napkinlaf.util;

public interface RandomValueSource {

    void randomize();

    double get();

    double generate();

    double getMid();

    double getRange();

    double getAdjust();
}

