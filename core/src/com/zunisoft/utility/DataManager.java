package com.zunisoft.utility;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class DataManager implements Encryptor {
	private Preferences prefs;
	private Encryptor encryptor;
	
	private String valueId = "q";
	private String timeId = "w";
	private String encrId = "e";
	
	public DataManager(String prefName) {
		prefs = Gdx.app.getPreferences(prefName);
		encryptor = this;
	}
	public void clear() {
		prefs.clear();
	}
	public void setEncryptor(Encryptor encryptor) {
		this.encryptor = encryptor;
	}
	public void setValueSub(String value,String time,String encr) {
		valueId = value;
		timeId = time;
		encrId = encr;
	}
	
	public void setBoolean(String key,boolean value) {
		prefs.putBoolean(key, value);
		prefs.flush();
	}
	public boolean getBoolean(String key,boolean defaultValue) {
		return prefs.getBoolean(key, defaultValue);
	}
	public void saveInt(String key, int value) {
		saveIntEncr(key, value);
	}
	public int getInt(String key,int defaultValue) {
		int value1 = prefs.getInteger(key + valueId);
		long value2 = prefs.getLong(key + timeId);
		String value3 = prefs.getString(key + encrId);
		
		String digest = encryptor.encrypt(value1 , value2);
		
		if (digest.equals(value3)) {
			return value1/17;
		} else {
			saveInt(key,defaultValue);
			return defaultValue;
		}		
	}
	
	
	private void saveIntEncr(String key, int value) {
		String key1 = key + valueId;
		String key2 = key + timeId;
		String key3 = key + encrId;

		int value1 = value*17;
		long value2 = new Date().getTime();
		
		String value3 = encryptor.encrypt(value1 , value2);
		
		prefs.putInteger(key1, value1);
		prefs.putLong(key2, value2);
		prefs.putString(key3, value3);

		prefs.flush();

	}

	public static String md5(String input) {
		String md5 = null;

		if (null == input)
			return null;

		try {
			// Create MessageDigest object for MD5
			MessageDigest digest = MessageDigest.getInstance("MD5");
			// Update input string in message digest
			digest.update(input.getBytes(), 0, input.length());
			// Converts message digest value in base 16 (hex)
			md5 = new BigInteger(1, digest.digest()).toString(16);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return md5;
	}
	
	
	@Override
	public String encrypt(int value, long time) {
		return md5(String.valueOf(value + time - 1234123));
	}
	
	

}
