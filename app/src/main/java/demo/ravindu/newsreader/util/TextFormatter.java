package demo.ravindu.newsreader.util;

import android.widget.EditText;
import android.widget.TextView;

public class TextFormatter {

    public static boolean isNullOrEmpty(EditText et) {
        return et.getText() == null || et.getText().toString().trim().isEmpty();
    }

    public static boolean isNullOrEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    public static boolean isNullOrEmpty(TextView tv) {
        return tv.getText() == null || tv.getText().toString().trim().isEmpty();
    }

    public static String getTrimmedText(EditText et) {
        return et.getText().toString().trim();
    }

    public static String getTrimmedText(TextView tv) {
        return tv.getText().toString().trim();
    }
}
