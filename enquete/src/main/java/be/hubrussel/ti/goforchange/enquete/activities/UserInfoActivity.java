package be.hubrussel.ti.goforchange.enquete.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLException;

import be.hubrussel.ti.goforchange.enquete.R;
import be.hubrussel.ti.goforchange.enquete.entities.Respondent;

public class UserInfoActivity extends Activity {

    public final static String SURVEY_COMPLETED = "be.hubrussel.ti.goforchange.COMPLETED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_quit_survey:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void handleSimpleError(Exception e) {
        Log.e(getClass().getName(), "Exception occured", e);
        Toast.makeText(this, "Door een tijdelijk probleem kan deze actie momenteel niet voltooid worden. Probeer het later opnieuw.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void finishSurvey(View view) {
        EditText editRespondentName = (EditText) findViewById(R.id.editRespondentName);
        EditText editCompanyName = (EditText) findViewById(R.id.editCompanyName);
        EditText editCompanyPostal = (EditText) findViewById(R.id.editCompanyPostal);
        EditText editRespondentMail = (EditText) findViewById(R.id.editRespondentMail);

        Respondent respondent = ApplicationData.getRespondent();
        respondent.setCompanyName(editCompanyName.getText().toString());
        respondent.setCompanyPerson(editRespondentName.getText().toString());
        respondent.setCompanyEmail(editRespondentMail.getText().toString());
        try {
            respondent.setCompanyPostal(Integer.parseInt(editCompanyPostal.getText().toString()));
        } catch (Exception ignored) {
        }

        try {
            ApplicationData.getDatabaseConnector().finishRespondent(respondent);
        } catch (SQLException e) {
            handleSimpleError(e);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(SURVEY_COMPLETED, true);
        startActivity(intent);
        finish();
    }
}
