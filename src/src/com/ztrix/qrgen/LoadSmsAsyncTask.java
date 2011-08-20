package com.ztrix.qrgen;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import com.ztrix.qrgen.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

final class LoadSmsAsyncTask extends
	AsyncTask<List<String[]>, Void, List<String[]>> {

	private final SmsPickerAct activity;
	private DialogInterface dialog;
	private static final String TAG = LoadSmsAsyncTask.class.getSimpleName();

	LoadSmsAsyncTask(SmsPickerAct activity) {
		this.activity = activity;
	}

	@Override
	protected synchronized void onPreExecute() {
		dialog = ProgressDialog.show(activity, "",
				activity.getString(R.string.msg_loading_sms), true, true);
	}

	@Override
	protected synchronized void onCancelled() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

	@Override
	protected List<String[]> doInBackground(List<String[]>... objects) {
		List<String[]> labelsPackages = objects[0];
		Cursor cursor = activity.getContentResolver().query(Uri.parse("content://sms"),
				new String[]{ "_id", "thread_id", "address", "person",
				"date", "body"},
				null,null,"date DESC");
		String addr="";
		String text="";
		while(cursor.moveToNext()){
			for(int i=0;i<cursor.getColumnCount();i++){
				String name=cursor.getColumnName(i);
				String value=cursor.getString(i);
				Utils.dbg(TAG, name+"\t\t"+value);
				if(name.equals("address")){
					addr=value;
				}else if(name.equals("body")){
					text=value;
				}
				
			}
			labelsPackages.add(new String[]{"item",addr+":"+text});
		}
		return labelsPackages;
	}

	@Override
	protected synchronized void onPostExecute(List<String[]> results) {
		List<String> labels = new ArrayList<String>(results.size());
		for (String[] result : results) {
			if(result[1].length()>31)
				labels.add(result[1].substring(0,31)+"...");
			else labels.add(result[1]);
		}
		ListAdapter listAdapter = new ArrayAdapter<String>(activity,
				android.R.layout.simple_list_item_1, labels);
		activity.setListAdapter(listAdapter);
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

}
