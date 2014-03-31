package be.hubrussel.ti.goforchange.enquete.entities;

/**
 * Created by Samuel on 31/03/2014.
 */
public abstract class Answer {

    private final Respondent respondent;
    private int id;
    private final Question answeredQuestion;

    public Answer(Respondent respondent, Question answeredQuestion){
        this.respondent = respondent;
        this.answeredQuestion = answeredQuestion;
    }

    public Respondent getRespondent() {
        return respondent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Question getAnsweredQuestion() {
        return answeredQuestion;
    }
}
