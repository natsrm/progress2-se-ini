package pt.ulisboa.tecnico.learnjava.sibs.mbway.domain;

import java.util.Hashtable;


public class MBway {

	private static Hashtable<String, String[]> db = new Hashtable<String, String[]>();
	
	public void createNewClient(String phone, String iban, String code) {
		String[] values = new String[2];
		values[0] = iban;
		values[1] = code;
		db.put(phone, values);
	}

	public String getCodeByPhoneNumber(String phone) {
		return db.get(phone)[1];
	}
	
	public String getIbanByPhoneNumber(String phone) {
		return db.get(phone)[0];
	}

	public boolean phoneNumberIsAssociated(String phone) {
		return db.containsKey(phone);
	}
	
	public Hashtable<String, String[]> getDB() {
		return db;
	}
	
	public static void clearMBway() {
		db.clear();
	}
	
}
