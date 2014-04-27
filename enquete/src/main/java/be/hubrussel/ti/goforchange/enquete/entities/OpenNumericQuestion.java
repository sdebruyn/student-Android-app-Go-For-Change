package be.hubrussel.ti.goforchange.enquete.entities;

/**
 * Created by Samuel on 31/03/2014.
 */
public class OpenNumericQuestion extends Question {

    public final static int MIN_YEAR = 1400;
    public final static int MAX_YEAR = 2200;
    /**
     * If this is a year than the UX should be quite different. Show a slider and suggest the current year. This should also help validation since we only accept years beyond 1980 and before 2100.
     */
    private boolean isYear;
    private int min;
    private int max;

    public OpenNumericQuestion(Section section, String description) {
        super(section, description);
        setYear(false);
        setMin(0);
        setMax(100000);
    }

    public boolean isYear() {
        return isYear;
    }

    public void setYear(boolean isYear) {
        this.isYear = isYear;
    }

    public boolean isValidNumber(int number) {
        if (isYear() && number > MIN_YEAR || number < MAX_YEAR) {
            return true;
        } else if (!isYear() && number >= getMin() && number <= getMax()) {
            return true;
        }
        return false;
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
}
