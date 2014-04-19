package be.hubrussel.ti.goforchange.enquete.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import be.hubrussel.ti.goforchange.enquete.R;

public class SendSavedSurveys extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_saved_surveys);

        if (ApplicationData.getInstance().getDatabaseConnector() == null)
            handleSimpleError(new IllegalStateException());
    }

    private void handleSimpleError(Exception e) {
        Log.e(getClass().getName(), "Exception occured", e);
        Toast.makeText(this, "Door een tijdelijk probleem kan deze actie momenteel niet voltooid worden. Probeer het later opnieuw.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private Uri createExportFile() throws IOException {
        Calendar nCalendar = Calendar.getInstance();
        String now = String.valueOf(nCalendar.get(Calendar.YEAR))
                + String.valueOf(nCalendar.get(Calendar.MONTH) + 1)
                + String.valueOf(nCalendar.get(Calendar.DAY_OF_MONTH))
                + String.valueOf(nCalendar.get(Calendar.HOUR_OF_DAY))
                + String.valueOf(nCalendar.get(Calendar.MINUTE))
                + String.valueOf(nCalendar.get(Calendar.SECOND))
                + String.valueOf(nCalendar.get(Calendar.MILLISECOND));
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + getString(R.string.app_name)
                + "/";
        File destinationDir = new File(path);
        destinationDir.mkdirs();
        File destinationFile = new File(destinationDir, now + ".csv");
        FileWriter fileWriter = new FileWriter(destinationFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(ApplicationData.getInstance().getDatabaseConnector().csvExport());
        bufferedWriter.close();
        return Uri.fromFile(destinationFile);
    }

    public void sendViaIntent(View view) {
        try {
            Uri uri = createExportFile();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setType("text/html");
            startActivity(intent);
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, R.string.error_file_export, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void storeOnStorage(View view) {
        try {
            createExportFile();
            Toast toast = Toast.makeText(this, R.string.success_file_export, Toast.LENGTH_LONG);
            toast.show();
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, R.string.error_file_export, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void clearRespondents(View view) {
        ApplicationData.getInstance().getDatabaseConnector().clearRespondents();
        Toast toast = Toast.makeText(this, R.string.respondents_cleared, Toast.LENGTH_LONG);
        toast.show();
    }
}
