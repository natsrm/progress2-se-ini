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
	
	/* This is the refactor for guideline 'Keep Unit Interfaces Small'
	 * We altered the constructor of class MBwayFriends to receive the other parameters that were eliminated */
	public MBwaySplitInsuranceController(MBwayFriends _mbway_friends, MBway _mbway) {
		mbway_friends = _mbway_friends;
		mbway = _mbway;
		num_family_members = mbway_friends.getNumFamilyMembers();
		amount = mbway_friends.getTotalAmount();
		target_phone_number = mbway_friends.getTargetPhoneNumber();
		target_amount = mbway_friends.getTargetAmount();	
	}
	
	/* This is the refactor for guidelines 'write simple units of code' and 'write short units of code'
	 * We created a new method to perform the validation of the parameters, and a new method to perform
	 * the transfers to the target iban. This way, both the amount of code lines and branches decreased. */
	public int splitInsurance() throws AccountException, BankException {
		int parameters_confirmation = checkParameters();
		if (parameters_confirmation != 1)
			return parameters_confirmation;
		else {
			String target_iban = mbway.getIbanByPhoneNumber(target_phone_number);
			int transfer = transferToTarget(target_iban);
			return transfer;
		}
	}
	
	public int checkParameters() {
		if ((num_family_members - 1) - mbway_friends.getTotalNumberOfFriends() == 1)
			return 0;	// 1 friend misisng
		else if ((num_family_members - 1) - mbway_friends.getTotalNumberOfFriends() > 1)
			return 2;	// More than 1 friend missing
		else if ((num_family_members - 1) - mbway_friends.getTotalNumberOfFriends() == -1)
			return 3;	// 1 friend too many
		else if ((num_family_members - 1) - mbway_friends.getTotalNumberOfFriends() < -1)
			return 4;	// More than 1 friend too many
		else if ((amount - target_amount) != mbway_friends.getTotalAmountPaidByFriends())
			return 5;	// Paid value is different (less or more)
		else
			try {
				if (verifyFriends() == 1)
					return 6;	// 1 friend doesn't have mbway
				else if (verifyFriends() > 1)
					return 7;	// More than 1 friend doesn't have mbway
			} catch (ClientException e1) {
				return 8;		// Invalid phone number
			}
		return 1;
	}
	
	public int transferToTarget(String target_iban) throws AccountException, BankException {
		for (int i = 0; i < num_family_members - 1; i++) {
			String source_phone_number = mbway_friends.getPhoneNumber(i);
			int transfer_amount = mbway_friends.getFriendAmount(source_phone_number);
			String source_iban = mbway.getIbanByPhoneNumber(source_phone_number);
			try {
				services.withdraw(source_iban, transfer_amount);
			} catch (Exception e) {
				return 9;	// Not enough balance
			}
			try {
				services.deposit(target_iban, transfer_amount);
			} catch (Exception e) {
				services.deposit(source_iban, transfer_amount);
				return 9;	// Return the value that was already withdrawn
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
