package demo.ravindu.newsreader.database;

import android.content.Context;
import android.util.Log;

import java.util.List;

import demo.ravindu.newsreader.model.PreviousQuery;

public class DatabaseManager {

    private static final String TAG = "DatabaseManager";

    private static DatabaseManager manager;
    private final DatabaseHandler databaseHandler;

    private DatabaseManager(Context context) {
        databaseHandler = new DatabaseHandler(context);
    }

    public static DatabaseManager getInstance(Context context) {
        // singleton pattern - only one instance is being used at any given moment.
        if (manager == null) {
            manager = new DatabaseManager(context);
        }
        return manager;
    }

    public void insertQuery(String query) {
        Log.d(TAG, "Inserting new query : " + query);
        databaseHandler.addQuery(new PreviousQuery(query));
    }

    public List<PreviousQuery> getAllQueries() {
        // Read all queries
        List<PreviousQuery> listQueries = databaseHandler.getAllQueries();

        for (PreviousQuery pq : listQueries) {
            String log = "[Id: " + pq.getId() + " , Query Description: " + pq.getQueryDesc() + "]";
            Log.d(TAG, log);
        }

        return listQueries;
    }
}
