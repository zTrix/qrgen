package com.ztrix.qrgen.history;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;

import com.ztrix.qrgen.QRGen;
import com.ztrix.qrgen.R;
import com.ztrix.qrgen.Utils;
import com.ztrix.qrgen.encode.EncodeActivity;

final class HistoryClickListener implements DialogInterface.OnClickListener {

	private final HistoryManager historyManager;
	private final QRGen activity;
	private final String[] dialogItems;
	private final List<HistoryItem> items;

	HistoryClickListener(HistoryManager historyManager,
			QRGen activity, String[] dialogItems, List<HistoryItem> items) {
		this.historyManager = historyManager;
		this.activity = activity;
		this.dialogItems = dialogItems;
		this.items = items;
	}

	public void onClick(DialogInterface dialogInterface, int i) {
		if (i == dialogItems.length - 1) {
			historyManager.clearHistory();
		} else if (i == dialogItems.length - 2) {
			CharSequence history = historyManager.buildHistory();
			Uri historyFile = HistoryManager.saveHistory(history.toString());
			if (historyFile == null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setMessage(R.string.msg_unmount_usb);
				builder.setPositiveButton(R.string.button_ok, null);
				builder.show();
				return;
			}
			Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			String subject = activity.getResources().getString(
					R.string.history_email_title);
			intent.putExtra(Intent.EXTRA_SUBJECT, subject);
			intent.putExtra(Intent.EXTRA_TEXT, subject);
			intent.putExtra(Intent.EXTRA_STREAM, historyFile);
			intent.setType("text/csv");
			activity.startActivity(intent);
		} else {
			activity.startActivity(Utils.getIntentTextEncode(dialogItems[i]));
		}
	}
}
