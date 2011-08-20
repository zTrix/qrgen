package com.ztrix.qrgen.history;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import android.preference.PreferenceManager;
import android.util.Log;
import com.google.zxing.BarcodeFormat;
import com.ztrix.qrgen.Const;
import com.ztrix.qrgen.R;
import com.ztrix.qrgen.QRGen;
import com.google.zxing.Result;

public final class HistoryManager {

	private static final String TAG = HistoryManager.class.getSimpleName();	
	private QRGen activity;
	private static HistoryManager instance;

	private static final int MAX_ITEMS = 50;
	// private static final String[] TEXT_COL_PROJECTION = { DBHelper.TEXT_COL
	// };
	
	private static final String[] ID_COL_PROJECTION = { DBHelper.ID_COL };
	private static final DateFormat EXPORT_DATE_TIME_FORMAT = DateFormat
			.getDateTimeInstance();

	public HistoryManager(QRGen activity) {
		this.activity = activity;
		instance=this;
	}
	
	public static HistoryManager getInstance(){
		return instance;
	}

	List<HistoryItem> getHistoryItems() {
		SQLiteOpenHelper helper = new DBHelper(activity);
		List<HistoryItem> items = new ArrayList<HistoryItem>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query(DBHelper.TABLE_NAME, null,null, null, null, null, DBHelper.TIMESTAMP_COL + " DESC");
			while (cursor.moveToNext()) {
				items.add(new HistoryItem(cursor.getLong(0),cursor.getString(1)));
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			db.close();
		}
		return items;
	}

	public AlertDialog buildAlert() {
		List<HistoryItem> items = getHistoryItems();
		int size = items.size();
		String[] dialogItems = new String[size + 2];
		for (int i = 0; i < size; i++) {
			dialogItems[i] = items.get(i).getText();
		}
		Resources res = activity.getResources();
		dialogItems[dialogItems.length - 2] = res
				.getString(R.string.history_send);
		dialogItems[dialogItems.length - 1] = res
				.getString(R.string.history_clear_text);
		DialogInterface.OnClickListener clickListener = new HistoryClickListener(this, activity, dialogItems, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.history_title);
		builder.setItems(dialogItems, clickListener);
		return builder.create();
	}

	public void addHistoryItem(HistoryItem result) {
		
		deletePrevious(result.getText());

		SQLiteOpenHelper helper = new DBHelper(activity);
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			// Insert
			ContentValues values = new ContentValues();
			values.put(DBHelper.TIMESTAMP_COL, result.getTimeStamp());
			values.put(DBHelper.TEXT_COL, result.getText());
			db.insert(DBHelper.TABLE_NAME, DBHelper.TIMESTAMP_COL, values);
		} finally {
			db.close();
		}
	}

	private void deletePrevious(String text) {
		SQLiteOpenHelper helper = new DBHelper(activity);
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			db.delete(DBHelper.TABLE_NAME, DBHelper.TEXT_COL + "=?",
					new String[] { text });
		} finally {
			db.close();
		}
	}

	public void trimHistory() {
		SQLiteOpenHelper helper = new DBHelper(activity);
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query(DBHelper.TABLE_NAME, ID_COL_PROJECTION, null,
					null, null, null, DBHelper.TIMESTAMP_COL + " DESC");
			int count = 0;
			while (count < MAX_ITEMS && cursor.moveToNext()) {
				count++;
			}
			while (cursor.moveToNext()) {
				db.delete(DBHelper.TABLE_NAME,
						DBHelper.ID_COL + '=' + cursor.getString(0), null);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			db.close();
		}
	}

	CharSequence buildHistory() {
		StringBuilder historyText = new StringBuilder(1000);
		SQLiteOpenHelper helper = new DBHelper(activity);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query(DBHelper.TABLE_NAME, DBHelper.EXPORT_COL_PROJECTION, null,
					null, null, null, DBHelper.TIMESTAMP_COL + " DESC");
			while (cursor.moveToNext()) {
				for (int col = 0; col < DBHelper.EXPORT_COL_PROJECTION.length; col++) {
					historyText.append('"').append(massageHistoryField(cursor.getString(col))).append("\",");
				}
				long timestamp = cursor.getLong(DBHelper.EXPORT_COL_PROJECTION.length-1);
				historyText.append('"').append(massageHistoryField(EXPORT_DATE_TIME_FORMAT.format(new Date(timestamp)))).append("\"\r\n");
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			db.close();
		}
		return historyText;
	}

	static Uri saveHistory(String history) {
		File bsRoot = new File(Environment.getExternalStorageDirectory(),"QRGen");
		File historyRoot = new File(bsRoot, "History");
		if (!historyRoot.exists() && !historyRoot.mkdirs()) {
			Log.w(TAG, "Couldn't make dir " + historyRoot);
			return null;
		}
		File historyFile = new File(historyRoot, "history-"+System.currentTimeMillis() + ".csv");
		OutputStreamWriter out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(historyFile),
					Charset.forName("UTF-8"));
			out.write(history);
			return Uri.parse("file://" + historyFile.getAbsolutePath());
		} catch (IOException ioe) {
			Log.w(TAG, "Couldn't access file " + historyFile + " due to " + ioe);
			return null;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ioe) {
					// do nothing
				}
			}
		}
	}

	private static String massageHistoryField(String value) {
		return value.replace("\"", "\"\"");
	}

	void clearHistory() {
		SQLiteOpenHelper helper = new DBHelper(activity);
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			db.delete(DBHelper.TABLE_NAME, null, null);
		} finally {
			db.close();
		}
	}
}
