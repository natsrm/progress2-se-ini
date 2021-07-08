package pt.ulisboa.tecnico.learnjava.sibs.mbway.controllers;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.mbway.domain.MBway;

public class MBwayTransferController {

	/* This is the refactor for guideline 'Write clean code - VariableNamingConventions'
	 * We altered the attribute's name by removing _ */
	private String sourcePhone;
	private String targetPhone;
	private int amount;
	private MBway mbway;
	private Services services = new Services();
	
	public MBwayTransferController(String _source_phone, String _target_phone, int _amount, MBway _mbway) {
		sourcePhone = _source_phone;
		targetPhone = _target_phone;
		amount = _amount;
		mbway = _mbway;
	}
	
	public int transfer() throws AccountException, BankException {
		try {
			checkParameters();
		} catch (ClientException e1) {
			return 0;
		}
		if (mbway.phoneNumberIsAssociated(sourcePhone) &&
				mbway.phoneNumberIsAssociated(targetPhone)) {
			String iban_source = mbway.getIbanByPhoneNumber(sourcePhone);
			String iban_target = mbway.getIbanByPhoneNumber(targetPhone);
			try {
				services.withdraw(iban_source, amount);
			} catch (Exception e) {
				return 2;
			}
			try {
				services.deposit(iban_target, amount);
			} catch (Exception e) {
				services.deposit(iban_source, amount);
				return 2;
			}
			return 1;
		}
		return 3;
	}
	
	/* This is the refactor for guideline 'Write clean code - IfElseStmtsMustUseBraces'
	 * We added the braces in lines 55, 57, 58 and 60 */
	public void checkParameters() throws ClientException {
		if (sourcePhone.length() != 9 || !sourcePhone.matches("[0-9]+")) {
			throw new ClientException();
		}
		else if (targetPhone.length() != 9 || !targetPhone.matches("[0-9]+")) {
			throw new ClientException();
		}
	}
	
}
