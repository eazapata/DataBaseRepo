package com.android.example.wordlistsql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WordListOpenHelper extends SQLiteOpenHelper {
    // It's a good idea to always define a log tag like this.
    private static final String TAG = WordListOpenHelper.class.getSimpleName();
    // has to be 1 first time or app will crash
    private static final int DATABASE_VERSION = 1;
    private static final String WORD_LIST_TABLE = "word_entries";
    private static final String DATABASE_NAME = "wordlist";
    // Column names...
    public static final String KEY_ID = "_id";
    public static final String KEY_WORD = "word";
    // ... and a string array of columns.
    private static final String[] COLUMNS = {KEY_ID, KEY_WORD};

    // Build the SQL query that creates the table.
    private static final String WORD_LIST_TABLE_CREATE =
            "CREATE TABLE " + WORD_LIST_TABLE + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY, " +
// id will auto-increment if no value passed
                    KEY_WORD + " TEXT );";
    private SQLiteDatabase mWritableDB;
    private SQLiteDatabase mReadableDB;

    public WordListOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public WordItem query(int position) {
        String query = "SELECT * FROM " + WORD_LIST_TABLE +
                " ORDER BY " + KEY_WORD + " ASC " +
                "LIMIT " + position + ",1";
        Cursor cursor = null;
        WordItem wordItem = new WordItem();
        try {
            if (mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }
            cursor = mReadableDB.rawQuery(query, null);
            cursor.moveToFirst();
            wordItem.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            wordItem.setWord(cursor.getString(cursor.getColumnIndex(KEY_WORD)));
        } catch (Exception e) {
            Log.d(TAG, "EXCEPTION! " + e);
        } finally {
            cursor.close();
            return wordItem;
        }
    }

    public long insert(String word) {
        long newId = 0;
        ContentValues values = new ContentValues();
        values.put(KEY_WORD, word);
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            newId = mWritableDB.insert(WORD_LIST_TABLE, null, values);

        } catch (Exception e) {
            Log.d(TAG, "INSERT EXCEPTION! " + e.getMessage());
        }
        return newId;
    }

    public int delete(int id) {
        int deleted = 0;
        long rows;
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
          rows =  count();
            deleted = mWritableDB.delete(WORD_LIST_TABLE,
                    KEY_ID + " = ? ", new String[]{String.valueOf(id)});
           rows = count();
        } catch (Exception e) {
            Log.d (TAG, "DELETE EXCEPTION! " + e.getMessage());
        }
        return deleted;
    }

    public int update(int id, String word){
        int mNumberOfRowsUpdated = -1;
        try{
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            ContentValues values = new ContentValues();
            values.put(KEY_WORD, word);
            mNumberOfRowsUpdated = mWritableDB.update(WORD_LIST_TABLE,
                    values, // new values to insert
            // selection criteria for row (the _id column)
                    KEY_ID + " = ?",
            //selection args; value of id
                    new String[]{String.valueOf(id)});

        }catch (Exception e){
            Log.d (TAG, "UPDATE EXCEPTION: " + e.getMessage());
        }
        return mNumberOfRowsUpdated;
    }

    public long count() {
        if (mReadableDB == null) {
            mReadableDB = getReadableDatabase();
        }
        return DatabaseUtils.queryNumEntries(mReadableDB, WORD_LIST_TABLE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WORD_LIST_TABLE_CREATE);
        fillDatabaseWithData(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(WordListOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + WORD_LIST_TABLE);
        onCreate(db);


    }

    private void fillDatabaseWithData(SQLiteDatabase db) {
        String[] words = {"Android", "Adapter", "ListView", "AsyncTask",
                "Android Studio", "SQLiteDatabase", "SQLOpenHelper",
                "Data model", "ViewHolder", "Android Performance",
                "OnClickListener"};
        ContentValues values = new ContentValues();
        for (int i = 0; i < words.length; i++) {
// Put column/value pairs into the container.
// put() overrides existing values.
            values.put(KEY_WORD, words[i]);
            db.insert(WORD_LIST_TABLE, null, values);
        }
    }
}
