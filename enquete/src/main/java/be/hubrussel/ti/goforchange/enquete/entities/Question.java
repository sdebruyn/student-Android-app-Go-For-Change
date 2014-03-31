package be.hubrussel.ti.goforchange.enquete.entities;

/**
 * Created by Samuel on 31/03/2014.
 */
public abstract class Question {

    private int id;
    private int order;
    private Section section;
    private String description;

    public Question(Section section, String description){
        setSection(section);
        setDescription(description);
        order = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
