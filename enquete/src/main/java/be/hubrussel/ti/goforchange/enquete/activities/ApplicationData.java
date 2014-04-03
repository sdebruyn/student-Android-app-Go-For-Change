package be.hubrussel.ti.goforchange.enquete.activities;

import be.hubrussel.ti.goforchange.enquete.controllers.DatabaseConnector;
import be.hubrussel.ti.goforchange.enquete.entities.Respondent;

/**
 * Created by Samuel on 2/04/2014.
 */
public class ApplicationData {

    private static ApplicationData ourInstance = new ApplicationData();
    private static DatabaseConnector databaseConnector;
    private static Respondent respondent;

    private ApplicationData() {
    }

    public static synchronized ApplicationData getInstance() {
        return ourInstance;
    }

    public static DatabaseConnector getDatabaseConnector() {
        return databaseConnector;
    }

    public static void setDatabaseConnector(DatabaseConnector databaseConnector) {
        ApplicationData.databaseConnector = databaseConnector;
    }

    public static Respondent getRespondent() {
        return respondent;
    }

    public static void setRespondent(Respondent respondent) {
        ApplicationData.respondent = respondent;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
