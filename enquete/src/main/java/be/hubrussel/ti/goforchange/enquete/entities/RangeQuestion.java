package be.hubrussel.ti.goforchange.enquete.entities;

/**
 * Created by Samuel on 31/03/2014.
 */
public class RangeQuestion extends Question {

    private int min;
    private int max;
    private int step;

    /**
     * @post Minimum is 1, maximum is 7, step is 1. These are the default settings.
     * @param section
     * @param description
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

    public void setStep(int step) {
        this.step = step;
    }

    public boolean isValidNumber(int number){
        if(number < getMin() || number > getMax() || ((number+getMin())%getStep() != 0))
            return false;

        return true;
    }
}
