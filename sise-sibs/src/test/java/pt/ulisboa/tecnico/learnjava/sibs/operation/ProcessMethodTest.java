package pt.ulisboa.tecnico.learnjava.sibs.operation;

import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Account;
import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.Bank.AccountType;
import pt.ulisboa.tecnico.learnjava.bank.domain.CheckingAccount;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Cancelled;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Completed;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Deposited;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Operation;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Retry;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Withdrawn;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Error;

import static org.junit.Assert.assertEquals;

import org.junit.After;

public class ProcessMethodTest {

	private static final String ADDRESS = "Ave.";
	private static final String PHONE_NUMBER = "987654321";
	private static final String NIF = "123456789";
	private static final String LAST_NAME = "Silva";
	private static final String FIRST_NAME = "António";
	private static final String SOURCE_IBAN_ERROR = "source_error";
	private static final String TARGET_IBAN_ERROR = "target_error";
	
	private static final int VALUE = 50;
	private static final int ID = 1;

	private Bank sourceBank;
	private Bank targetBank;
	private Client sourceClient;
	private Client targetClient;
	private Services services;
	private Operation operation;
	private String source_iban;
	private String target_iban;
	private Operation operation_error;

	@Before
	public void setUp() throws BankException, AccountException, ClientException, OperationException {
		this.services = new Services();
		this.sourceBank = new Bank("CGD");
		this.targetBank = new Bank("BPI");
		this.sourceClient = new Client(this.sourceBank, FIRST_NAME, LAST_NAME, NIF, PHONE_NUMBER, ADDRESS, 33);
		this.targetClient = new Client(this.targetBank, FIRST_NAME, LAST_NAME, NIF, PHONE_NUMBER, ADDRESS, 22);
		this.source_iban = sourceBank.createAccount(AccountType.CHECKING, sourceClient, 100, 50);
		this.target_iban = targetBank.createAccount(AccountType.CHECKING, targetClient, 100, 50);
		this.operation = new Operation(source_iban, target_iban, VALUE, ID);
	}

	@Test
	public void Registeredsuccess() throws BankException, AccountException, SibsException, OperationException, ClientException {
		operation.process(services);
		int source_balance = (services.getAccountByIban(source_iban)).getBalance();
		assertEquals(true, operation.getState() instanceof Withdrawn);
		assertEquals(50, source_balance);
	}
	
	@Test
	public void Withdrawnsuccess() throws BankException, AccountException, SibsException, OperationException, ClientException {
		operation.process(services);
		operation.process(services);
		int target_balance = (services.getAccountByIban(target_iban)).getBalance();
		assertEquals(true, operation.getState() instanceof Deposited);
		assertEquals(150, target_balance);
	}
	
	@Test
	public void Depositedsuccess() throws BankException, AccountException, SibsException, OperationException, ClientException {
		operation.process(services);
		operation.process(services);
		operation.process(services);
		int source_balance = (services.getAccountByIban(source_iban)).getBalance();
		assertEquals(true, operation.getState() instanceof Completed);
		assertEquals(49, source_balance);
	}
	
	@Test
	public void Completedsuccess() throws BankException, AccountException, SibsException, OperationException, ClientException {
		operation.process(services);
		operation.process(services);
		operation.process(services);
		operation.process(services);
		assertEquals(true, operation.getState() instanceof Completed);
	}
	
	@Test
	public void CancelledSuccess() throws AccountException, BankException {
		operation.cancel(services);
		operation.process(services);
		assertEquals(true, operation.getState() instanceof Cancelled);
	}
	
	@Test
	public void ErrorSuccess() throws OperationException, AccountException, BankException {
		operation_error = new Operation(source_iban, target_iban, 1000, ID);
		operation_error.process(services);
		operation_error.process(services);
		operation_error.process(services);
		assertEquals(true, operation_error.getState() instanceof Error);
	}
	
	@Test 
	public void NotEnoughBalanceWithdraw() throws AccountException, OperationException, BankException {
		operation_error = new Operation(source_iban, target_iban, 1000, ID);
		operation_error.process(services);
		assertEquals(true, operation_error.getState() instanceof Retry);
	}
	
	@Test
	public void NotEnoughBalanceCommission() throws OperationException, AccountException, BankException {
		operation_error = new Operation(source_iban, target_iban, 100, ID);
		operation_error.process(services);
		operation_error.process(services);
		operation_error.process(services);
		assertEquals(true, operation_error.getState() instanceof Retry);
	}
	
	@Test
	public void InvalidSourceAccountRetry () throws AccountException, OperationException, BankException {
		operation_error = new Operation(SOURCE_IBAN_ERROR, target_iban, VALUE, ID);
		operation_error.process(services);
		assertEquals(true, operation_error.getState() instanceof Retry);
		
	}
	
	@Test 
	public void InvalidTargetAccountRetry () throws AccountException, OperationException, BankException {
		operation_error = new Operation(source_iban, TARGET_IBAN_ERROR, VALUE, ID);
		operation_error.process(services);
		operation_error.process(services);
		assertEquals(true, operation_error.getState() instanceof Retry);
	}
	
//	@Test (expected = BankException.class)
//	public void InvalidAccounts () throws AccountException, OperationException, BankException {
//		String target_iban2 = "CGDTarget_iban2";
//		String source_iban2 = "CGDSource_iban2";
//		Operation op4 = new Operation(source_iban2, target_iban2, VALUE, ID);
//		op4.process(services);
//	}
	
	@Test
	public void InvalidSourceAccountBeforeCompleted() throws BankException, AccountException {
		operation.process(services);
		operation.process(services);
		sourceBank.deleteAccount(services.getAccountByIban(source_iban));
		operation.process(services);
		assertEquals(true, operation.getState() instanceof Retry);
	}
	
	@Test 
	public void NotEnoughBalanceWithdrawError() throws AccountException, OperationException, BankException {
		operation_error = new Operation(source_iban, target_iban, 1000, ID);
		operation_error.process(services);
		operation_error.process(services);
		assertEquals(true, operation_error.getState() instanceof Error);
	}
	
	@Test
	public void NotEnoughBalanceCommissionError() throws OperationException, AccountException, BankException {
		operation_error = new Operation(source_iban, target_iban, 100, ID);
		operation_error.process(services);
		operation_error.process(services);
		operation_error.process(services);
		operation_error.process(services);
		assertEquals(true, operation_error.getState() instanceof Error);
	}
	
	@Test
	public void InvalidSourceAccountError() throws AccountException, OperationException, BankException {
		operation_error = new Operation(SOURCE_IBAN_ERROR, target_iban, VALUE, ID);
		operation_error.process(services);
		operation_error.process(services);
		assertEquals(true, operation_error.getState() instanceof Error);
		
	}
	
	@Test 
	public void InvalidTargetAccountError() throws AccountException, OperationException, BankException {
		operation_error = new Operation(source_iban, TARGET_IBAN_ERROR, VALUE, ID);
		operation_error.process(services);
		operation_error.process(services);
		operation_error.process(services);
		assertEquals(true, operation_error.getState() instanceof Error);
	}
	
	@Test
	public void InvalidSourceAccountBeforeCompletedError() throws BankException, AccountException {
		operation.process(services);
		operation.process(services);
		sourceBank.deleteAccount(services.getAccountByIban(source_iban));
		operation.process(services);
		operation.process(services);
		assertEquals(true, operation.getState() instanceof Error);
	}
	
	@Test
	public void InactiveAccountRegistered() throws BankException, AccountException {
		Account source_account = services.getAccountByIban(source_iban);
		source_account.inactive((CheckingAccount) source_account);
		operation.process(services);
		assertEquals(true, operation.getState() instanceof Retry);
	}
	
	@Test
	public void InactiveAccountWithdrawn() throws BankException, AccountException {
		operation.process(services);
		Account target_account = services.getAccountByIban(target_iban);
		target_account.inactive((CheckingAccount) target_account);
		operation.process(services);
		assertEquals(true, operation.getState() instanceof Retry);
	}
	
	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}
