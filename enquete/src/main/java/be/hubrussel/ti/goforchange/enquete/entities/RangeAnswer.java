package be.hubrussel.ti.goforchange.enquete.entities;

/**
 * Created by Samuel on 31/03/2014.
 */
public class RangeAnswer extends Answer {

    private final int answeredNumber;

    /**
     * @param respondent
     * @param answeredQuestion
     * @param answeredNumber
     * @throws ClassCastException       The answeredQuestion is not a RangeQuestion.
     * @throws IllegalArgumentException The answeredNumber is not valid for the answeredQuestion.
     */
    public RangeAnswer(Respondent respondent, Question answeredQuestion, int answeredNumber) throws ClassCastException, IllegalArgumentException {
        super(respondent, answeredQuestion);

        RangeQuestion question = (RangeQuestion) getAnsweredQuestion();
        if (!question.isValidNumber(answeredNumber))
            throw new IllegalArgumentException();

        this.answeredNumber = answeredNumber;
    }

    public int getAnsweredNumber() {
        return answeredNumber;
    }

    @Override
    public String toString() {
        return String.valueOf(getAnsweredNumber());
    }
}
