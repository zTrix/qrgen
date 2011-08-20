package com.ztrix.qrgen;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Browser;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public final class SmsPickerAct extends ListActivity {
	private final List<String[]> labelsSms = new ArrayList<String[]>();

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		if (labelsSms.isEmpty()) {
			new LoadSmsAsyncTask(this).execute(labelsSms);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View view, int position, long id) {
		if (position >= 0 && position < labelsSms.size()) {
			String s = labelsSms.get(position)[1];
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			intent.putExtra(Const.Type.SMS, s);
			setResult(RESULT_OK, intent);
		} else {
			setResult(RESULT_CANCELED);
		}
		finish();
	}
}
