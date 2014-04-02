package be.hubrussel.ti.goforchange.enquete.entities;

import android.provider.BaseColumns;

/**
 * Created by Samuel on 31/03/2014.
 */
public class Choice implements BaseColumns {

    private MultipleChoiceQuestion question;
    private int id;
    private String text;
    private Question nextQuestion;
    private boolean shouldEnd;

    public Choice(String text){
        setText(text);
        setNextQuestion(null);
        setShouldEnd(false);
    }

    public MultipleChoiceQuestion getQuestion() {
        return question;
    }

    /**
     *
     * @param question
     * @throws UnsupportedOperationException You can only set this to a question of the previous setting was null.
     */
    protected void setQuestion(MultipleChoiceQuestion question) throws UnsupportedOperationException {
        if(this.question != null)
            throw new UnsupportedOperationException();

        this.question = question;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Question getNextQuestion() {
        return nextQuestion;
    }

    public void setNextQuestion(Question nextQuestion) {
        this.nextQuestion = nextQuestion;
    }

    public boolean shouldEnd() {
        return shouldEnd;
    }

    public void setShouldEnd(boolean shouldEnd) {
        this.shouldEnd = shouldEnd;
    }
}
