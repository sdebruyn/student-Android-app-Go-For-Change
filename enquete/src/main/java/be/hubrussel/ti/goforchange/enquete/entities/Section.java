package be.hubrussel.ti.goforchange.enquete.entities;

import android.provider.BaseColumns;

/**
 * Created by Samuel on 31/03/2014.
 */
public class Section implements BaseColumns {

    private int id;
    private String name;

    public Section(String name){
        this.setName(name);
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

    @Override
    public String toString() {
        return getName();
    }
}
