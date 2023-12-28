package com.mindex.challenge.integration;

import com.mindex.challenge.data.Compensation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationEndpointIntegrationTest {

    private String compensationUrl;
    private String compensationIdUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        compensationUrl = "http://localhost:"+port+"/compensation";
        compensationIdUrl = "http://localhost:"+port+"/compensation/{id}";
    }

    @Test
    public void testCreate() {
        Compensation compensation = new Compensation("testId", 20.00, LocalDate.now());

        Compensation result = restTemplate.postForEntity(compensationUrl, compensation, Compensation.class).getBody();

        assertThat(result).isNotNull();
        assertThat(result.getEmployeeId()).isEqualTo("testId");
    }

    @Test
    public void testRead() {
        Compensation compensation = Compensation.builder().employeeId("testId").salary(20.00).effectiveDate(LocalDate.now()).build();
        Compensation compensation2 = Compensation.builder().employeeId("testId").salary(21.00).effectiveDate(LocalDate.now().minusDays(1)).build();

        restTemplate.postForEntity(compensationUrl, compensation, Compensation.class).getBody();
        restTemplate.postForEntity(compensationUrl, compensation2, Compensation.class).getBody();

        Compensation[] resultArray = restTemplate.getForEntity(compensationIdUrl, Compensation[].class, "testId").getBody();
        List<Compensation> results = Arrays.asList(resultArray);

        assertThat(results.size()).isEqualTo(2);
        //Ordered by date
        assertThat(results.get(0).getSalary()).isEqualTo(21.00);
        assertThat(results.get(1).getSalary()).isEqualTo(20.00);
    }
}
