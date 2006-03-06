// $Id$

package napkin.dev;

import napkin.util.RandomValue;
import napkin.util.RandomValueSource;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.text.DecimalFormat;

class RandomValueSpinner extends JPanel implements RandomValueSource {

    private final RandomValue value;
    private final SpinnerNumberModel midModel;
    private SpinnerNumberModel rangeModel;
    private JLabel adjustLabel;

    RandomValueSpinner(String name, RandomValue value, double min, double max,
            int steps) {
        this(name, value, min, max, steps, true);
    }

    RandomValueSpinner(String name, final RandomValue value, double min,
            double max,
            int steps, boolean randomized) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(new TitledBorder(name));

        this.value = value;

        double range = value.getRange();
        double init = value.getMid();
        double stepSize = (max - min) / steps;
        midModel = new SpinnerNumberModel(init, min, max, stepSize);
        add(new JLabel(randomized ? "mid" : "val"));
        add(makeSpinner(midModel, "#0.00"));
        midModel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                value.setMid(midModel.getNumber().doubleValue());
            }
        });

        if (randomized) {
            add(new JLabel("range"));
            add(makeRandomizer(range));

            add(new JLabel("adj "));
            adjustLabel = new JLabel("", SwingConstants.RIGHT);
            showAdjust();
            add(adjustLabel);
        }
    }

    private JSpinner makeRandomizer(double range) {
        rangeModel = new SpinnerNumberModel(range, 0, 20, 0.1);
        rangeModel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                value.setRange(rangeModel.getNumber().doubleValue());
            }
        });
        return makeSpinner(rangeModel, GeneratorTest.DECIMAL.toPattern());
    }

    private void showAdjust() {
        if (adjustLabel != null) {
            double adjust = value.getAdjust();
            adjustLabel.setText(GeneratorTest.DECIMAL.format(adjust));
        }
    }

    private static JSpinner makeSpinner(SpinnerModel model, String pattern) {
        JSpinner spinner = new JSpinner(model);
        DecimalFormat format =
                ((JSpinner.NumberEditor) spinner.getEditor()).getFormat();
        format.applyPattern(pattern);
        model.addChangeListener(GeneratorTest.NEWPOINTS);
        return spinner;
    }

    public void randomize() {
        value.randomize();
        showAdjust();
    }

    public double get() {
        return value.get();
    }

    public double generate() {
        return value.generate();
    }

    public double getMid() {
        return value.getMid();
    }

    public double getRange() {
        return value.getRange();
    }

    public double getAdjust() {
        return value.getAdjust();
    }
}

