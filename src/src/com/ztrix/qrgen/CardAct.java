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
import android.provider.Contacts;
import android.provider.Contacts.Intents;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CardAct extends Activity {
	private static final String TAG = CardAct.class.getSimpleName();
	private Button genCardQR;
	private Context self=this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		if (intent!=null){
			String action=intent.getAction();
			if (action.equals(Const.Card.ACTION)||action.equals(Intent.ACTION_SEND)){
				Utils.dbg(TAG, "setContentView");
				setContentView(R.layout.card);
				genCardQR=(Button)findViewById(R.id.card_gen_button);
				genCardQR.setOnClickListener(genCardQRListener);
				return;
			}
		}
		finish();
	}

	private final Button.OnClickListener genCardQRListener = new Button.OnClickListener() {
		public void onClick(View v) {
			Bundle bundle = new Bundle();
			StringBuffer sb=new StringBuffer(300);
			sb.append("BIZCARD:");
			Boolean has=false;
			String s;
			s=((EditText)findViewById(R.id.card_input_name)).getText().toString();
			if(s.length()>0){
				has=true;
				sb.append("N:"+s+";");
			}
			s=((EditText)findViewById(R.id.card_input_phone)).getText().toString();
			if(s.length()>0){
				has=true;
				sb.append("B:"+s+";");
			}
			s=((EditText)findViewById(R.id.card_input_email)).getText().toString();
			if(s.length()>0){
				has=true;
				sb.append("E:"+s+";");
			}
			s=((EditText)findViewById(R.id.card_input_addr)).getText().toString();
			if(s.length()>0){
				has=true;
				sb.append("A:"+s+";");
			}
			s=((EditText)findViewById(R.id.card_input_com)).getText().toString();
			if(s.length()>0){
				has=true;
				sb.append("C:"+s+";");
			}
			s=((EditText)findViewById(R.id.card_input_job)).getText().toString();
			if(s.length()>0){
				has=true;
				sb.append("T:"+s+";");
			}
			sb.append(";");
			if(has==false){
				Utils.showAlert(self, getString(R.string.qrgen_error), getString(R.string.card_please_input));
			}else{
				startActivity(Utils.getIntentTextEncode(sb.toString()));
			}
		}
	};
}