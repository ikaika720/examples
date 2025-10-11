package hoge.exp.springdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import hoge.exp.springdata.webapp.Account;
import hoge.exp.springdata.webapp.AccountController;
import hoge.exp.springdata.webapp.Transaction;
import jakarta.persistence.EntityManager;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@SuppressWarnings("null")
class SpringDataApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired AccountController ac;
	@Autowired EntityManager em;

	@Test
	void contextLoads() {
		Account act = new Account(1L, BigDecimal.valueOf(1234, 2));
		assertEquals(act.getId(), 1L);
		assertEquals(act.getBalance(), BigDecimal.valueOf(1234, 2));

	}

	@Transactional
	@Test
	void accountControllerCreateAndGetTest() {
		ac.clear();

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
		assertEquals(LocalDateTime.now().toString().substring(0, 13), accounts.get(0).getLastUpdated().toString().substring(0, 13));

		assertEquals(101L, accounts.get(1).getId());
		assertEquals(new BigDecimal("678.90"), accounts.get(1).getBalance());
		assertEquals(LocalDateTime.now().toString().substring(0, 13), accounts.get(1).getLastUpdated().toString().substring(0, 13));

		ac.clear();
	}

	@Transactional
	@Test
	void accountControllerTransferTest() {
		ac.clear();

		ac.createAccount(200L, "100.00");
		ac.createAccount(201L, "100.00");

		em.flush();

		ac.transfer(200L, 201L, "1.1", 0L);

		var accounts = ac.getAllAccounts().getBody();

		assertEquals(2, accounts.size());

		assertEquals(200L, accounts.get(0).getId());
		assertEquals(new BigDecimal("98.90"), accounts.get(0).getBalance());
		assertEquals(LocalDateTime.now().toString().substring(0, 13), accounts.get(0).getLastUpdated().toString().substring(0, 13));

		assertEquals(201L, accounts.get(1).getId());
		assertEquals(new BigDecimal("101.10"), accounts.get(1).getBalance());
		assertEquals(LocalDateTime.now().toString().substring(0, 13), accounts.get(1).getLastUpdated().toString().substring(0, 13));

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
	@Transactional(propagation = Propagation.NEVER)
	void httpRequestTest() {
		ac.clear();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		{ // Create Account 200
			MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
			map.add("id", "200");
			map.add("balance", "100");

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
			ResponseEntity<Account> response = restTemplate.postForEntity(
					  "http://localhost:" + port + "/account/new", request , Account.class);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getBody().getId()).isEqualTo(200L);
			assertThat(response.getBody().getBalance()).isEqualTo(new BigDecimal("100.00"));
		}

		{ // Create Account 201
			MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
			map.add("id", "201");
			map.add("balance", "123.45");

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
			ResponseEntity<Account> response = restTemplate.postForEntity(
					  "http://localhost:" + port + "/account/new", request , Account.class);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getBody().getId()).isEqualTo(201L);
			assertThat(response.getBody().getBalance()).isEqualTo(new BigDecimal("123.45"));
		}

		{ // Transfer
			MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
			map.add("accountFrom", "200");
			map.add("accountTo", "201");
			map.add("amount", "1.23");
			map.add("sleep", "250");

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
			ResponseEntity<String> response = restTemplate.postForEntity(
					  "http://localhost:" + port + "/account/transfer", request , String.class);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		}

		{ // Check Account 200 after transfer
			ResponseEntity<Account> response = restTemplate.getForEntity(
					  "http://localhost:" + port + "/account/200", Account.class);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(response.getBody().getId()).isEqualTo(200L);
			assertThat(response.getBody().getBalance()).isEqualTo(new BigDecimal("98.77"));
		}

		{ // Check Account 201 after transfer
			ResponseEntity<Account> response = restTemplate.getForEntity(
					  "http://localhost:" + port + "/account/201", Account.class);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(response.getBody().getId()).isEqualTo(201L);
			assertThat(response.getBody().getBalance()).isEqualTo(new BigDecimal("124.68"));
		}

		{ // Check the transactions
			ResponseEntity<Transaction[]> response = restTemplate.getForEntity(
					  "http://localhost:" + port + "/transaction/list", Transaction[].class);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			Transaction[] transactions = response.getBody();
			assertThat(transactions[0].getAccount()).isEqualTo(200L);
			assertThat(transactions[0].getAmount()).isEqualTo(new BigDecimal("-1.23"));
			assertThat(transactions[0].getRunningBalance()).isEqualTo(new BigDecimal("98.77"));
			assertThat(transactions[1].getAccount()).isEqualTo(201L);
			assertThat(transactions[1].getAmount()).isEqualTo(new BigDecimal("1.23"));
			assertThat(transactions[1].getRunningBalance()).isEqualTo(new BigDecimal("124.68"));
		}

		ac.clear();

	}

}
