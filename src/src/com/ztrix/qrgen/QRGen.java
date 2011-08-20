package com.ztrix.qrgen;

import java.util.Date;
import com.ztrix.qrgen.*;
import com.ztrix.qrgen.history.*;
import com.google.zxing.BarcodeFormat;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.Browser;
import android.provider.Contacts;
import android.provider.Contacts.Intents;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

public class QRGen extends Activity {
	private static final String TAG = QRGen.class.getSimpleName();
	private Context self = this;
	private HistoryManager hm;

	private static final int PICK_TEXTIN = 0;
	private static final int PICK_CONTACT = 1;
	private static final int PICK_CLIPBOARD = 2;
	private static final int PICK_APP = 3;
	private static final int PICK_SMS = 4;

	private static final int PHONES_NUMBER_COLUMN = 1;

	private static final int METHODS_KIND_COLUMN = 1;
	private static final int METHODS_DATA_COLUMN = 2;

	private static final int HISTORY_ID = 0;
	private static final int ABOUT_ID = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		hm = new HistoryManager(this);
		hm.trimHistory();

		findViewById(R.id.contact_button).setOnClickListener(contactListener);
		findViewById(R.id.clipboard_button).setOnClickListener(
				clipboardListener);
		findViewById(R.id.gen_button).setOnClickListener(genListener);
		findViewById(R.id.card_button).setOnClickListener(cardListener);
		findViewById(R.id.date_button).setOnClickListener(dateListener);
		findViewById(R.id.wifi_button).setOnClickListener(wifiListener);
		findViewById(R.id.bkmk_button).setOnClickListener(bkmkListener);
		findViewById(R.id.app_button).setOnClickListener(appListener);
		findViewById(R.id.sms_button).setOnClickListener(smsListener);
		// chkClipBoard();
	}

	private final Button.OnClickListener smsListener = new Button.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			intent.setClassName(QRGen.this, SmsPickerAct.class.getName());
			intent.putExtra(Const.Encode.TYPE, Const.Type.SMS);
			startActivityForResult(intent, PICK_SMS);
		}
	};

	private final Button.OnClickListener appListener = new Button.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			intent.setClassName(QRGen.this, AppPickerActivity.class.getName());
			startActivityForResult(intent, PICK_APP);
		}
	};

	private final Button.OnClickListener bkmkListener = new Button.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(Const.Bkmk.ACTION);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			intent.putExtra(Const.Card.TYPE, Const.Type.TEXT);
			startActivity(intent);
		}
	};

	private final Button.OnClickListener wifiListener = new Button.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(Const.Wifi.ACTION);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			intent.putExtra(Const.Card.TYPE, Const.Type.TEXT);
			startActivity(intent);
		}
	};

	private final Button.OnClickListener dateListener = new Button.OnClickListener() {
		public void onClick(View v) {
			StringBuffer sb = new StringBuffer(100);
			Date d = new Date();
			Resources res=self.getResources();
			sb.append(d.getYear() + 1900).append(getString(R.string.qrgen_year)).append(d.getMonth())
					.append(getString(R.string.qrgen_month)).append(d.getDate()).append(getString(R.string.qrgen_day));
			sb.append("\n").append(res.getStringArray(R.array.weekday)[d.getDay()]);
			sb.append("\n").append(d.getHours()).append(getString(R.string.qrgen_hour))
					.append(d.getMinutes()).append(getString(R.string.qrgen_minute));
			startActivity(Utils.getIntentTextEncode(sb.toString()));
		}
	};

	private final Button.OnClickListener cardListener = new Button.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(Const.Card.ACTION);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			intent.putExtra(Const.Card.TYPE, Const.Type.TEXT);
			startActivity(intent);
			Utils.dbg(TAG, "click card button");
		}
	};

	private final Button.OnClickListener genListener = new Button.OnClickListener() {
		public void onClick(View v) {
			Button genBtn = (Button) findViewById(R.id.gen_button);
			EditText input_text = (EditText) findViewById(R.id.input_text);
			String a = input_text.getText().toString();
			if (a.length() > 0) {
				startActivity(Utils.getIntentTextEncode(a));
			} else {
				Utils.showAlert(self, getString(R.string.qrgen_error), getString(R.string.qrgen_please_input));
			}
		}
	};

	private void showAlert(String title, String s) {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setMessage(s).setCancelable(false)
				.setPositiveButton(getString(R.string.qrgen_ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		AlertDialog diag = ab.create();
		diag.setTitle(title);
		diag.setIcon(R.drawable.qrgen);
		diag.show();
	}

	private final Button.OnClickListener contactListener = new Button.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(Intent.ACTION_PICK,
					Contacts.People.CONTENT_URI);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			startActivityForResult(intent, PICK_CONTACT);
		}
	};

	private final Button.OnClickListener clipboardListener = new Button.OnClickListener() {
		public void onClick(View v) {
			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			if (clipboard.hasText()) {
				Intent intent = new Intent(Const.Encode.ACTION);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				intent.putExtra(Const.Encode.TYPE, Const.Type.TEXT);
				intent.putExtra(Const.Encode.DATA, clipboard.getText()
						.toString());
				intent.putExtra(Const.Encode.FORMAT,
						BarcodeFormat.QR_CODE.toString());
				startActivity(intent);
			} else {
				Utils.showAlert(self, getString(R.string.qrgen_error), getString(R.string.clipboard_empty));
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case PICK_CONTACT:
				showContactAsBarcode(intent.getData());
				break;
			case PICK_APP:
				startActivity(Utils.getIntentTextEncode(intent
						.getStringExtra(Browser.BookmarkColumns.URL)));
				break;
			case PICK_SMS:
				startActivity(Utils.getIntentTextEncode(intent
						.getStringExtra(Const.Type.SMS)));
				break;
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// chkClipBoard();
	}

	private void chkClipBoard() {
		ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		if (clipboard.hasText()) {
			findViewById(R.id.clipboard_button).setEnabled(true);
			((Button) findViewById(R.id.clipboard_button))
					.setText(R.string.button_share_clipboard);
		} else {
			findViewById(R.id.clipboard_button).setEnabled(false);
			((Button) findViewById(R.id.clipboard_button))
					.setText(R.string.button_clipboard_empty);
		}
	}

	private void showContactAsBarcode(Uri contactUri) {
		Log.i(TAG, "Showing contact URI as barcode: " + contactUri);
		if (contactUri == null)
			return;
		ContentResolver resolver = getContentResolver();
		Cursor contactCursor = resolver.query(contactUri, null, null, null,
				null);
		Bundle bundle = new Bundle();
		if (contactCursor != null && contactCursor.moveToFirst()) {
			int nameColumn = contactCursor
					.getColumnIndex(Contacts.PeopleColumns.NAME);
			String name = contactCursor.getString(nameColumn);
			if (name != null && name.length() > 0) {
				bundle.putString(Contacts.Intents.Insert.NAME,
						massageContactData(name));
			}
			contactCursor.close();

			Uri phonesUri = Uri.withAppendedPath(contactUri,
					Contacts.People.Phones.CONTENT_DIRECTORY);
			Cursor phonesCursor = resolver.query(phonesUri,
					Const.PHONES_PROJECTION, null, null, null);
			if (phonesCursor != null) {
				int foundPhone = 0;
				while (phonesCursor.moveToNext()) {
					String number = phonesCursor
							.getString(PHONES_NUMBER_COLUMN);
					if (foundPhone < Const.PHONE_KEYS.length) {
						bundle.putString(Const.PHONE_KEYS[foundPhone],
								massageContactData(number));
						foundPhone++;
					}
				}
				phonesCursor.close();
			}

			Uri methodsUri = Uri.withAppendedPath(contactUri,
					Contacts.People.ContactMethods.CONTENT_DIRECTORY);
			Cursor methodsCursor = resolver.query(methodsUri,
					Const.METHODS_PROJECTION, null, null, null);
			if (methodsCursor != null) {
				int foundEmail = 0;
				boolean foundPostal = false;
				while (methodsCursor.moveToNext()) {
					int kind = methodsCursor.getInt(METHODS_KIND_COLUMN);
					String data = methodsCursor.getString(METHODS_DATA_COLUMN);
					switch (kind) {
					case Contacts.KIND_EMAIL:
						if (foundEmail < Const.EMAIL_KEYS.length) {
							bundle.putString(Const.EMAIL_KEYS[foundEmail],
									massageContactData(data));
							foundEmail++;
						}
						break;
					case Contacts.KIND_POSTAL:
						if (!foundPostal) {
							bundle.putString(Contacts.Intents.Insert.POSTAL,
									massageContactData(data));
							foundPostal = true;
						}
						break;
					}
				}
				methodsCursor.close();
			}
			Utils.dbg(TAG, "Sending bundle for encoding: " + bundle);
			if (!bundle.isEmpty())
				startActivity(Utils.getIntentContactEncode(bundle));
		}
	}

	private static String massageContactData(String data) {
		if (data.indexOf('\n') >= 0) {
			data = data.replace("\n", " ");
		}
		if (data.indexOf('\r') >= 0) {
			data = data.replace("\r", " ");
		}
		return data;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, HISTORY_ID, 0, R.string.menu_history).setIcon(
				android.R.drawable.ic_menu_recent_history);
		menu.add(0, ABOUT_ID, 0, R.string.menu_about).setIcon(
				android.R.drawable.ic_menu_info_details);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case HISTORY_ID:
			AlertDialog historyAlert = hm.buildAlert();
			historyAlert.show();
			break;
		case ABOUT_ID:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.title_about));
			builder.setMessage(getString(R.string.msg_about)+"\n\n"+getString(R.string.author));
			builder.setIcon(R.drawable.qrgen);
			builder.setPositiveButton(R.string.button_ok,null);
			builder.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}