package com.ztrix.qrgen;

import android.provider.BaseColumns;
import android.provider.Contacts;

public final class Const {
	private Const() {
	}
	
	public static final class Type {
		public static final String TEXT = "TEXT_TYPE";
		public static final String EMAIL = "EMAIL_TYPE";
		public static final String PHONE = "PHONE_TYPE";
		public static final String SMS = "SMS_TYPE";
		public static final String CONTACT = "CONTACT_TYPE";
		public static final String LOCATION = "LOCATION_TYPE";

		private Type() {
		}
	}

	public static final String[] PHONE_KEYS = { Contacts.Intents.Insert.PHONE,
			Contacts.Intents.Insert.SECONDARY_PHONE,
			Contacts.Intents.Insert.TERTIARY_PHONE };

	public static final String[] EMAIL_KEYS = { Contacts.Intents.Insert.EMAIL,
			Contacts.Intents.Insert.SECONDARY_EMAIL,
			Contacts.Intents.Insert.TERTIARY_EMAIL };

	public static final String[] PHONES_PROJECTION = { BaseColumns._ID, // 0
			Contacts.PhonesColumns.NUMBER // 1
	};

	public static final String[] METHODS_PROJECTION = { BaseColumns._ID, // 0
			Contacts.ContactMethodsColumns.KIND, // 1
			Contacts.ContactMethodsColumns.DATA, // 2
	};

	public static final class Encode {
		public static final String ACTION = "com.ztrix.qrgen.ENCODE";
		public static final String DATA = "ENCODE_DATA";
		public static final String TYPE = "ENCODE_TYPE";
		public static final String FORMAT = "ENCODE_FORMAT";

		private Encode() {
		}
	}

	public static final class Card {
		public static final String ACTION = "com.ztrix.qrgen.CARD";
		public static final String DATA = "CARD_DATA";
		public static final String TYPE = "CARD_TYPE";
		public static final String FORMAT = "CARD_FORMAT";

		private Card() {
		}
	}
	
	public static final class Wifi{
		public static final String ACTION = "com.ztrix.qrgen.WIFI";
		public static final String DATA = "WIFI_DATA";
		public static final String TYPE = "WIFI_TYPE";
		public static final String FORMAT = "WIFI_FORMAT";

		private Wifi() {
		}
	}
	
	public static final class Bkmk{
		public static final String ACTION = "com.ztrix.qrgen.BKMK";
		public static final String DATA = "BKMK_DATA";
		public static final String TYPE = "BKMK_TYPE";
		public static final String FORMAT = "BKMK_FORMAT";

		private Bkmk() {
		}
	}

}
