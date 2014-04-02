package be.hubrussel.ti.goforchange.enquete.activities;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import be.hubrussel.ti.goforchange.enquete.R;
import be.hubrussel.ti.goforchange.enquete.controllers.DatabaseConnector;


public class MainActivity extends Activity {

    DatabaseConnector connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new IntroductionFragment())
                    .commit();
        }

        connect = new DatabaseConnector(getApplicationContext());
        try {
            connect.initDatabase();
        } catch (IOException ignored){}

        if(connect.restoreSurveyRespondent() != null)
            findViewById(R.id.restoreSurveyLayout).setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_submit_answers) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void beginSurvey(View view) {

    }

    public static class IntroductionFragment extends Fragment {

        public IntroductionFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_introduction, container, false);
        }

    }
}
