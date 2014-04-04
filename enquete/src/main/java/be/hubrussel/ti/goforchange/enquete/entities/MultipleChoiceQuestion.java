package be.hubrussel.ti.goforchange.enquete.entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Samuel on 31/03/2014.
 */
public class MultipleChoiceQuestion extends Question {

    private final ArrayList<Choice> choices;
    private int maxChoices;
    private int minChoices;

    /**
     * By default minChoices and maxChoices will be 0.
     * @param section
     * @param description
     */
    public MultipleChoiceQuestion(Section section, String description) {
        super(section, description);
        choices = new ArrayList<Choice>();
        setMaxChoices(0);
        setMinChoices(0);
    }

    /**
     * @post If maxChoices or minChoices was 0 before, then make it 1.
     * @param choice
     */
    public void addChoice(Choice choice){
        choices.add(choice);
        choice.setQuestion(this);

        if(getMaxChoices() == 0)
            setMaxChoices(1);

        if(getMinChoices() == 0)
            setMinChoices(1);
    }

    /**
     * @post If the number of maximum choices is higher than the new number of available choices, make sure the number of max choices is set to the same number as the available choices.
     * @param choice
     */
    public void removeChoice(Choice choice){
        choices.remove(choice);
        choice.setQuestion(null);

        if(getMaxChoices() > choices.size())
            setMaxChoices(choices.size());

        if(getMinChoices() > choices.size())
            setMinChoices(choices.size());
    }

    public Iterator<Choice> getChoices(){
        return choices.iterator();
    }

    public void removeChoiceById(int id){
        Iterator<Choice> itr = getChoices();
        Choice toDelete = null;
        do{
            Choice c = itr.next();
            if(c.getId() == id){
                toDelete = c;
                break;
            }
        }while(itr.hasNext());
        if(toDelete != null)
            choices.remove(toDelete);
    }

    public int getMaxChoices() {
        return maxChoices;
    }

    /**
     *
     * @param maxChoices
     * @throws IllegalArgumentException The maximum number of choices must be equal to or lower than the number of available choices.
     */
    public void setMaxChoices(int maxChoices) throws IllegalArgumentException {
        if(maxChoices > choices.size())
            throw new IllegalArgumentException();

        this.maxChoices = maxChoices;
    }

    private boolean isValidChoice(Choice choice){
        if(choice == null)
            return false;
        return choice.getQuestion() == this && choices.contains(choice);
    }

    public boolean isValidChoiceList(List<Choice> choices){
        for(Choice choice: choices){
            if(!isValidChoice(choice))
                return false;
        }

        return !(choices.size() > getMaxChoices() || choices.size() < getMinChoices());
    }

    public int getMinChoices() {
        return minChoices;
    }

    /**
     *
     * @param minChoices
     * @throws IllegalArgumentException The minimum number of choices must be equal to or lower than the number of available choices.
     */
    public void setMinChoices(int minChoices) throws IllegalArgumentException {
        if(minChoices > choices.size())
            throw new IllegalArgumentException();

        this.minChoices = minChoices;
    }
}
