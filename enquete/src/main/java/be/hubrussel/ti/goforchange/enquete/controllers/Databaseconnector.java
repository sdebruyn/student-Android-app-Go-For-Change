package be.hubrussel.ti.goforchange.enquete.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Iterator;

import be.hubrussel.ti.goforchange.enquete.entities.*;

/**
 * Created by Samuel on 31/03/2014.
 *
 * Always call initDatabase before doing anything else!
 */
public class DatabaseConnector extends SQLiteOpenHelper {

    private final static String DB_NAME = "enquete.db";
    /**
     * This is the database version number. Change this number if the structure changed and the old database could be incompatible with the new version.
     * Attention! This will erase all data in the database.
     * The same could happen when the user downgrades instead of upgrading.
     */
    private static final int DATABASE_VERSION = 1;
    private final static String VIEW_QUESTIONS = "single_questions";
    private final static String TABLE_ANSWERS = "answers";
    private final static String TABLE_CHOICES = "choices";
    private final static String TABLE_RESPONDENTS = "respondents";
    private final static String VIEW_RESPONDENTS = "restorable_respondents";
    private final static String TABLE_QUESTION_TYPES = "question_types";
    private final String DB_PATH;
    private final Context context;


    public DatabaseConnector(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);

        this.context = context;
        //noinspection ConstantConditions
        DB_PATH = getContext().getApplicationInfo().dataDir + "/databases/";
    }

    public void initDatabase() throws IOException {
        if (needToCopyFirst())
            copyFromAssets();
    }

    private String getPath() {
        return DB_PATH + DB_NAME;
    }

    /**
     * Copy the database we ship with this application from the assets folder to the data folder
     *
     * @throws IOException Some write or read operation failed. Possibly file not found or no access rights.
     */
    private void copyFromAssets() throws IOException {
        try {

            // First create an empty database so we can overwrite it.
            getWritableDatabase();

            InputStream inputStream = getContext().getAssets().open(DB_NAME);
            OutputStream outputStream = new FileOutputStream(getPath());

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0)
                outputStream.write(buffer, 0, length);

            outputStream.flush();
            outputStream.close();
            inputStream.close();

        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private boolean needToCopyFirst() {
        try {

            File checkFile = new File(getPath());
            if (!checkFile.exists())
                return true;

            SQLiteDatabase checkDB = SQLiteDatabase.openDatabase(getPath(), null, SQLiteDatabase.OPEN_READONLY);
            //noinspection ConstantConditions
            if (checkDB == null)
                return true;

            if (!checkDB.isDatabaseIntegrityOk())
                return true;

            checkDB.close();

        } catch (Exception e) {
            return true;
        }
        return false;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // NOP
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        try {
            copyFromAssets();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, newVersion, oldVersion);
    }

    public Context getContext() {
        return context;
    }

    private Question retrieveNextQuestion(Question input) throws SQLiteDatabaseCorruptException {
        if(input.getNext() != null)
            return input.getNext();

        return retrieveQuestionByOrderId(input.getOrder() + 1);
    }

    private Question retrieveQuestionByOrderId(int orderInput) throws SQLiteDatabaseCorruptException {
        if (orderInput < 0)
            return null;

        SQLiteDatabase db = getReadableDatabase();
        assert db != null;

        Cursor cursor = db.query(VIEW_QUESTIONS, null, "[order] = ?", new String[]{String.valueOf(orderInput)}, null, null, null);
        return processCursor(cursor, db);
    }

    private Question retrieveQuestionById(int idInput) throws SQLiteDatabaseCorruptException {
        if (idInput < 0)
            return null;

        SQLiteDatabase db = getReadableDatabase();
        assert db != null;

        Cursor cursor = db.query(VIEW_QUESTIONS, null, "[_id] = ?", new String[]{String.valueOf(idInput)}, null, null, null);
        return processCursor(cursor, db);
    }

    private Question processCursor(Cursor cursor, SQLiteDatabase db) throws SQLiteDatabaseCorruptException {

        Question result = null;

        assert db != null;

        if (cursor.getCount() != 1)
            throw new SQLiteDatabaseCorruptException();

        cursor.moveToFirst();

        try {
            Section section = new Section(cursor.getString(cursor.getColumnIndexOrThrow("section")));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            int qid = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));

            String meta = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("meta_id")));
            QuestionType type = QuestionType.fromString(cursor.getString(cursor.getColumnIndexOrThrow("type")));
            switch (type) {


                case RANGE:
                    result = new RangeQuestion(section, description);
                    RangeQuestion rQuestion = (RangeQuestion) result;

                    Cursor rMeta = db.query(TABLE_QUESTION_TYPES, new String[]{"min", "max", "step"}, "[_id] = ?", new String[]{meta}, null, null, null);

                    if (rMeta.getCount() != 1)
                        throw new SQLiteDatabaseCorruptException();

                    rMeta.moveToFirst();

                    int minColInd = rMeta.getColumnIndexOrThrow("min");
                    int maxColInd = rMeta.getColumnIndexOrThrow("max");
                    int stepColInd = rMeta.getColumnIndexOrThrow("step");

                    if(rMeta.getType(minColInd) == Cursor.FIELD_TYPE_NULL || rMeta.getType(maxColInd) == Cursor.FIELD_TYPE_NULL || rMeta.getType(stepColInd) == Cursor.FIELD_TYPE_NULL)
                        throw new SQLiteDatabaseCorruptException();

                    rQuestion.setMin(rMeta.getInt(minColInd));
                    rQuestion.setMax(rMeta.getInt(maxColInd));
                    rQuestion.setStep(rMeta.getInt(stepColInd));

                    rMeta.close();
                    break;


                case NUMERIC:
                    result = new OpenNumericQuestion(section, description);
                    break;


                case YEAR:
                    result = new OpenNumericQuestion(section, description);
                    ((OpenNumericQuestion) result).setYear(true);
                    break;


                case TEXT:
                    result = new OpenTextQuestion(section, description);
                    break;


                case MULTIPLE_CHOICE:
                    result = new MultipleChoiceQuestion(section, description);
                    MultipleChoiceQuestion mQuestion = (MultipleChoiceQuestion) result;

                    Cursor choicesCursor = db.query(TABLE_CHOICES, null, "[question_id] = ?", new String[]{String.valueOf(qid)}, null, null, null);
                    if (choicesCursor.getCount() < 1)
                        throw new SQLiteDatabaseCorruptException();

                    choicesCursor.moveToFirst();
                    do {

                        Choice choice = new Choice(choicesCursor.getString(choicesCursor.getColumnIndexOrThrow("text")));
                        choice.setId(choicesCursor.getInt(choicesCursor.getColumnIndexOrThrow("_id")));

                        int nIndex = choicesCursor.getColumnIndexOrThrow("next_question_id");
                        int eIndex = choicesCursor.getColumnIndexOrThrow("should_end");
                        if (choicesCursor.getType(nIndex) != Cursor.FIELD_TYPE_NULL) {
                            // This could resolve into an endless recursive loop. Always make sure the database never has a choice where the question and the next question are the same.
                            Question next = retrieveQuestionById(choicesCursor.getInt(nIndex));
                            choice.setNextQuestion(next);
                        } else if (choicesCursor.getInt(eIndex) == 1) {
                            choice.setShouldEnd(true);
                        }

                        mQuestion.addChoice(choice);

                    } while (choicesCursor.moveToNext());
                    choicesCursor.close();

                    Cursor mMeta = db.query(TABLE_QUESTION_TYPES, new String[]{"min", "max"}, "[_id] = ?", new String[]{meta}, null, null, null);

                    if (mMeta.getCount() != 1)
                        throw new SQLiteDatabaseCorruptException();

                    mMeta.moveToFirst();

                    int minColInd1 = mMeta.getColumnIndexOrThrow("min");
                    int maxColInd1 = mMeta.getColumnIndexOrThrow("max");
                    if (mMeta.getType(minColInd1) == Cursor.FIELD_TYPE_NULL || mMeta.getType(maxColInd1) == Cursor.FIELD_TYPE_NULL)
                        throw new SQLiteDatabaseCorruptException();

                    try {
                        mQuestion.setMinChoices(mMeta.getInt(minColInd1));
                        mQuestion.setMaxChoices(mMeta.getInt(maxColInd1));
                    } catch (IllegalArgumentException e) {
                        throw new SQLiteDatabaseCorruptException(e.getMessage());
                    }

                    mMeta.close();
                    break;
            }

            result.setId(qid);

            int orderColumn = cursor.getColumnIndexOrThrow("order");
            if (cursor.getType(orderColumn) != Cursor.FIELD_TYPE_NULL)
                result.setOrder(cursor.getInt(orderColumn));

            int nextQuestionColumn = cursor.getColumnIndexOrThrow("next");
            if(cursor.getType(nextQuestionColumn) != Cursor.FIELD_TYPE_NULL){
                try{
                    Question next = retrieveQuestionById(cursor.getInt(nextQuestionColumn));
                    result.setNext(next);
                }catch(Exception ignored){}
            }

        } catch (IllegalArgumentException e) {
            cursor.close();
            db.close();
            throw new SQLiteDatabaseCorruptException(e.getMessage());
        }

        cursor.close();
        /*
        Do not close the database in recursive calls.
        db.close();
         */

        return result;
    }

    private void storeAnswer(Answer answer) throws SQLException {

        if (answer.getClass() == MultipleChoiceAnswer.class) {
            storeMultipleChoiceAnswer((MultipleChoiceAnswer) answer);
            return;
        }

        ContentValues values = new ContentValues();
        values.put("respondent_id", answer.getRespondent().getId());
        values.put("question_id", answer.getRespondent().getId());

        if (answer.getClass() == OpenNumericAnswer.class) {
            values.put("numeric", ((OpenNumericAnswer) answer).getAnsweredNumber());
        } else if (answer.getClass() == OpenTextAnswer.class) {
            values.put("open_text", ((OpenTextAnswer) answer).getAnsweredText());
        } else if (answer.getClass() == RangeAnswer.class) {
            values.put("numeric", ((RangeAnswer) answer).getAnsweredNumber());
        }

        SQLiteDatabase db = getWritableDatabase();
        assert db != null;
        long id = db.insertOrThrow(TABLE_ANSWERS, null, values);
        db.close();

        answer.setId((int) id);
    }

    private void storeMultipleChoiceAnswer(MultipleChoiceAnswer answer) throws SQLException {

        Iterator<Choice> itr = answer.getChoices();
        long id = 0;
        SQLiteDatabase db = getWritableDatabase();

        while (itr.hasNext()) {
            Choice current = itr.next();

            ContentValues values = new ContentValues();
            values.put("respondent_id", answer.getRespondent().getId());
            values.put("question_id", answer.getRespondent().getId());
            values.put("choice_id", current.getId());
            assert db != null;
            id = db.insertOrThrow(TABLE_ANSWERS, null, values);
        }

        assert db != null;
        db.close();

        answer.setId((int) id);
    }

    /**
     * @param respondent
     * @param question   The next question this respondent has to answer. Set to null if the survey is ended.
     */
    private void storeNextQuestionId(Respondent respondent, Question question) throws SQLException {
        int toDB = 0;
        if (question != null)
            toDB = question.getId();

        ContentValues values = new ContentValues();
        values.put("next_question_id", toDB);

        SQLiteDatabase db = getWritableDatabase();
        assert db != null;
        int success = db.update(TABLE_RESPONDENTS, values, " _id = ?", new String[]{String.valueOf(respondent.getId())});
        db.close();

        if (success < 0)
            throw new SQLException();
    }

    private void cancelRestoreSurvey() {
        ContentValues values = new ContentValues();
        values.putNull("next_question_id");
        SQLiteDatabase db = getWritableDatabase();
        assert db != null;
        db.update(TABLE_RESPONDENTS, values, "", new String[]{});
        db.close();
    }

    public Question continueSurvey(Answer answer) throws SQLiteDatabaseCorruptException, SQLException {
        storeAnswer(answer);

        Question next = null;
        try {

            //noinspection ConstantConditions
            if (next == null)
                next = retrieveNextQuestion(answer.getAnsweredQuestion());

            if (answer.getClass() == MultipleChoiceAnswer.class) {
                Iterator<Choice> itr = ((MultipleChoiceAnswer) answer).getChoices();
                while (itr.hasNext()) {
                    Choice current = itr.next();

                    if (current.getNextQuestion() != null)
                        next = current.getNextQuestion();

                    if (current.shouldEnd())
                        next = null;
                }
            }

        } catch (Exception ignored) {
        }
        storeNextQuestionId(answer.getRespondent(), next);

        return next;
    }

    public Respondent restoreSurveyRespondent() {
        Respondent result = null;

        SQLiteDatabase db = getReadableDatabase();
        assert db != null;

        Cursor cursor = db.query(VIEW_RESPONDENTS, null, null, null, null, null, null);

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();

            result = new Respondent();
            try {
                result.setId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
            } catch (IllegalArgumentException e) {
                throw new SQLiteDatabaseCorruptException();
            }
        }
        cursor.close();
        db.close();
        return result;
    }

    public Question getNextQuestionForRespondent(Respondent respondent) throws SQLiteDatabaseCorruptException {
        Question result = null;

        SQLiteDatabase db = getReadableDatabase();
        assert db != null;

        Cursor cursor = db.query(TABLE_RESPONDENTS, new String[]{"next_question_id"}, "[_id] = ?", new String[]{String.valueOf(respondent.getId())}, null, null, null);

        if (cursor.getCount() != 1)
            throw new SQLiteDatabaseCorruptException();

        cursor.moveToFirst();

        if (cursor.getType(0) != Cursor.FIELD_TYPE_NULL) {
            int qid = cursor.getInt(0);
            if (qid > 0) {
                result = retrieveQuestionById(qid);
            }
        }

        cursor.close();
        db.close();

        return result;
    }

    public void newRespondent(Respondent respondent) throws SQLException {
        cancelRestoreSurvey();

        ContentValues values = new ContentValues();
        values.put("next_question_id", 1);

        SQLiteDatabase db = getWritableDatabase();
        assert db != null;
        long id = db.insertOrThrow(TABLE_RESPONDENTS, null, values);
        db.close();

        respondent.setId((int) id);
    }

    public void finishRespondent(Respondent respondent) throws SQLException{
        ContentValues values = new ContentValues();
        if (respondent.getCompanyName() == null || respondent.getCompanyName().equals(""))
            values.put("company_name", "anonymous");
        else
            values.put("company_name", respondent.getCompanyName());
        if (!(respondent.getCompanyEmail() == null || respondent.getCompanyEmail().equals("")))
            values.put("company_email", respondent.getCompanyEmail());
        if (!(respondent.getCompanyPerson() == null || respondent.getCompanyPerson().equals("")))
            values.put("company_person", respondent.getCompanyPerson());
        if (!(respondent.getCompanyPostal() < 1000 || respondent.getCompanyPostal() > 9999))
            values.put("company_postal", respondent.getCompanyPostal());
        values.putNull("next_question_id");

        SQLiteDatabase db = getWritableDatabase();
        assert db != null;
        int success = db.update(TABLE_RESPONDENTS, values, " _id = ?", new String[]{String.valueOf(respondent.getId())});
        db.close();

        if (success < 0)
            throw new SQLException();
    }

}
