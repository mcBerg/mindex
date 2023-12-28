package com.mindex.challenge.integration;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeEndpointIntegrationTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String employeeReportUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        employeeReportUrl = "http://localhost:" + port + "/employee/reportCount/{id}";
    }

    @Test
    public void testCreate() {
        //Demo of Builder from lombok
        Employee testEmployee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .department("Engineering")
                .position("Developer")
                .build();
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        assertThat(createdEmployee).isNotNull();
        assertThat(createdEmployee.getEmployeeId()).isNotNull();
        //Test the employee returned is equivalent to the employee sent
        assertEmployeeEquivalence(testEmployee, createdEmployee);

    }
    @Test
    public void testRead() {
        Employee testEmployee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .department("Engineering")
                .position("Developer")
                .build();

        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        assert createdEmployee != null;

        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assert readEmployee != null;

        //Test the employee retrieved from the DB is equivalent to the employee created.
        assertThat(createdEmployee.getEmployeeId()).isEqualTo(readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);
    }

    @Test
    public void testUpdate() {
        Employee testEmployee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .department("Engineering")
                .position("Developer")
                .build();

        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        assertThat(createdEmployee).isNotNull();
        assertThat(createdEmployee.getEmployeeId()).isNotNull();
        Employee createdEmployee2 = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        assertThat(createdEmployee2).isNotNull();
        assertThat(createdEmployee2.getEmployeeId()).isNotNull();

        // Update checks
        createdEmployee.setPosition("Development Manager");
        createdEmployee.getDirectReports().add(createdEmployee2);
        //TODO: What do we do if someone has a direct report that is not in the DB?

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<>(createdEmployee, headers),
                        Employee.class,
                        createdEmployee.getEmployeeId()).getBody();

        //Check the employee returned by the method
        assertThat(updatedEmployee).isNotNull();
        assertThat(updatedEmployee.getDirectReports().size()).isEqualTo(1);
        //TODO: the employees aren't the same object!
        assertEmployeeEquivalence(createdEmployee, updatedEmployee);

        //Check the employee retrieved from the db
        Employee updatedWithReports =
                restTemplate.getForEntity(employeeIdUrl,
                        Employee.class,
                        updatedEmployee.getEmployeeId())
                        .getBody();

        assertThat(updatedWithReports).isNotNull();
        assertThat(updatedWithReports.getDirectReports().size()).isEqualTo(1);
        assertEmployeeEquivalence(updatedEmployee, updatedWithReports);
    }

    @Test
    public void testReportCount() {
        String ceoId = setupCountData();

        ReportingStructure result = restTemplate.getForEntity(employeeReportUrl,
                        ReportingStructure.class,
                        ceoId)
                .getBody();

        assertThat(result).isNotNull();
        assertThat(ceoId).isEqualTo(result.getEmployeeId());
        assertThat(4).isEqualTo(result.getNumberOfReports().intValue());
    }

    private String setupCountData() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee employee = Employee.builder()
                .firstName("John")
                .lastName("Lennon")
                .department("Executive")
                .position("CEO")
                .build();

        Employee ceoEmployee = restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody();
        assertThat(ceoEmployee).isNotNull();
        String ceoId = ceoEmployee.getEmployeeId();

        employee = Employee.builder()
                .firstName("Paul")
                .lastName("McCartney")
                .department("Sound")
                .position("testPosition")
                .build();
        Employee paulEmployee = restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody();
        assertThat(paulEmployee).isNotNull();
        assertThat(ceoId).isNotEqualTo(paulEmployee.getEmployeeId());
        employee = new Employee(null, "Ringo", "Starr", "StarrDepartment", "testPosition", (List<Employee>)Collections.EMPTY_LIST);
        Employee ringoEmployee = restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody();

        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(paulEmployee);
        employeeList.add(ringoEmployee);
        ceoEmployee.setDirectReports(employeeList);
        restTemplate.exchange(employeeIdUrl,
                HttpMethod.PUT,
                new HttpEntity<>(ceoEmployee, headers),
                Employee.class,
                ceoEmployee.getEmployeeId());

        employee = Employee.builder()
                .firstName("Pete")
                .lastName("Best")
                .department("testDepartment")
                .position("testBest")
                .build();

        employeeList.clear();
        employeeList.add(restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody());

        employee = Employee.builder()
                .firstName("George")
                .lastName("Harrison")
                .department("testDepartment")
                .position("testPosition")
                .build();
        employeeList.add(restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody());

        assertThat(ringoEmployee).isNotNull();
        ringoEmployee.setDirectReports(employeeList);
        restTemplate.exchange(employeeIdUrl,
                HttpMethod.PUT,
                new HttpEntity<>(ringoEmployee, headers),
                Employee.class,
                ringoEmployee.getEmployeeId());

        return ceoId;

    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertThat(expected).isEqualToIgnoringGivenFields(actual, "employeeId", "directReports");
        assertThat(expected.getDirectReports().size()).isEqualTo(actual.getDirectReports().size());
    }
}
