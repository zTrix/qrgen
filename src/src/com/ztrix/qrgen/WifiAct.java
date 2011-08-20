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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class WifiAct extends Activity {
	private static final String TAG = WifiAct.class.getSimpleName();
	private Button genWifiQR;
	private Context self=this;
	private Spinner spinner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		if (intent!=null){
			String action=intent.getAction();
			if (action.equals(Const.Wifi.ACTION)||action.equals(Intent.ACTION_SEND)){
				Utils.dbg(TAG, "setContentView");
				setContentView(R.layout.wifi);
				
				spinner = (Spinner) findViewById(R.id.wifi_spinner);
			    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
			            this, R.array.wifi_prompt, android.R.layout.simple_spinner_item);
			    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			    spinner.setAdapter(adapter);
			    
				genWifiQR=(Button)findViewById(R.id.wifi_gen_button);
				genWifiQR.setOnClickListener(genWifiQRListener);
				return;
			}
		}
		finish();
	}

	private final Button.OnClickListener genWifiQRListener = new Button.OnClickListener() {
		public void onClick(View v) {
			StringBuffer sb=new StringBuffer(300);
			sb.append("WIFI:");
			String s=((EditText)findViewById(R.id.wifi_input_id)).getText().toString();
			if(s.length()==0){
				Utils.showAlert(self, getString(R.string.qrgen_error), getString(R.string.wifi_please_input));
				return;
			}
			sb.append("S:"+s+";");
			s=((EditText)findViewById(R.id.wifi_input_pass)).getText().toString();
			if(s.length()>0)sb.append("P:"+s+";");
			if(spinner!=null){
				int sel=spinner.getSelectedItemPosition();
				if(sel==0){
					sb.append("T:WEP;");
				}else if(sel==1){
					sb.append("T:WPA;");
				}
			}
			sb.append(";");
			startActivity(Utils.getIntentTextEncode(sb.toString()));
		}
	};
}