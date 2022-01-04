package demo.ravindu.newsreader.util;

import android.widget.EditText;

public class TextFormatter {

    // check if the given edit text field value is null or empty
    public static boolean isNullOrEmpty(EditText et) {
        return et.getText() == null || et.getText().toString().trim().isEmpty();
    }

    // check if the given string value is null or empty
    public static boolean isNullOrEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    // get the trimmed string value of a given edit text field
    public static String getTrimmedText(EditText et) {
        return et.getText().toString().trim();
    }
}
