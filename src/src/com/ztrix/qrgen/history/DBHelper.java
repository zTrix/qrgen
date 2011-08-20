package com.ztrix.qrgen.history;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

final class DBHelper extends SQLiteOpenHelper {
  private static final int DB_VERSION = 2;
  private static final String DB_NAME = "qr_gen_history.db";
  static final String TABLE_NAME = "history";
  static final String ID_COL = "id";
  static final String TEXT_COL = "text";
  static final String TIMESTAMP_COL = "timestamp";
  
  public static final String[] EXPORT_COL_PROJECTION = {
      DBHelper.TEXT_COL,
      DBHelper.TIMESTAMP_COL,
  };

  DBHelper(Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(
            "CREATE TABLE " + TABLE_NAME + " (" +
            ID_COL + " INTEGER PRIMARY KEY, " +
            TEXT_COL + " TEXT, " +
            TIMESTAMP_COL + " INTEGER" +
            ");");
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    onCreate(sqLiteDatabase);
  }
}
