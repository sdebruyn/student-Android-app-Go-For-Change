package be.hubrussel.ti.goforchange.enquete.controllers;

/**
 * Created by Samuel on 31/03/2014.
 */
public class DatabaseConnector {

    private static DatabaseConnector ourInstance = new DatabaseConnector();

    public static synchronized DatabaseConnector getInstance() {
        return ourInstance;
    }

    private DatabaseConnector() {
    }


    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
