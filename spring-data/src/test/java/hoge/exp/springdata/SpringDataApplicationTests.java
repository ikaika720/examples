package hoge.exp.springdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import hoge.exp.springdata.webapp.Account;
import hoge.exp.springdata.webapp.AccountController;
import hoge.exp.springdata.webapp.Transaction;

@SpringBootTest
class SpringDataApplicationTests {

	WebTestClient webTestClient;

	@Autowired
	WebApplicationContext context;

	@BeforeEach
	void setUp() {
		this.webTestClient = MockMvcWebTestClient.bindToApplicationContext(this.context).build();
	}

	@Autowired
	AccountController ac;

	@Test
	void accountAssertTest() {
		Account act = new Account(1L, BigDecimal.valueOf(1234, 2));
		assertThat(act)
				.extracting(Account::getId, Account::getBalance)
				.containsExactly(1L, BigDecimal.valueOf(1234, 2));
	}

	@Transactional
	@Test
	@Sql(statements = "TRUNCATE TABLE account CASCADE")
	void accountControllerCreateAndGetTest() {
		ac.createAccount(100L, "123.45");
		Account act = ac.getAccount(100L).getBody();

		assertEquals(100L, act.getId());
		assertEquals(new BigDecimal("123.45"), act.getBalance());
		assertEquals(LocalDateTime.now().toString().substring(0, 13), act.getLastUpdated().toString().substring(0, 13));

		ac.createAccount(101L, "678.90");
		var accounts = ac.getAllAccounts().getBody();

		assertEquals(2, accounts.size());

		assertEquals(100L, accounts.get(0).getId());
		assertEquals(new BigDecimal("123.45"), accounts.get(0).getBalance());
		assertEquals(LocalDateTime.now().toString().substring(0, 13),
				accounts.get(0).getLastUpdated().toString().substring(0, 13));

		assertEquals(101L, accounts.get(1).getId());
		assertEquals(new BigDecimal("678.90"), accounts.get(1).getBalance());
		assertEquals(LocalDateTime.now().toString().substring(0, 13),
				accounts.get(1).getLastUpdated().toString().substring(0, 13));
	}

	@Transactional
	@Test
	@Sql(statements = {
			"TRUNCATE TABLE account CASCADE",
			"INSERT INTO account (id, balance, lastUpdated) VALUES (200, 100.00, current_timestamp)",
			"INSERT INTO account (id, balance, lastUpdated) VALUES (201, 100.00, current_timestamp)"
	})
	void accountControllerTransferTest() {
		ac.transfer(200L, 201L, "1.1", 0L);

		var accounts = ac.getAllAccounts().getBody();

		assertEquals(2, accounts.size());

		assertEquals(200L, accounts.get(0).getId());
		assertEquals(new BigDecimal("98.90"), accounts.get(0).getBalance());
		assertEquals(LocalDateTime.now().toString().substring(0, 13),
				accounts.get(0).getLastUpdated().toString().substring(0, 13));

		assertEquals(201L, accounts.get(1).getId());
		assertEquals(new BigDecimal("101.10"), accounts.get(1).getBalance());
		assertEquals(LocalDateTime.now().toString().substring(0, 13),
				accounts.get(1).getLastUpdated().toString().substring(0, 13));

		var transactions = ac.getAllTransactions().getBody();

		assertEquals(2, transactions.size());

		assertEquals(200L, transactions.get(0).getAccount());
		assertEquals(new BigDecimal("-1.10"), transactions.get(0).getAmount());
		assertEquals(LocalDate.now(), transactions.get(0).getDate());
		assertEquals(new BigDecimal("98.90"), transactions.get(0).getRunningBalance());

		assertEquals(201L, transactions.get(1).getAccount());
		assertEquals(new BigDecimal("1.10"), transactions.get(1).getAmount());
		assertEquals(LocalDate.now(), transactions.get(1).getDate());
		assertEquals(new BigDecimal("101.10"), transactions.get(1).getRunningBalance());
	}

	@Test
	@Sql(statements = {
			"TRUNCATE TABLE transaction CASCADE",
			"TRUNCATE TABLE account CASCADE",
	}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(statements = {
			"TRUNCATE TABLE transaction CASCADE",
			"TRUNCATE TABLE account CASCADE",
	}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	void httpRequestTest() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		{ // Create Account 200
			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			map.add("id", "200");
			map.add("balance", "100");

			webTestClient.post()
					.uri("/account/new")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.bodyValue(map)
					.exchange()
					.expectStatus().isCreated()
					.expectBody(Account.class).value(actual -> {
						assertThat(actual)
								.extracting(Account::getId, Account::getBalance)
								.containsExactly(200L, new BigDecimal("100.00"));
						assertThat(actual.getLastUpdated()).isNotNull();
					});
		}

		{ // Create Account 201
			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			map.add("id", "201");
			map.add("balance", "123.45");

			webTestClient.post()
					.uri("/account/new")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.bodyValue(map)
					.exchange()
					.expectStatus().isCreated()
					.expectBody(Account.class).value(actual -> {
						assertThat(actual)
								.extracting(Account::getId, Account::getBalance)
								.containsExactly(201L, new BigDecimal("123.45"));
						assertThat(actual.getLastUpdated()).isNotNull();
					});
		}

		{ // Transfer
			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			map.add("accountFrom", "200");
			map.add("accountTo", "201");
			map.add("amount", "1.23");
			map.add("sleep", "250");

			webTestClient.post()
					.uri("/account/transfer")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.bodyValue(map)
					.exchange()
					.expectStatus().isOk();
		}

		{ // Check Account 200 after transfer
			webTestClient.get()
					.uri("/account/200")
					.exchange()
					.expectStatus().isOk()
					.expectBody(Account.class).value(actual -> {
						assertThat(actual)
								.extracting(Account::getId, Account::getBalance)
								.containsExactly(200L, new BigDecimal("98.77"));
						assertThat(actual.getLastUpdated()).isNotNull();
					});
		}

		{ // Check Account 201 after transfer
			webTestClient.get()
					.uri("/account/201")
					.exchange()
					.expectStatus().isOk()
					.expectBody(Account.class).value(actual -> {
						assertThat(actual)
								.extracting(Account::getId, Account::getBalance)
								.containsExactly(201L, new BigDecimal("124.68"));
						assertThat(actual.getLastUpdated()).isNotNull();
					});
		}

		{ // Check the transactions
			webTestClient.get()
					.uri("/transaction/list")
					.exchange()
					.expectStatus().isOk()
					.expectBody(Transaction[].class).value(transactions -> {
						assertThat(transactions)
								.extracting(Transaction::getAccount, Transaction::getAmount,
										Transaction::getRunningBalance)
								.containsExactly(
										tuple(200L, new BigDecimal("-1.23"), new BigDecimal("98.77")),
										tuple(201L, new BigDecimal("1.23"), new BigDecimal("124.68")));
					});
		}
	}
}
