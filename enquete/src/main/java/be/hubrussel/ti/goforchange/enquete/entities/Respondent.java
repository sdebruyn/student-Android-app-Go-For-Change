package be.hubrussel.ti.goforchange.enquete.entities;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Samuel on 31/03/2014.
 */
public class Respondent {

    private int id;
    private String companyName;
    private String companyPostal;
    private String companyPerson;
    private String companyEmail;
    private final ArrayList<Answer> answers;

    public Respondent(){
        answers = new ArrayList<Answer>();
    }

    public Iterator<Answer> getAnswers(){
        return answers.iterator();
    }

    public void addAnswer(Answer answer){
        answers.add(answer);
    }

    public void removeAnswer(Answer answer){
        answers.remove(answer);
    }

    public void removeAnswerById(int id){
        Iterator<Answer> itr = getAnswers();
        Answer toDelete = null;
        do{
            Answer a = itr.next();
            if(a.getId() == id){
                toDelete = a;
                break;
            }
        }while(itr.hasNext());
        if(toDelete != null)
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

    public String getCompanyPostal() {
        return companyPostal;
    }

    public void setCompanyPostal(String companyPostal) {
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
