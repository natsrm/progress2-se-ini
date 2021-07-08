package pt.ulisboa.tecnico.learnjava.sibs.mbway;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.domain.Bank.AccountType;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.mbway.controllers.MBwaySplitInsuranceController;
import pt.ulisboa.tecnico.learnjava.sibs.mbway.domain.MBway;
import pt.ulisboa.tecnico.learnjava.sibs.mbway.domain.MBwayFriends;

public class SplitInsuranceMethodTest {
	
	private Services services;
	private Bank bank;
	private Client client1;
	private Client client2;
	private Client client3;
	private String target_iban;
	private String source_iban2;
	private String source_iban3;
	private String target_phone;
	private String source_phone2;
	private String source_phone3;
	private MBway mbway;
	private MBwayFriends mbway_friends;
	private int num_friends;
	private int amount;
	private MBwaySplitInsuranceController controller;
	private int result;
	private int target_balance;
	private int source_balance2;
	private int source_balance3;
	
	@Before
	public void setUp() throws BankException, AccountException, ClientException {
		this.services = new Services();
		this.bank = new Bank("CGD");
		this.client1 = new Client(bank, "Maria", "Soares", "123456789", "912346987", "Rua Alves Redol", 25);
		this.client2 = new Client(bank, "Clara", "Rodrigues", "987654321", "917895234", "Rua Redol", 30);
		this.client3 = new Client(bank, "Marco", "Andrade", "345123678", "967896734", "Rua da Figueira", 40);
		this.target_iban = bank.createAccount(AccountType.CHECKING, client1, 1000, 0);
		this.source_iban2 = bank.createAccount(AccountType.CHECKING, client2, 5000, 0);
		this.source_iban3 = bank.createAccount(AccountType.CHECKING, client3, 2500, 0);
		this.target_phone = "987654321";
		this.source_phone2 = "912485697";
		this.source_phone3 = "939874561";
		this.mbway = new MBway();
		mbway.createNewClient(target_phone, target_iban, "target");
		mbway.createNewClient(source_phone2, source_iban2, "source2");
		mbway.createNewClient(source_phone3, source_iban3, "source3");
	}
	
	@Test
	public void success() throws BankException, AccountException {
		this.num_friends = 3;
		this.amount = 1000;
		this.mbway_friends = new MBwayFriends(num_friends, amount, target_phone, 250);
		mbway_friends.addFriend(source_phone2, 500);
		mbway_friends.addFriend(source_phone3, 250);
		this.controller = new MBwaySplitInsuranceController(mbway_friends, mbway);
		this.result = controller.splitInsurance();
		this.target_balance = (services.getAccountByIban(target_iban)).getBalance();
		this.source_balance2 = (services.getAccountByIban(source_iban2)).getBalance();
		this.source_balance3 = (services.getAccountByIban(source_iban3)).getBalance();
		assertEquals(1, result);
		assertEquals(1750, target_balance);
		assertEquals(4500, source_balance2);
		assertEquals(2250, source_balance3);
	}
	
	@Test
	public void MissingOneFriend() throws AccountException, BankException {
		this.num_friends = 4;
		this.amount = 1000;
		this.mbway_friends = new MBwayFriends(num_friends, amount, target_phone, 250);
		mbway_friends.addFriend(source_phone2, 500);
		mbway_friends.addFriend(source_phone3, 250);
		this.controller = new MBwaySplitInsuranceController(mbway_friends, mbway);
		this.result = controller.splitInsurance();
		this.target_balance = (services.getAccountByIban(target_iban)).getBalance();
		this.source_balance2 = (services.getAccountByIban(source_iban2)).getBalance();
		this.source_balance3 = (services.getAccountByIban(source_iban3)).getBalance();
		assertEquals(0, result);
		assertEquals(1000, target_balance);
		assertEquals(5000, source_balance2);
		assertEquals(2500, source_balance3);
	}
	
	@Test
	public void MissingMoreThanOneFriend() throws AccountException, BankException {
		this.num_friends = 5;
		this.amount = 1000;
		this.mbway_friends = new MBwayFriends(num_friends, amount, target_phone, 250);
		mbway_friends.addFriend(source_phone2, 500);
		mbway_friends.addFriend(source_phone3, 250);
		this.controller = new MBwaySplitInsuranceController(mbway_friends, mbway);
		this.result = controller.splitInsurance();
		this.target_balance = (services.getAccountByIban(target_iban)).getBalance();
		this.source_balance2 = (services.getAccountByIban(source_iban2)).getBalance();
		this.source_balance3 = (services.getAccountByIban(source_iban3)).getBalance();
		assertEquals(2, result);
		assertEquals(1000, target_balance);
		assertEquals(5000, source_balance2);
		assertEquals(2500, source_balance3);
	}
	
	@Test
	public void OneFriendTooMany() throws AccountException, BankException {
		this.num_friends = 2;
		this.amount = 1000;
		this.mbway_friends = new MBwayFriends(num_friends, amount, target_phone, 250);
		mbway_friends.addFriend(source_phone2, 500);
		mbway_friends.addFriend(source_phone3, 250);
		this.controller = new MBwaySplitInsuranceController(mbway_friends, mbway);
		this.result = controller.splitInsurance();
		this.target_balance = (services.getAccountByIban(target_iban)).getBalance();
		this.source_balance2 = (services.getAccountByIban(source_iban2)).getBalance();
		this.source_balance3 = (services.getAccountByIban(source_iban3)).getBalance();
		assertEquals(3, result);
		assertEquals(1000, target_balance);
		assertEquals(5000, source_balance2);
		assertEquals(2500, source_balance3);
	}
	
	@Test
	public void TooManyFriends() throws AccountException, BankException {
		this.num_friends = 1;
		this.amount = 1000;
		this.mbway_friends = new MBwayFriends(num_friends, amount, target_phone, 250);
		mbway_friends.addFriend(source_phone2, 500);
		mbway_friends.addFriend(source_phone3, 250);
		this.controller = new MBwaySplitInsuranceController(mbway_friends, mbway);
		this.result = controller.splitInsurance();
		this.target_balance = (services.getAccountByIban(target_iban)).getBalance();
		this.source_balance2 = (services.getAccountByIban(source_iban2)).getBalance();
		this.source_balance3 = (services.getAccountByIban(source_iban3)).getBalance();
		assertEquals(4, result);
		assertEquals(1000, target_balance);
		assertEquals(5000, source_balance2);
		assertEquals(2500, source_balance3);
	}
	
	@Test
	public void PayedTooMuch() throws AccountException, BankException {
		this.num_friends = 3;
		this.amount = 1000;
		this.mbway_friends = new MBwayFriends(num_friends, amount, target_phone, 250);
		mbway_friends.addFriend(source_phone2, 500);
		mbway_friends.addFriend(source_phone3, 500);
		this.controller = new MBwaySplitInsuranceController(mbway_friends, mbway);
		this.result = controller.splitInsurance();
		this.target_balance = (services.getAccountByIban(target_iban)).getBalance();
		this.source_balance2 = (services.getAccountByIban(source_iban2)).getBalance();
		this.source_balance3 = (services.getAccountByIban(source_iban3)).getBalance();
		assertEquals(5, result);
		assertEquals(1000, target_balance);
		assertEquals(5000, source_balance2);
		assertEquals(2500, source_balance3);
	}
	
	@Test
	public void PayedTooLittle() throws AccountException, BankException {
		this.num_friends = 3;
		this.amount = 1000;
		this.mbway_friends = new MBwayFriends(num_friends, amount, target_phone, 250);
		mbway_friends.addFriend(source_phone2, 250);
		mbway_friends.addFriend(source_phone3, 250);
		this.controller = new MBwaySplitInsuranceController(mbway_friends, mbway);
		this.result = controller.splitInsurance();
		this.target_balance = (services.getAccountByIban(target_iban)).getBalance();
		this.source_balance2 = (services.getAccountByIban(source_iban2)).getBalance();
		this.source_balance3 = (services.getAccountByIban(source_iban3)).getBalance();
		assertEquals(5, result);
		assertEquals(1000, target_balance);
		assertEquals(5000, source_balance2);
		assertEquals(2500, source_balance3);
	}
	
	@Test
	public void OneFriendWithoutMBway() throws AccountException, BankException {
		this.num_friends = 3;
		this.amount = 1000;
		this.mbway_friends = new MBwayFriends(num_friends, amount, target_phone, 250);
		mbway_friends.addFriend(source_phone2, 500);
		mbway_friends.addFriend("921234567", 250);
		this.controller = new MBwaySplitInsuranceController(mbway_friends, mbway);
		this.result = controller.splitInsurance();
		this.target_balance = (services.getAccountByIban(target_iban)).getBalance();
		this.source_balance2 = (services.getAccountByIban(source_iban2)).getBalance();
		this.source_balance3 = (services.getAccountByIban(source_iban3)).getBalance();
		assertEquals(6, result);
		assertEquals(1000, target_balance);
		assertEquals(5000, source_balance2);
		assertEquals(2500, source_balance3);
	}
	
	@Test
	public void MoreThanOneFriendWithoutMBway() throws AccountException, BankException {
		this.num_friends = 3;
		this.amount = 1000;
		this.mbway_friends = new MBwayFriends(num_friends, amount, target_phone, 250);
		mbway_friends.addFriend("921234568", 500);
		mbway_friends.addFriend("921234567", 250);
		this.controller = new MBwaySplitInsuranceController(mbway_friends, mbway);
		this.result = controller.splitInsurance();
		this.target_balance = (services.getAccountByIban(target_iban)).getBalance();
		this.source_balance2 = (services.getAccountByIban(source_iban2)).getBalance();
		this.source_balance3 = (services.getAccountByIban(source_iban3)).getBalance();
		assertEquals(7, result);
		assertEquals(1000, target_balance);
		assertEquals(5000, source_balance2);
		assertEquals(2500, source_balance3);
	}
	
	@Test
	public void InvalidPhoneNumberLength() throws AccountException, BankException {
		this.num_friends = 3;
		this.amount = 1000;
		this.mbway_friends = new MBwayFriends(num_friends, amount, target_phone, 250);
		mbway_friends.addFriend(source_phone2, 500);
		mbway_friends.addFriend("9212345", 250);
		this.controller = new MBwaySplitInsuranceController(mbway_friends, mbway);
		this.result = controller.splitInsurance();
		this.target_balance = (services.getAccountByIban(target_iban)).getBalance();
		this.source_balance2 = (services.getAccountByIban(source_iban2)).getBalance();
		this.source_balance3 = (services.getAccountByIban(source_iban3)).getBalance();
		assertEquals(8, result);
		assertEquals(1000, target_balance);
		assertEquals(5000, source_balance2);
		assertEquals(2500, source_balance3);
	}
	
	@Test
	public void InvalidPhoneNumberChar() throws AccountException, BankException {
		this.num_friends = 3;
		this.amount = 1000;
		this.mbway_friends = new MBwayFriends(num_friends, amount, target_phone, 250);
		mbway_friends.addFriend(source_phone2, 500);
		mbway_friends.addFriend("PN1234567", 250);
		this.controller = new MBwaySplitInsuranceController(mbway_friends, mbway);
		this.result = controller.splitInsurance();
		this.target_balance = (services.getAccountByIban(target_iban)).getBalance();
		this.source_balance2 = (services.getAccountByIban(source_iban2)).getBalance();
		this.source_balance3 = (services.getAccountByIban(source_iban3)).getBalance();
		assertEquals(8, result);
		assertEquals(1000, target_balance);
		assertEquals(5000, source_balance2);
		assertEquals(2500, source_balance3);
	}
	
	@Test
	public void NotEnoughBalance() throws AccountException, BankException {
		this.num_friends = 3;
		this.amount = 5000;
		this.mbway_friends = new MBwayFriends(num_friends, amount, target_phone, 100);
		mbway_friends.addFriend(source_phone2, 100);
		mbway_friends.addFriend(source_phone3, 4800);
		this.controller = new MBwaySplitInsuranceController(mbway_friends, mbway);
		this.result = controller.splitInsurance();
		this.target_balance = (services.getAccountByIban(target_iban)).getBalance();
		this.source_balance2 = (services.getAccountByIban(source_iban2)).getBalance();
		this.source_balance3 = (services.getAccountByIban(source_iban3)).getBalance();
		assertEquals(9, result);
		assertEquals(1000, target_balance);
		assertEquals(5000, source_balance2);
		assertEquals(2500, source_balance3);
	}
	
	@After
	public void tearDown() {
		Bank.clearBanks();
		MBway.clearMBway();
	}
	
}
