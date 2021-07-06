package pt.ulisboa.tecnico.learnjava.sibs.mbway;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.domain.Bank.AccountType;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.mbway.controllers.AssociateMBwayController;
import pt.ulisboa.tecnico.learnjava.sibs.mbway.controllers.ConfirmMBwayController;
import pt.ulisboa.tecnico.learnjava.sibs.mbway.domain.MBway;

public class ConfirmMBWayCodeMethodTest {
	
	private String iban;
	private String phone_number;
	private String confirmation_code;
	private String generated_code;
	private MBway mbway = new MBway();
	private Services services;
	private Bank bank;
	private Client client;
	private ConfirmMBwayController confirm;
	private AssociateMBwayController associate;
	private int db_code;
	
	@Before
	public void setUp() throws BankException, AccountException, ClientException {
		this.services = new Services();
		this.bank = new Bank("CGD");
		this.client = new Client(bank, "Maria", "Soares", "123456789", "912346987", "Rua Alves Redol", 25);
		this.iban = bank.createAccount(AccountType.CHECKING, client, 1000, 0);
		this.phone_number = "987654321";
		this.confirmation_code = "546798";
	}
	
	@Test
	public void success() {
		this.associate = new AssociateMBwayController(iban, phone_number, mbway, services);
		generated_code = associate.setClient();
		this.confirm = new ConfirmMBwayController(phone_number, generated_code,  mbway);
		db_code = confirm.confirmMBwayCode();
		assertEquals(1,db_code); 
		assertEquals(6, confirmation_code.length());
	}
	
	@Test
	public void DifferentCode() {
		this.associate = new AssociateMBwayController(iban, phone_number, mbway, services);
		generated_code = associate.setClient();
		this.confirm = new ConfirmMBwayController(phone_number, confirmation_code, mbway);
		db_code = confirm.confirmMBwayCode();
		assertEquals(false, confirmation_code == generated_code);
		assertEquals(0,db_code); 
		
	}

	@Test
	public void DifferentPhoneNumber() {
		this.associate = new AssociateMBwayController(iban, phone_number, mbway, services);
		confirmation_code = associate.setClient();
		this.confirm = new ConfirmMBwayController("915835949", confirmation_code, mbway);
		db_code = confirm.confirmMBwayCode();
		assertEquals(2,db_code); 
		
	}
	
	@Test
	public void InvalidPhoneNumber() {
		this.associate = new AssociateMBwayController(iban, phone_number, mbway, services);
		confirmation_code = associate.setClient();
		this.confirm = new ConfirmMBwayController("9158359", confirmation_code, mbway);
		db_code = confirm.confirmMBwayCode();
		assertEquals(2,db_code); 
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
		MBway.clearMBway();
	}

}
