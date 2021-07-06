package pt.ulisboa.tecnico.learnjava.sibs.mbway.domain;

import java.util.Hashtable;

public class MBwayFriends {

	private int num_family_members;
	private int amount;
	private String target_phone_number;
	private int target_amount;
	private Hashtable<String, Integer> db = new Hashtable<String, Integer>();
	
	public MBwayFriends(int _num_family_members, int _amount, String _target_phone_number, int _target_amount) {
		num_family_members = _num_family_members;
		amount = _amount;
		target_phone_number = _target_phone_number;
		target_amount = _target_amount;
	}
	
	public void addFriend(String phone, int amount) {
		db.put(phone, amount);
	}

	public int getTotalAmountPaidByFriends() {
		int total_amount = 0;
		for (Integer i : db.values()) {
			total_amount += i;
		}
		return total_amount;
	}
	
	public Hashtable<String, Integer> getDB() {
		return db;
	}
	
	public int getTotalNumberOfFriends() {
		return db.size();
	}
	
	public String getPhoneNumber(int i) {
		return (String) db.keySet().toArray()[i];
	}
	
	public int getFriendAmount(String phone) {
		return db.get(phone);
	}
	

	public int getNumFamilyMembers() {
		return num_family_members;
	}

	public int getTotalAmount() {
		return amount;
	}

	public String getTargetPhoneNumber() {
		return target_phone_number;
	}

	public int getTargetAmount() {
		return target_amount;
	}
	
}
