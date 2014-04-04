package be.hubrussel.ti.goforchange.enquete.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import be.hubrussel.ti.goforchange.enquete.R;
import be.hubrussel.ti.goforchange.enquete.entities.Answer;
import be.hubrussel.ti.goforchange.enquete.entities.Choice;
import be.hubrussel.ti.goforchange.enquete.entities.MultipleChoiceAnswer;
import be.hubrussel.ti.goforchange.enquete.entities.MultipleChoiceQuestion;
import be.hubrussel.ti.goforchange.enquete.entities.OpenNumericAnswer;
import be.hubrussel.ti.goforchange.enquete.entities.OpenNumericQuestion;
import be.hubrussel.ti.goforchange.enquete.entities.OpenTextAnswer;
import be.hubrussel.ti.goforchange.enquete.entities.OpenTextQuestion;
import be.hubrussel.ti.goforchange.enquete.entities.Question;
import be.hubrussel.ti.goforchange.enquete.entities.RangeAnswer;
import be.hubrussel.ti.goforchange.enquete.entities.RangeQuestion;


public class QuestionActivity extends Activity {

    private EditText openTextEdit;
    private NumberPicker openNumericPicker;
    private SeekBar rangeSeekBar;
    private RadioGroup choiceRadioGroup;
    private HashMap<Integer, Choice> itemIdentifiers;
    private Question currentQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        if (ApplicationData.getDatabaseConnector() == null)
            handleSimpleError(new IllegalStateException());

        if(ApplicationData.getRespondent().getId() == 0)
            handleSimpleError(new IllegalStateException());

        try{
            loadQuestion(ApplicationData.getDatabaseConnector().getNextQuestionForRespondent(ApplicationData.getRespondent()));
        }catch(SQLiteDatabaseCorruptException e){
            handleSimpleError(e);
        }
    }

    private void resetInsertedQuestion() {
        itemIdentifiers = new HashMap<Integer, Choice>();
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

    private void handleSimpleError(Exception e) {
        Log.e(getClass().getName(), "Exception occured", e);
        Toast.makeText(this, "Door een tijdelijk probleem kan deze actie momenteel niet voltooid worden. Probeer het later opnieuw.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void nextQuestion(View view) {

        Answer answer = null;
        String errorMessage = getString(R.string.invalid_answer);

        if(getCurrentQuestion().getClass() == OpenTextQuestion.class){

            String answeredText = getOpenTextEdit().getText().toString();
            if(answeredText.isEmpty()){
                errorMessage = getString(R.string.invalid_text);
            }else{
                answer = new OpenTextAnswer(ApplicationData.getRespondent(), getCurrentQuestion(), answeredText);
            }

        }else if(getCurrentQuestion().getClass() == OpenNumericQuestion.class){

            OpenNumericQuestion numericQuestion = (OpenNumericQuestion)getCurrentQuestion();
            int answered = getOpenNumericPicker().getValue();
            if(!numericQuestion.isValidNumber(answered)){
                errorMessage = getString(R.string.invalid_number, answered);
            }else{
                answer = new OpenNumericAnswer(ApplicationData.getRespondent(), getCurrentQuestion(), answered);
            }

        }else if(getCurrentQuestion().getClass() == RangeQuestion.class){

            RangeQuestion rangeQuestion = (RangeQuestion)getCurrentQuestion();
            int answered = rangeQuestion.getRealValueFromSeekBar(getRangeSeekBar().getProgress());
            if(!rangeQuestion.isValidNumber(answered)){
                errorMessage = getString(R.string.invalid_number, answered);
            }else{
                answer = new RangeAnswer(ApplicationData.getRespondent(), getCurrentQuestion(), answered);
            }

        }else if(getCurrentQuestion().getClass() == MultipleChoiceQuestion.class){

            MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion)getCurrentQuestion();
            ArrayList<Choice> answered = new ArrayList<Choice>();

            if(multipleChoiceQuestion.getMaxChoices() == 1){

                int checkButton = getChoiceRadioGroup().getCheckedRadioButtonId();
                if(checkButton != -1){
                    Choice choice = itemIdentifiers.get(checkButton);
                    answered.add(choice);
                }

            }else{

                for(int id: itemIdentifiers.keySet()){
                    CheckBox checkBox = (CheckBox)findViewById(id);
                    if(checkBox.isChecked()){
                        Choice choice = itemIdentifiers.get(id);
                        answered.add(choice);
                    }
                }

            }

            if(!multipleChoiceQuestion.isValidChoiceList(answered)){
                errorMessage = getString(R.string.invalid_choices, multipleChoiceQuestion.getMinChoices(), answered.size());
            }else{
                answer = new MultipleChoiceAnswer(ApplicationData.getRespondent(), getCurrentQuestion(), answered);
            }

        }

        if(answer == null){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.invalid_answer).setMessage(errorMessage);
            AlertDialog dialog = builder.create();
            dialog.show();

        }else{

            Question next = null;
            try {
                next = ApplicationData.getDatabaseConnector().continueSurvey(answer);
            } catch (SQLException ignored) {}

            if(next == null){
                Intent intent = new Intent(this, UserInfoActivity.class);
                startActivity(intent);
                finish();
            }else{
                loadQuestion(next);
            }

        }

    }

    private void loadQuestion(Question question){
        resetInsertedQuestion();
        setCurrentQuestion(question);

        TextView sectionDescView = (TextView)findViewById(R.id.sectionDescription);
        sectionDescView.setText(question.getSection().getName());
        TextView questionDescView = (TextView)findViewById(R.id.questionDescription);
        questionDescView.setText(question.getDescription());
        LinearLayout questionLayout = (LinearLayout)findViewById(R.id.questionLayout);
        questionLayout.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        if(question.getClass() == OpenTextQuestion.class){
            EditText editText = new EditText(this);
            editText.setId(View.generateViewId());
            editText.setLayoutParams(params);
            setOpenTextEdit(editText);
            questionLayout.addView(editText);

        }else if(question.getClass() == OpenNumericQuestion.class) {
            NumberPicker numberPicker = new NumberPicker(this);
            numberPicker.setId(View.generateViewId());
            numberPicker.setLayoutParams(params);

            if(((OpenNumericQuestion)question).isYear()){
                numberPicker.setMinValue(OpenNumericQuestion.MIN_YEAR);
                numberPicker.setMaxValue(OpenNumericQuestion.MAX_YEAR);
                numberPicker.setValue(Calendar.getInstance().get(Calendar.YEAR));
            }else{
                numberPicker.setMinValue(0);
                numberPicker.setMaxValue(1000000);
                numberPicker.setValue(0);
            }

            setOpenNumericPicker(numberPicker);
            questionLayout.addView(numberPicker);

        }else if(question.getClass() == RangeQuestion.class){
            final RangeQuestion rangeQuestion = (RangeQuestion)question;

            LinearLayout rangePicker = new LinearLayout(this);
            rangePicker.setLayoutParams(params);
            rangePicker.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            final TextView currentRangeView = new TextView(this);
            currentRangeView.setLayoutParams(tvParams);

            TextView rangeMinView = new TextView(this);
            rangeMinView.setLayoutParams(tvParams);
            rangeMinView.setText(String.valueOf(rangeQuestion.getMin()));
            rangePicker.addView(rangeMinView);

            SeekBar seekBar = new SeekBar(this);
            seekBar.setMax(rangeQuestion.getSeekBarMax());

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onProgressChanged(SeekBar seekBar1, int progress, boolean fromUser){
                    int realValue = rangeQuestion.getRealValueFromSeekBar(progress);
                    currentRangeView.setText(String.valueOf(realValue));
                }

            });

            seekBar.setLayoutParams(params);
            seekBar.setProgress(0);
            setRangeSeekBar(seekBar);
            rangePicker.addView(seekBar);

            TextView rangeMaxView = new TextView(this);
            rangeMaxView.setLayoutParams(tvParams);
            rangeMaxView.setText(String.valueOf(rangeQuestion.getMax()));
            rangePicker.addView(rangeMaxView);

            questionLayout.addView(rangePicker);
            questionLayout.addView(currentRangeView);

        }else if(question.getClass() == MultipleChoiceQuestion.class){
            MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion)question;

            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            if(multipleChoiceQuestion.getMaxChoices() == 1){

                RadioGroup radioGroup = new RadioGroup(this);
                setChoiceRadioGroup(radioGroup);
                radioGroup.setLayoutParams(params);

                Iterator<Choice> itr = multipleChoiceQuestion.getChoices();
                while(itr.hasNext()){
                    Choice choice = itr.next();
                    RadioButton radioButton = new RadioButton(this);
                    radioButton.setLayoutParams(itemParams);
                    radioButton.setText(choice.getText());
                    int id = View.generateViewId();
                    itemIdentifiers.put(id, choice);
                    radioButton.setId(id);
                    radioGroup.addView(radioButton);
                }

                questionLayout.addView(radioGroup);

            }else{

                Iterator<Choice> itr = multipleChoiceQuestion.getChoices();
                while(itr.hasNext()){
                    Choice choice = itr.next();
                    CheckBox checkBox = new CheckBox(this);
                    checkBox.setLayoutParams(itemParams);
                    checkBox.setText(choice.getText());
                    int id = View.generateViewId();
                    itemIdentifiers.put(id, choice);
                    checkBox.setId(id);
                    questionLayout.addView(checkBox);
                }

            }
        }
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public EditText getOpenTextEdit() {
        return openTextEdit;
    }

    public void setOpenTextEdit(EditText openTextEdit) {
        this.openTextEdit = openTextEdit;
    }

    public NumberPicker getOpenNumericPicker() {
        return openNumericPicker;
    }

    public void setOpenNumericPicker(NumberPicker openNumericPicker) {
        this.openNumericPicker = openNumericPicker;
    }

    public SeekBar getRangeSeekBar() {
        return rangeSeekBar;
    }

    public void setRangeSeekBar(SeekBar rangeSeekBar) {
        this.rangeSeekBar = rangeSeekBar;
    }

    public RadioGroup getChoiceRadioGroup() {
        return choiceRadioGroup;
    }

    public void setChoiceRadioGroup(RadioGroup choiceRadioGroup) {
        this.choiceRadioGroup = choiceRadioGroup;
    }
}
