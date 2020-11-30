package com.example.exp2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDbHelper extends SQLiteOpenHelper {

    public static final String _ID = "_id";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String CREATE_TIME = "create_time";
    public static final String AUTHOR = "author";
    public static final String PHOTO_PATH = "photo_path";

    private final Context context;
    private SQLiteDatabase db;
    static String CREATE_TABLE = "create table diary(" +
            "_id integer primary key autoincrement," +
            TITLE + " text," +
            CONTENT + " text," +
            CREATE_TIME + " integer," +
            AUTHOR + " text," +
            PHOTO_PATH + " text" +
            ")";

    public MyDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        Toast.makeText(context, "数据库创建成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS diary");
        db.execSQL(CREATE_TABLE);
        Toast.makeText(context, "数据库升级成功", Toast.LENGTH_SHORT).show();
    }

    public void deleteAllDiary() {
        db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS diary");
        db.execSQL(CREATE_TABLE);
    }

    public void createDiary(String title,String author) {
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MyDbHelper.TITLE, title);
        values.put(MyDbHelper.CONTENT,"");
        java.util.Date date = new java.util.Date();
        values.put(MyDbHelper.CREATE_TIME, date.getTime());
        values.put(MyDbHelper.AUTHOR, author);
        db.insert("diary", null, values);
        values.clear();
    }

    public void deleteDiary(int _id) {
        db = getWritableDatabase();
        db.delete("diary", "_id=?", new String[]{String.valueOf(_id)});
    }

    public void updateDiary(int _id, String title, String content, String photo_path) {
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        if (title != null)
            values.put(TITLE, title);
        if (content != null)
            values.put(CONTENT, content);
        if (photo_path != null)
            values.put(PHOTO_PATH, photo_path);
        db.update("diary", values, "_id=?", new String[]{String.valueOf(_id)});
        values.clear();
    }

    public Cursor getDiaryListCursor() {
        db = getWritableDatabase();
        return db.query("diary", null, null, null, null, null, null, null);
    }
}
