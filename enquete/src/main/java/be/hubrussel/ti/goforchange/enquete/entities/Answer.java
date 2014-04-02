package be.hubrussel.ti.goforchange.enquete.entities;

import android.provider.BaseColumns;

/**
 * Created by Samuel on 31/03/2014.
 */
public abstract class Answer implements BaseColumns {

    private final Respondent respondent;
    private int id;
    private final Question answeredQuestion;

    public Answer(Respondent respondent, Question answeredQuestion){
        this.answeredQuestion = answeredQuestion;

        this.respondent = respondent;
        respondent.addAnswer(this);
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

    @Override
    public abstract String toString();
}
