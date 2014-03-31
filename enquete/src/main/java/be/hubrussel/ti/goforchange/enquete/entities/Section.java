package be.hubrussel.ti.goforchange.enquete.entities;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Samuel on 31/03/2014.
 */
public class Section {

    private int id;
    private String name;
    private final ArrayList<Question> questions;

    public Section(String name){
        questions = new ArrayList<Question>();
        this.setName(name);
    }

    public void addQuestion(Question question){
        questions.add(question);
        question.setSection(this);
    }

    public Iterator<Question> getQuestions(){
        return questions.iterator();
    }

    public void removeQuestion(Question question){
        questions.remove(question);
    }

    public void removeQuestionById(int id){
        Iterator<Question> itr = getQuestions();
        Question toDelete = null;
        do{
            Question q = itr.next();
            if(q.getId() == id){
                toDelete = q;
                break;
            }
        }while(itr.hasNext());
        if(toDelete != null)
            questions.remove(toDelete);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
