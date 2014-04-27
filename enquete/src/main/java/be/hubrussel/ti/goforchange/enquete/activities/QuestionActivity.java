package be.hubrussel.ti.goforchange.enquete.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

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

        if (ApplicationData.getInstance().getDatabaseConnector() == null)
            handleSimpleError(new IllegalStateException());

        if (ApplicationData.getInstance().getRespondent().getId() == 0)
            handleSimpleError(new IllegalStateException());

        try {
            loadQuestion(ApplicationData.getInstance().getDatabaseConnector().getNextQuestionForRespondent(ApplicationData.getInstance().getRespondent()));
        } catch (SQLiteDatabaseCorruptException e) {
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

        if (getCurrentQuestion().getClass() == OpenTextQuestion.class) {

            String answeredText = "";
            try {
                answeredText = getOpenTextEdit().getText().toString();
            } catch (NullPointerException ignored) {
            }

            if (answeredText.isEmpty()) {
                errorMessage = getString(R.string.invalid_text);
            } else {
                answer = new OpenTextAnswer(ApplicationData.getInstance().getRespondent(), getCurrentQuestion(), answeredText);
            }

        } else if (getCurrentQuestion().getClass() == OpenNumericQuestion.class) {

            OpenNumericQuestion numericQuestion = (OpenNumericQuestion) getCurrentQuestion();
            int answered = getOpenNumericPicker().getValue();

            if (!numericQuestion.isValidNumber(answered) && numericQuestion.isYear()) {
                errorMessage = getString(R.string.invalid_year, answered);
            } else if (!numericQuestion.isValidNumber(answered) && !numericQuestion.isYear()) {
                errorMessage = getString(R.string.invalid_number, answered);
            } else {
                answer = new OpenNumericAnswer(ApplicationData.getInstance().getRespondent(), getCurrentQuestion(), answered);
            }

        } else if (getCurrentQuestion().getClass() == RangeQuestion.class) {

            RangeQuestion rangeQuestion = (RangeQuestion) getCurrentQuestion();
            int answered = rangeQuestion.getRealValueFromSeekBar(getRangeSeekBar().getProgress());
            if (!rangeQuestion.isValidNumber(answered)) {
                errorMessage = getString(R.string.invalid_number, answered);
            } else {
                answer = new RangeAnswer(ApplicationData.getInstance().getRespondent(), getCurrentQuestion(), answered);
            }

        } else if (getCurrentQuestion().getClass() == MultipleChoiceQuestion.class) {

            MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) getCurrentQuestion();
            ArrayList<Choice> answered = new ArrayList<Choice>();

            if (multipleChoiceQuestion.getMaxChoices() == 1) {

                int checkButton = getChoiceRadioGroup().getCheckedRadioButtonId();
                if (checkButton != -1) {
                    Choice choice = itemIdentifiers.get(checkButton);
                    answered.add(choice);
                }

            } else {

                for (int id : itemIdentifiers.keySet()) {
                    CheckBox checkBox = (CheckBox) findViewById(id);
                    if (checkBox.isChecked()) {
                        Choice choice = itemIdentifiers.get(id);
                        answered.add(choice);
                    }
                }

            }

            if (!multipleChoiceQuestion.isValidChoiceList(answered)) {
                errorMessage = getString(R.string.invalid_choices, multipleChoiceQuestion.getMinChoices(), answered.size());
            } else {
                answer = new MultipleChoiceAnswer(ApplicationData.getInstance().getRespondent(), getCurrentQuestion(), answered);
            }

        }

        if (answer == null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.invalid_answer).setMessage(errorMessage);
            AlertDialog dialog = builder.create();
            dialog.show();

        } else {

            Question next = null;

            if (!getCurrentQuestion().isShouldEnd()) {
                try {
                    next = ApplicationData.getInstance().getDatabaseConnector().continueSurvey(answer);
                } catch (SQLException ignored) {
                }
            }

            if (next == null) {
                Intent intent = new Intent(this, UserInfoActivity.class);
                startActivity(intent);
                finish();
            } else {
                loadQuestion(next);
            }

        }

    }

    private void loadQuestion(Question question) {
        resetInsertedQuestion();
        setCurrentQuestion(question);

        TextView sectionDescView = (TextView) findViewById(R.id.sectionDescription);
        sectionDescView.setText(question.getSection().getName());
        TextView questionDescView = (TextView) findViewById(R.id.questionDescription);
        questionDescView.setText(question.getDescription());
        LinearLayout questionLayout = (LinearLayout) findViewById(R.id.questionLayout);
        questionLayout.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        if (question.getClass() == OpenTextQuestion.class) {
            EditText editText = new EditText(this);
            editText.setId(View.generateViewId());
            editText.setLayoutParams(params);
            setOpenTextEdit(editText);
            questionLayout.addView(editText);

        } else if (question.getClass() == OpenNumericQuestion.class) {
            final OpenNumericQuestion nQuestion = (OpenNumericQuestion) question;

            final NumberPicker numberPicker = new NumberPicker(this);
            numberPicker.setId(View.generateViewId());
            numberPicker.setLayoutParams(params);

            if (((OpenNumericQuestion) question).isYear()) {
                numberPicker.setMinValue(OpenNumericQuestion.MIN_YEAR);
                numberPicker.setMaxValue(OpenNumericQuestion.MAX_YEAR);
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                numberPicker.setValue(currentYear);
            } else {
                numberPicker.setMinValue(nQuestion.getMin());
                numberPicker.setMaxValue(nQuestion.getMax());
                numberPicker.setValue(nQuestion.getMin());
            }

            setOpenNumericPicker(numberPicker);
            questionLayout.addView(numberPicker);

            final Context aContext = this;
            Button jumpToNumberOpenener = new Button(this);
            jumpToNumberOpenener.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            jumpToNumberOpenener.setText(R.string.jump_to_number);
            jumpToNumberOpenener.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_fast_forward, 0);
            jumpToNumberOpenener.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dBuilder = new AlertDialog.Builder(aContext);
                    if (nQuestion.isYear())
                        dBuilder.setTitle(getString(R.string.enter_ranged_number, OpenNumericQuestion.MIN_YEAR, OpenNumericQuestion.MAX_YEAR));
                    else
                        dBuilder.setTitle(getString(R.string.enter_ranged_number, nQuestion.getMin(), nQuestion.getMax()));
                    LinearLayout dialogContents = new LinearLayout(aContext);
                    dialogContents.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    final EditText rangedNumber = new EditText(aContext);
                    rangedNumber.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    rangedNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
                    if (nQuestion.isYear())
                        rangedNumber.setText(String.valueOf(OpenNumericQuestion.MIN_YEAR));
                    else
                        rangedNumber.setText(String.valueOf(0));
                    dialogContents.addView(rangedNumber);
                    dBuilder.setView(dialogContents);
                    dBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int entered = 0;
                            if (rangedNumber.getText().length() > 0)
                                entered = Integer.valueOf(rangedNumber.getText().toString());
                            if (entered < 0)
                                entered = 0;
                            if (entered > 100000)
                                entered = 100000;
                            if (nQuestion.isYear() && entered < OpenNumericQuestion.MIN_YEAR)
                                entered = OpenNumericQuestion.MIN_YEAR;
                            if (nQuestion.isYear() && entered > OpenNumericQuestion.MAX_YEAR)
                                entered = OpenNumericQuestion.MAX_YEAR;
                            numberPicker.setValue(entered);
                        }
                    });
                    dBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // NOP
                        }
                    });
                    AlertDialog dialog = dBuilder.create();
                    dialog.show();
                }
            });

            questionLayout.addView(jumpToNumberOpenener);

        } else if (question.getClass() == RangeQuestion.class) {
            final RangeQuestion rangeQuestion = (RangeQuestion) question;

            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            final TextView currentRangeView = new TextView(this);
            currentRangeView.setText(String.valueOf(rangeQuestion.getMin()));
            currentRangeView.setLayoutParams(tvParams);
            currentRangeView.setTextSize(getResources().getDimension(R.dimen.choice_text_size));

            TextView rangeMinView = new TextView(this);
            rangeMinView.setLayoutParams(tvParams);
            rangeMinView.setText(String.valueOf(rangeQuestion.getMin()));
            TextView rangeMaxView = new TextView(this);
            rangeMaxView.setLayoutParams(tvParams);
            rangeMaxView.setText(String.valueOf(rangeQuestion.getMax()));

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
                public void onProgressChanged(SeekBar seekBar1, int progress, boolean fromUser) {
                    int realValue = rangeQuestion.getRealValueFromSeekBar(progress);
                    currentRangeView.setText(String.valueOf(realValue));
                }

            });

            seekBar.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            seekBar.setProgress(rangeQuestion.getSeekBarStart());
            setRangeSeekBar(seekBar);

            LinearLayout rangePicker = new LinearLayout(this);
            rangePicker.setLayoutParams(params);
            rangePicker.setOrientation(LinearLayout.HORIZONTAL);

            rangePicker.addView(rangeMinView);
            rangePicker.addView(seekBar);
            rangePicker.addView(rangeMaxView);

            LinearLayout container = new LinearLayout(this);
            container.setLayoutParams(params);
            container.setGravity(Gravity.CENTER);
            container.setOrientation(LinearLayout.HORIZONTAL);
            container.addView(currentRangeView);

            questionLayout.addView(rangePicker);
            questionLayout.addView(container);

        } else if (question.getClass() == MultipleChoiceQuestion.class) {
            MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) question;

            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            if (multipleChoiceQuestion.getMaxChoices() == 1) {

                RadioGroup radioGroup = new RadioGroup(this);
                setChoiceRadioGroup(radioGroup);
                radioGroup.setLayoutParams(params);

                Iterator<Choice> itr = multipleChoiceQuestion.getChoices();
                while (itr.hasNext()) {
                    Choice choice = itr.next();
                    RadioButton radioButton = new RadioButton(this);
                    radioButton.setLayoutParams(itemParams);
                    radioButton.setText(choice.getText());
                    radioButton.setTextSize(getResources().getDimension(R.dimen.radio_text_size));
                    int id = View.generateViewId();
                    itemIdentifiers.put(id, choice);
                    radioButton.setId(id);
                    radioGroup.addView(radioButton);
                }

                questionLayout.addView(radioGroup);

            } else {

                Iterator<Choice> itr = multipleChoiceQuestion.getChoices();
                while (itr.hasNext()) {
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

        ScrollView sv = (ScrollView) findViewById(R.id.questionScrollView);
        sv.smoothScrollTo(0, 0);
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
