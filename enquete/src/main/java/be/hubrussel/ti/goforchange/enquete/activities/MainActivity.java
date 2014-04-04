package be.hubrussel.ti.goforchange.enquete.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;

import be.hubrussel.ti.goforchange.enquete.R;
import be.hubrussel.ti.goforchange.enquete.controllers.DatabaseConnector;
import be.hubrussel.ti.goforchange.enquete.entities.Respondent;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ApplicationData.getDatabaseConnector() == null)
            ApplicationData.setDatabaseConnector(new DatabaseConnector(getApplicationContext()));
        try {
            ApplicationData.getDatabaseConnector().initDatabase();
        } catch (IOException e) {
            handleSimpleError(e);
        }

        ApplicationData.setRespondent(ApplicationData.getDatabaseConnector().restoreSurveyRespondent());
        if (ApplicationData.getRespondent() != null)
            findViewById(R.id.restoreSurveyLayout).setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_submit_answers:
                Intent intent = new Intent(this, SendSavedSurveys.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleSimpleError(Exception e) {
        Log.e(getClass().getName(), "Exception occured", e);
        Toast.makeText(this, "Door een tijdelijk probleem kan deze actie momenteel niet voltooid worden. Probeer het later opnieuw.", Toast.LENGTH_LONG).show();
    }

    public void beginSurvey(View view) {
        Respondent respondent = new Respondent();
        try {
            ApplicationData.getDatabaseConnector().newRespondent(respondent);
        } catch (SQLException e) {
            handleSimpleError(e);
        }
        ApplicationData.setRespondent(respondent);

        Intent intent = new Intent(this, QuestionActivity.class);
        startActivity(intent);
        finish();
    }

    public void resumeSurvey(View view) {
        Respondent respondent = ApplicationData.getDatabaseConnector().restoreSurveyRespondent();
        ApplicationData.setRespondent(respondent);

        Intent intent = new Intent(this, QuestionActivity.class);
        startActivity(intent);
        finish();
    }

}
