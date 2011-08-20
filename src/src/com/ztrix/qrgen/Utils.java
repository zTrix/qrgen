package com.ztrix.qrgen;

import com.google.zxing.BarcodeFormat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Utils{
	private Utils(){}
	
	public static void dbg(String tag,String content){
		Log.i(tag, "-----------------------------------------------\n"+content);
	}
	
	public static void showAlert(Context obj,String title,String s){
		AlertDialog.Builder ab=new AlertDialog.Builder(obj);
		ab.setMessage(s).setCancelable(false).setPositiveButton(obj.getString(R.string.qrgen_ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		AlertDialog diag=ab.create();
		diag.setTitle(title);
		diag.setIcon(R.drawable.qrgen);
		diag.show();
	}
	
	public static Intent getIntentTextEncode(String text) {
		if (text == null) return null;
		if(text.length()>1000)text=text.substring(0,1000);
		Intent intent = new Intent(Const.Encode.ACTION);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.putExtra(Const.Encode.TYPE, Const.Type.TEXT);
		intent.putExtra(Const.Encode.DATA, text);
		intent.putExtra(Const.Encode.FORMAT, BarcodeFormat.QR_CODE.toString());
		return intent;
	}
	
	public static Intent getIntentContactEncode(Bundle bundle){
		Intent intent = new Intent(Const.Encode.ACTION);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.putExtra(Const.Encode.TYPE, Const.Type.CONTACT);
		intent.putExtra(Const.Encode.DATA, bundle);
		intent.putExtra(Const.Encode.FORMAT,
				BarcodeFormat.QR_CODE.toString());
		return intent;
	}
}