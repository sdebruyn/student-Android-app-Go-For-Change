package be.hubrussel.ti.goforchange.enquete.entities;

/**
 * Created by Samuel on 31/03/2014.
 */
public class OpenNumericAnswer extends Answer {

    private final int answeredNumber;

    /**
     *
     * @param respondent
     * @param answeredQuestion
     * @param answeredNumber
     * @throws ClassCastException The answeredQuestion is not a OpenNumericQuestion.
     * @throws IllegalArgumentException The answeredNumber is not valid for the answeredQuestion.
     */
    public OpenNumericAnswer(Respondent respondent, Question answeredQuestion, int answeredNumber) throws ClassCastException, IllegalArgumentException {
        super(respondent, answeredQuestion);

        OpenNumericQuestion question = (OpenNumericQuestion) getAnsweredQuestion();
        if(!question.isValidNumber(answeredNumber))
            throw new IllegalArgumentException();

        this.answeredNumber = answeredNumber;
    }

    public int getAnsweredNumber() {
        return answeredNumber;
    }
}
