package be.hubrussel.ti.goforchange.enquete.entities;

/**
 * Created by Samuel on 31/03/2014.
 */
public class OpenTextAnswer extends Answer {

    private final String answeredText;

    /**
     *
     * @param respondent
     * @param answeredQuestion
     * @param answeredText
     * @throws ClassCastException The answeredQuestion is not a OpenTextQuestion.
     */
    public OpenTextAnswer(Respondent respondent, Question answeredQuestion, String answeredText) throws ClassCastException {
        super(respondent, answeredQuestion);

        if(answeredQuestion.getClass() != OpenTextQuestion.class)
            throw new ClassCastException();

        this.answeredText = answeredText;
    }

    public String getAnsweredText() {
        return answeredText;
    }

    @Override
    public String toString() {
        return getAnsweredText();
    }
}
