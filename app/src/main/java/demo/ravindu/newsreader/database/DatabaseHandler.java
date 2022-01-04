package demo.ravindu.newsreader.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import demo.ravindu.newsreader.model.PreviousQuery;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "queryManager";
    private static final String TABLE_PREVIOUS_QUERY = "queries";
    private static final String KEY_ID = "id";
    private static final String KEY_QUERY_DESC = "query_desc";

    public DatabaseHandler(Context context) {
        //3rd argument to be passed is CursorFactory instance
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_QUERY_TABLE = "CREATE TABLE " + TABLE_PREVIOUS_QUERY + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_QUERY_DESC + " TEXT)";
        db.execSQL(CREATE_QUERY_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREVIOUS_QUERY);

        // Create tables again
        onCreate(db);
    }

    // code to add the new query
    void addQuery(PreviousQuery previousQuery) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_QUERY_DESC, previousQuery.getQueryDesc()); // Query Description

        // Inserting Row
        //2nd argument is String containing nullColumnHack
        db.insert(TABLE_PREVIOUS_QUERY, null, values);

        db.close(); // Closing database connection
    }

    // code to get all queries in a list view
    public List<PreviousQuery> getAllQueries() {
        List<PreviousQuery> listQueries = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PREVIOUS_QUERY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PreviousQuery query = new PreviousQuery();
                query.setId(Integer.parseInt(cursor.getString(0)));
                query.setQueryDesc(cursor.getString(1));

                // Adding query to list
                listQueries.add(query);
            } while (cursor.moveToNext());
        }

        // return query list
        return listQueries;
    }

    // Getting queries Count
    public int getQueryCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PREVIOUS_QUERY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}
