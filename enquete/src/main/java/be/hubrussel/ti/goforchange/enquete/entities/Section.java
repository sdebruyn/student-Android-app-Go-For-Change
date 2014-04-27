package be.hubrussel.ti.goforchange.enquete.entities;

import android.provider.BaseColumns;

/**
 * Created by Samuel on 31/03/2014.
 */
public class Section implements BaseColumns {

    private String name;

    public Section(String name) {
        this.setName(name);
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
