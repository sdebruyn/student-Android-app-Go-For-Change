package be.hubrussel.ti.goforchange.enquete.entities;

/**
 * Created by Samuel on 31/03/2014.
 */
public class RangeQuestion extends Question {

    private int min;
    private int max;
    private int step;

    /**
     * @param section
     * @param description
     * @post Minimum is 1, maximum is 7, step is 1. These are the default settings.
     */
    public RangeQuestion(Section section, String description) {
        super(section, description);
        setMin(1);
        setMax(7);
        setStep(1);
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getStep() {
        return step;
    }

    /**
     * @param step The step for this range. This step should be valid for the set max and min. That means that ((max - min) % step) should be zero.
     */
    public void setStep(int step) {
        if (((getMax() - getMin()) % step) == 0)
            this.step = step;
    }

    public boolean isValidNumber(int number) {
        return !(number < getMin() || number > getMax() || ((number + getMin()) % getStep() != 0));
    }

    public int getSeekBarMax() {
        return (getMax() - getMin()) / getStep();
    }

    public int getRealValueFromSeekBar(int fromSeekBar) {
        return getMin() + fromSeekBar * getStep();
    }

    public int getSeekBarStart() {
        int max = getSeekBarMax();
        int min = 0;
        double half = ((max - min) / 2) + min;
        long rounded = Math.round(half);
        return (int) rounded;

    }
}
