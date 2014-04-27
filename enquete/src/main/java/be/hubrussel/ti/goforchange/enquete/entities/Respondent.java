package be.hubrussel.ti.goforchange.enquete.entities;

import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Samuel on 31/03/2014.
 */
public class Respondent implements BaseColumns {

    private final ArrayList<Answer> answers;
    private int id;
    private String companyName;
    private int companyPostal;
    private String companyPerson;
    private String companyEmail;

    public Respondent() {
        answers = new ArrayList<Answer>();
    }

    public Iterator<Answer> getAnswers() {
        return answers.iterator();
    }

    protected void addAnswer(Answer answer) {
        answers.add(answer);
    }

    protected void removeAnswer(Answer answer) {
        answers.remove(answer);
    }

    protected void removeAnswerById(int id) {
        Iterator<Answer> itr = getAnswers();
        Answer toDelete = null;
        do {
            Answer a = itr.next();
            if (a.getId() == id) {
                toDelete = a;
                break;
            }
        } while (itr.hasNext());
        if (toDelete != null)
            answers.remove(toDelete);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getCompanyPostal() {
        return companyPostal;
    }

    public void setCompanyPostal(int companyPostal) {
        this.companyPostal = companyPostal;
    }

    public String getCompanyPerson() {
        return companyPerson;
    }

    public void setCompanyPerson(String companyPerson) {
        this.companyPerson = companyPerson;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }
}
