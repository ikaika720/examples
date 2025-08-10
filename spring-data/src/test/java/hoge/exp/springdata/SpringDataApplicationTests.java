package hoge.exp.springdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import hoge.exp.springdata.webapp.Account;
import hoge.exp.springdata.webapp.AccountController;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
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
		String actStr = ac.getAccount(100L).getBody();

		System.out.println(actStr);
		System.out.println(LocalDateTime.now().toString().substring(0, 13));
		assertTrue(actStr.contains("id=100"));
		assertTrue(actStr.contains("balance=123.45"));
		assertTrue(actStr.contains("lastUpdated=" + LocalDateTime.now().toString().substring(0, 13)));

		ac.createAccount(101L, "678.90");
		String allStr = ac.getAllAccounts().getBody();
		String[] splittedStr = allStr.split("\r\n");

		assertEquals(2, splittedStr.length);

		assertTrue(splittedStr[0].contains("id=100"));
		assertTrue(splittedStr[0].contains("balance=123.45"));
		assertTrue(splittedStr[0].contains("lastUpdated=" + LocalDateTime.now().toString().substring(0, 13)));

		assertTrue(splittedStr[1].contains("id=101"));
		assertTrue(splittedStr[1].contains("balance=678.90"));
		assertTrue(splittedStr[1].contains("lastUpdated=" + LocalDateTime.now().toString().substring(0, 13)));

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

		String allStr = ac.getAllAccounts().getBody();
		String[] splittedStr = allStr.split("\r\n");

		assertEquals(2, splittedStr.length);

		assertTrue(splittedStr[0].contains("id=200"));
		assertTrue(splittedStr[0].contains("balance=98.90"));
		assertTrue(splittedStr[0].contains("lastUpdated=" + LocalDateTime.now().toString().substring(0, 13)));

		assertTrue(splittedStr[1].contains("id=201"));
		assertTrue(splittedStr[1].contains("balance=101.10"));
		assertTrue(splittedStr[1].contains("lastUpdated=" + LocalDateTime.now().toString().substring(0, 13)));

		String transStr = ac.getAllTransactions().getBody();
		System.out.println(transStr);
		String[] splTransStr = transStr.split("\r\n");

		assertEquals(2, splTransStr.length);

		assertTrue(splTransStr[0].contains("account=200"));
		assertTrue(splTransStr[0].contains("amount=-1.1"));
		assertTrue(splTransStr[0].contains("date=" + LocalDate.now()));
		assertTrue(splTransStr[0].contains("runningBalance=98.90"));

		assertTrue(splTransStr[1].contains("account=201"));
		assertTrue(splTransStr[1].contains("amount=1.1"));
		assertTrue(splTransStr[1].contains("date=" + LocalDate.now()));
		assertTrue(splTransStr[1].contains("runningBalance=101.10"));
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
			ResponseEntity<String> response = restTemplate.postForEntity(
					  "http://localhost:" + port + "/account/new", request , String.class);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getBody()).contains("id=200").contains("balance=100.00");
		}

		{ // Create Account 201
			MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
			map.add("id", "201");
			map.add("balance", "123.45");

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
			ResponseEntity<String> response = restTemplate.postForEntity(
					  "http://localhost:" + port + "/account/new", request , String.class);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getBody()).contains("id=201").contains("balance=123.45");
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
			ResponseEntity<String> response = restTemplate.getForEntity(
					  "http://localhost:" + port + "/account/200", String.class);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(response.getBody()).contains("id=200").contains("balance=98.77");
		}

		{ // Check Account 201 after transfer
			ResponseEntity<String> response = restTemplate.getForEntity(
					  "http://localhost:" + port + "/account/201", String.class);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(response.getBody()).contains("id=201").contains("balance=124.68");
		}

		{ // Check the transactions
			ResponseEntity<String> response = restTemplate.getForEntity(
					  "http://localhost:" + port + "/transaction/list", String.class);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

			String[] splTransStr = response.getBody().split("\r\n");
			assertThat(splTransStr[0]).contains("account=200").contains("amount=-1.23")
					.contains("runningBalance=98.77");
			assertThat(splTransStr[1]).contains("account=201").contains("amount=1.23")
					.contains("runningBalance=124.68");
		}

		ac.clear();

	}

}
