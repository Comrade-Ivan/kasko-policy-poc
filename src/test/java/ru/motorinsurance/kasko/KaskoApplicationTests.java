package ru.motorinsurance.kasko;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Slf4j
class KaskoApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	private final String API_V1 = "/api/v1";

	@Test
	void contextLoads() {
	}

	@Test
	@Transactional
	@DirtiesContext
	void createPolicy_ShouldCreatePreCalculationOnValidRequest() throws Exception {
		String requestBody = """
				{
				  "vehicle": {
				    "vin": "XTA21099765432102",
				    "mileage": 45000,
				    "actualValue": 1250000,
				    "purchaseDate": "15.05.2022",
				    "usagePurpose": "Личное использование",
				    "registrationNumber": "А123БВ777"
				  },
				  "policyHolder": {
				    "type": "Физ.Лицо",
				    "name": "Иванов Иван Иванович",
				    "contact": {
				      "phone": "+79161234567",
				      "email": "ivanov@example.com"
				    }
				  },
				  "drivers": "{\\"type\\":\\"Список\\",\\"drivers\\":[{\\"fullName\\":\\"Иванов Иван Иванович\\",\\"experience\\":10,\\"age\\":35}]}"
				}
				""";
		MvcResult result = mockMvc.perform(post(API_V1 + "/policies/create")
						.header("Authorization", basicAuthHeader())
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)).andReturn();

		if (result.getResolvedException() != null) {
			log.error("Exception: " + result.getResolvedException().getMessage());
			result.getResolvedException().printStackTrace();
		}

		log.debug("Response " + result.getResponse().getContentAsString());
	}

	private String basicAuthHeader() {
		String auth = "admin:admin123";
		return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
	}

}
