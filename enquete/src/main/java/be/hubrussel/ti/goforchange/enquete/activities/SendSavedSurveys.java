package be.hubrussel.ti.goforchange.enquete.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import be.hubrussel.ti.goforchange.enquete.R;

public class SendSavedSurveys extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_saved_surveys);

        if (ApplicationData.getDatabaseConnector() == null)
            handleSimpleError(new IllegalStateException());
    }

    private void handleSimpleError(Exception e) {
        Log.e(getClass().getName(), "Exception occured", e);
        Toast.makeText(this, "Door een tijdelijk probleem kan deze actie momenteel niet voltooid worden. Probeer het later opnieuw.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    public void sendViaIntent(View view) {
    }

    public void storeOnStorage(View view) {
    }

    public void clearRespondents(View view) {

    }
}
