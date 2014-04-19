package be.hubrussel.ti.goforchange.enquete.activities;

import be.hubrussel.ti.goforchange.enquete.controllers.DatabaseConnector;
import be.hubrussel.ti.goforchange.enquete.entities.Respondent;

/**
 * Created by Samuel on 2/04/2014.
 */
public class ApplicationData {

    private static ApplicationData ourInstance = new ApplicationData();
    private DatabaseConnector databaseConnector;
    private Respondent respondent;

    private ApplicationData() {
    }

    public static synchronized ApplicationData getInstance() {
        return ourInstance;
    }

    public DatabaseConnector getDatabaseConnector() {
        return databaseConnector;
    }

    public void setDatabaseConnector(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    public Respondent getRespondent() {
        return respondent;
    }

    public void setRespondent(Respondent respondent) {
        this.respondent = respondent;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
