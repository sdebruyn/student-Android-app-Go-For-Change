package be.hubrussel.ti.goforchange.enquete.entities;

/**
 * Created by Samuel on 31/03/2014.
 */
public class OpenNumericQuestion extends Question {

    /**
     * If this is a year than the UX should be quite different. Show a slider and suggest the current year. This should also help validation since we only accept years beyond 1980 and before 2100.
     */
    private boolean isYear;
    public final static int MIN_YEAR = 1200;
    public final static int MAX_YEAR = 2000;

    public OpenNumericQuestion(Section section, String description){
        super(section, description);
        setYear(false);
    }

    public boolean isYear() {
        return isYear;
    }

    public void setYear(boolean isYear) {
        this.isYear = isYear;
    }

    public boolean isValidNumber(int number){
        return !(isYear() && (number < MIN_YEAR || number > MAX_YEAR));
    }
}
