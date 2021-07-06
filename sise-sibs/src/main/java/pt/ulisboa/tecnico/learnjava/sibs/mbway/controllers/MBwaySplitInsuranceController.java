package pt.ulisboa.tecnico.learnjava.sibs.mbway.controllers;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.mbway.domain.MBway;
import pt.ulisboa.tecnico.learnjava.sibs.mbway.domain.MBwayFriends;

public class MBwaySplitInsuranceController {

	private int num_family_members;
	private int amount;
	private String target_phone_number;
	private int target_amount;
	private MBwayFriends mbway_friends;
	private MBway mbway;
	private Services services = new Services();
	
	public MBwaySplitInsuranceController(int _num_family_members, int _amount, String _target_phone_number, int _target_amount, MBwayFriends _mbway_friends, MBway _mbway) {
		num_family_members = _num_family_members;
		amount = _amount;
		target_phone_number = _target_phone_number;
		target_amount = _target_amount;
		mbway_friends = _mbway_friends;
		mbway = _mbway;
	}
	
	public int splitInsurance() throws AccountException, BankException {
		if ((num_family_members - 1) - mbway_friends.getTotalNumberOfFriends() == 1)
			return 0;	// Falta 1 friend
		else if ((num_family_members - 1) - mbway_friends.getTotalNumberOfFriends() > 1)
			return 2;	// Falta mais que um friend
		else if ((num_family_members - 1) - mbway_friends.getTotalNumberOfFriends() == -1)
			return 3;	// 1 friend a mais
		else if ((num_family_members - 1) - mbway_friends.getTotalNumberOfFriends() < -1)
			return 4;	// mais que um friend a mais
		else if ((amount - target_amount) != mbway_friends.getTotalAmount())
			return 5;	// Pago demasiado/a menos
		else
			try {
				if (verifyFriends() == 1)
					return 6;	// um friend nao tem mbway
				else if (verifyFriends() > 1)
					return 7;	//mais que um friend nao tem mbway
			} catch (ClientException e1) {
				return 8;		// invalid phone number
			}
		String target_iban = mbway.getIbanByPhoneNumber(target_phone_number);
		for (int i = 0; i < num_family_members - 1; i++) {
			String source_phone_number = mbway_friends.getPhoneNumber(i);
			int transfer_amount = mbway_friends.getAmount(source_phone_number);
			String source_iban = mbway.getIbanByPhoneNumber(source_phone_number);
			try {
				services.withdraw(source_iban, transfer_amount);
				services.deposit(target_iban, transfer_amount);
			} catch (Exception e) {
				return 9;	//nao tem saldo
			}
		}
		return 1;
	}
	
	public int verifyFriends() throws ClientException {
		int counter = 0;
		for (String phone : mbway_friends.getDB().keySet()) {
			if (phone.length() != 9 || !phone.matches("[0-9]+"))
				throw new ClientException();
			else if (!mbway.phoneNumberIsAssociated(phone))
				counter++;
		}
		return counter;
	}
	
	
}
