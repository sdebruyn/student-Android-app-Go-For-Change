package be.hubrussel.ti.goforchange.enquete.entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Samuel on 31/03/2014.
 */
public class MultipleChoiceAnswer extends Answer {

    private final ArrayList<Choice> answeredChoices;

    public MultipleChoiceAnswer(Respondent respondent, Question answeredQuestion, List<Choice> answeredChoicesLst) throws IllegalArgumentException, ClassCastException {
        super(respondent, answeredQuestion);

        MultipleChoiceQuestion question = (MultipleChoiceQuestion) getAnsweredQuestion();
        if(!question.isValidChoiceList(answeredChoicesLst.iterator()))
            throw new IllegalArgumentException();

        answeredChoices = new ArrayList<Choice>();
        for (Choice answeredChoice : answeredChoicesLst)
            answeredChoices.add(answeredChoice);
    }

    public Iterator<Choice> getChoices(){
        return answeredChoices.iterator();
    }

    @Override
    public String toString() {
        return answeredChoices.toString();
    }
}
