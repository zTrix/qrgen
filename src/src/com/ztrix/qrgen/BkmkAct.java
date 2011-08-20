package com.ztrix.qrgen;

import com.google.zxing.BarcodeFormat;
import com.ztrix.qrgen.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.Browser;
import android.provider.Contacts;
import android.provider.Contacts.Intents;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class BkmkAct extends Activity {
	private static final String TAG = BkmkAct.class.getSimpleName();
	private Context self = this;
	private final int PICK_BOOKMARK = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		if (intent != null) {
			String action = intent.getAction();
			if (action.equals(Const.Bkmk.ACTION)
					|| action.equals(Intent.ACTION_SEND)) {
				Utils.dbg(TAG, "setContentView");
				setContentView(R.layout.bkmk);

				findViewById(R.id.bkmk_gen_button).setOnClickListener(
						genBkmkQRListener);
				findViewById(R.id.bkmk_gen_from_user_button)
						.setOnClickListener(genUserBkmkQRListener);
				return;
			}
		}
		finish();
	}

	private final Button.OnClickListener genUserBkmkQRListener = new Button.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			intent.setClassName(BkmkAct.this,
					BookmarkPickerActivity.class.getName());
			startActivityForResult(intent, PICK_BOOKMARK);
		}
	};

	private final Button.OnClickListener genBkmkQRListener = new Button.OnClickListener() {
		public void onClick(View v) {
			StringBuffer sb = new StringBuffer(300);
			sb.append("MEBKM:");
			String name=((EditText)findViewById(R.id.bkmk_input_name)).getText().toString();
			if (name.length()>0)sb.append("TITLE:"+name+";");
			String url=((EditText)findViewById(R.id.bkmk_input_url)).getText().toString();
			if (url.length()>0){
				if(url.length()>7&&url.substring(0, 7).toLowerCase().equals("http://")||url.length()>8&&url.substring(0, 8).toLowerCase().equals("https://")){
				}else{
					url="http://"+url;
				}
				sb.append("URL:"+url+";;");
			}
			if(url.length()>0){
				if(name.length()>0){
					startActivity(Utils.getIntentTextEncode(sb.toString()));
				}else{
					startActivity(Utils.getIntentTextEncode(url));
				}
			}else{
				Utils.showAlert(self, getString(R.string.qrgen_error), getString(R.string.bkmk_please_input));
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case PICK_BOOKMARK:
				startActivity(Utils.getIntentTextEncode(intent.getStringExtra(Browser.BookmarkColumns.URL)));
				break;
			}
		}
	}
}