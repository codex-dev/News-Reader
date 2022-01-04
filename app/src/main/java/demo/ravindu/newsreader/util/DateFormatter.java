package demo.ravindu.newsreader.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateFormatter {
    public static String convertTimeFromUtcToLocal(String outputDateFormat, String dateUTC) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat sdfOutPutToSend = new SimpleDateFormat(outputDateFormat, Locale.ENGLISH);
        sdfOutPutToSend.setTimeZone(TimeZone.getDefault());

        Date date = sdf.parse(dateUTC);
        String dateLocal = sdfOutPutToSend.format(date);
        return dateLocal;
    }
}
