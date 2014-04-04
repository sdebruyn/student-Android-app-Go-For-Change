package be.hubrussel.ti.goforchange.enquete.entities;

/**
 * Created by Samuel on 2/04/2014.
 */
public enum QuestionType {

    RANGE, NUMERIC, YEAR, TEXT, MULTIPLE_CHOICE;

    /**
     * This returns the type of a question based on a string. It defaults to TEXT when it doesn't recognize the type.
     * @param input The string to convert.
     * @return A QuestionType based on the string.
     */
    public static QuestionType fromString(String input){

        try{
            return valueOf(input.trim().replace(" ", "_").toUpperCase());
        }catch(Exception e){
            return TEXT;
        }

    }

}
