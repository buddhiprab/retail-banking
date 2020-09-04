package com.buddhi.banking;

import com.buddhi.banking.models.User;
import com.buddhi.banking.repository.TxnRepository;
import com.buddhi.banking.repository.UserRepository;
import com.buddhi.banking.services.BankingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = BankingApplication.class)
class BankingServiceTests {
	@Autowired
	BankingService bankingService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	TxnRepository txnRepository;

	private static ByteArrayOutputStream outContent;
	private static User alice;

	@BeforeEach
	private void beforeEach() {
		outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
	}

	@Test
	public void firstTimeLoginTest() {
		login("Alice");

		assertEquals("Hello, Alice!\n" +
				"Your balance is 0.0.\n", outContent.toString());
	}

	@Test
	public void loginTopupTest() {
		login("Alice");
		topup("Alice", "100");

		assertEquals("Hello, Alice!\n" +
				"Your balance is 0.0.\n" +
				"Your balance is 100.0.\n", outContent.toString());
	}

	@Test
	public void loginTwoUsersTopupPay() {
		login("Alice");
		topup("Alice", "100");
		login("Bob");
		topup("Bob", "80");
		pay("Bob", "50", "Alice");

		assertEquals("Hello, Alice!\n" +
				"Your balance is 0.0.\n" +
				"Your balance is 100.0.\n" +
				"Hello, Bob!\n" +
				"Your balance is 0.0.\n" +
				"Your balance is 80.0.\n" +
				"Transferred 50.0 to Alice.\n" +
				"Your balance is 30.0.\n", outContent.toString());
	}

	@Test
	public void loginTwoUsersTopupPayOwing() {
		login("Alice");
		topup("Alice", "100");
		login("Bob");
		topup("Bob", "80");
		pay("Bob", "50", "Alice");
		pay("Bob", "100", "Alice");

		assertEquals("Hello, Alice!\n" +
				"Your balance is 0.0.\n" +
				"Your balance is 100.0.\n" +
				"Hello, Bob!\n" +
				"Your balance is 0.0.\n" +
				"Your balance is 80.0.\n" +
				"Transferred 50.0 to Alice.\n" +
				"Your balance is 30.0.\n" +
				"Transferred 30.0 to Alice.\n" +
				"Your balance is 0.0.\n" +
				"Owing 70.0 to Alice.\n", outContent.toString());
	}

	@Test
	public void loginTwoUsersTopupPayOwingTopup() {
		login("Alice");
		topup("Alice", "100");
		login("Bob");
		topup("Bob", "80");
		pay("Bob", "50", "Alice");
		pay("Bob", "100", "Alice");
		topup("Bob", "30");

		assertEquals("Hello, Alice!\n" +
				"Your balance is 0.0.\n" +
				"Your balance is 100.0.\n" +
				"Hello, Bob!\n" +
				"Your balance is 0.0.\n" +
				"Your balance is 80.0.\n" +
				"Transferred 50.0 to Alice.\n" +
				"Your balance is 30.0.\n" +
				"Transferred 30.0 to Alice.\n" +
				"Your balance is 0.0.\n" +
				"Owing 70.0 to Alice.\n" +
				"Transferred 30.0 to Alice.\n" +
				"Your balance is 0.0.\n" +
				"Owing 40.0 to Alice.\n", outContent.toString());
	}

	@Test
	public void loginTwoUsersPayOwingUser() {
		login("Alice");
		topup("Alice", "100");
		login("Bob");
		topup("Bob", "80");
		pay("Bob", "50", "Alice");
		pay("Bob", "100", "Alice");
		topup("Bob", "30");
		login("Alice");
		pay("Alice", "30", "Bob");

		assertEquals("Hello, Alice!\n" +
				"Your balance is 0.0.\n" +
				"Your balance is 100.0.\n" +
				"Hello, Bob!\n" +
				"Your balance is 0.0.\n" +
				"Your balance is 80.0.\n" +
				"Transferred 50.0 to Alice.\n" +
				"Your balance is 30.0.\n" +
				"Transferred 30.0 to Alice.\n" +
				"Your balance is 0.0.\n" +
				"Owing 70.0 to Alice.\n" +
				"Transferred 30.0 to Alice.\n" +
				"Your balance is 0.0.\n" +
				"Owing 40.0 to Alice.\n" +
				"Hello, Alice!\n" +
				"Owing 40.0 from Bob.\n" +
				"Your balance is 210.0.\n" +
				"Owing 10.0 from Bob.\n" +
				"Your balance is 210.0.\n", outContent.toString());
	}

	@Test
	public void loginTwoUsersPayOwingUserTopup() {
		login("Alice");
		topup("Alice", "100");
		login("Bob");
		topup("Bob", "80");
		pay("Bob", "50", "Alice");
		pay("Bob", "100", "Alice");
		topup("Bob", "30");
		login("Alice");
		pay("Alice", "30", "Bob");
		login("Bob");
		topup("Bob", "100");

		assertEquals("Hello, Alice!\n" +
				"Your balance is 0.0.\n" +
				"Your balance is 100.0.\n" +
				"Hello, Bob!\n" +
				"Your balance is 0.0.\n" +
				"Your balance is 80.0.\n" +
				"Transferred 50.0 to Alice.\n" +
				"Your balance is 30.0.\n" +
				"Transferred 30.0 to Alice.\n" +
				"Your balance is 0.0.\n" +
				"Owing 70.0 to Alice.\n" +
				"Transferred 30.0 to Alice.\n" +
				"Your balance is 0.0.\n" +
				"Owing 40.0 to Alice.\n" +
				"Hello, Alice!\n" +
				"Owing 40.0 from Bob.\n" +
				"Your balance is 210.0.\n" +
				"Owing 10.0 from Bob.\n" +
				"Your balance is 210.0.\n" +
				"Hello, Bob!\n" +
				"Your balance is 0.0.\n" +
				"Owing 10.0 to Alice.\n" +
				"Transferred 10.0 to Alice.\n" +
				"Your balance is 90.0.\n", outContent.toString());
	}

	private void login(String name) {
		bankingService.login(name);
	}

	private void topup(String name, String amount) {
		bankingService.topUp(name, amount);
	}

	private void pay(String name, String amount, String to) {
		bankingService.pay(name, amount, to);
	}

	@AfterEach
	private void cleanupAfterEach() {
		userRepository.deleteAll();
		txnRepository.deleteAll();

	}
}
