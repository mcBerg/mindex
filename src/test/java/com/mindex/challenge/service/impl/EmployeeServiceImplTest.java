package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String employeeReportUrl;

    @Autowired
    private EmployeeService employeeService;

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

    //Split these up, each test should only test one thing.
    @Test
    public void testCreate() {
        Employee testEmployee = Employee.builder().firstName("John").lastName("Doe").department("Engineering").position("Developer").build();

        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        assertThat(createdEmployee).isNotNull();
        assertThat(createdEmployee.getEmployeeId()).isNotNull();
        assertThat(createdEmployee.getDirectReports()).isNotNull();
        //Test the employee returned is equivalent to the employee sent
        assertEmployeeEquivalence(testEmployee, createdEmployee);

    }
    @Test
    public void testRead() {
        Employee testEmployee = Employee.builder().build();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        assertThat(testEmployee.getDirectReports()).isNotNull();

        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        assertThat(createdEmployee).isNotNull();

        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertThat(readEmployee).isNotNull();

        //Test the employee retrieved from the DB is equivalent to the employee created.
        assertThat(createdEmployee.getEmployeeId()).isEqualTo(readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);
    }

    @Test
    public void testUpdate() {
        Employee testEmployee = Employee.builder().build();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

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
        assertEmployeeEquivalence(createdEmployee, updatedEmployee);

        //Check the employee retrieved from the db
        Employee updatedWithReports =
                restTemplate.getForEntity(employeeIdUrl,
                        Employee.class,
                        updatedEmployee.getEmployeeId())
                        .getBody();

        assertThat(updatedWithReports).isNotNull();
        assertThat(updatedWithReports.getDirectReports()).isNotEmpty();
        assertEmployeeEquivalence(updatedEmployee, updatedWithReports);
    }

    @Test
    public void testCountReports() {
        String ceoId = setupCountData();

        assertThat(4).isEqualTo(employeeService.countReports(ceoId));

    }

    @Test
    public void testReportingStructure() {
        String ceoId = setupCountData();

        ReportingStructure result = employeeService.getReportingStructure(ceoId);

        assertThat(ceoId).isEqualTo(result.getEmployeeId());
        assertThat(4).isEqualTo(result.getNumberOfReports().intValue());

    }

    @Test
    public void testReportCountEndpoint() {
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
        employee = Employee.builder()
                .firstName("Ringo")
                .lastName("Starr")
                .department("StarrDepartment")
                .position("testPosition")
                .build();
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
        assertThat(expected).isEqualToIgnoringGivenFields(actual, "directReports", "employeeId");
        assertThat(expected.getDirectReports().size()).isEqualTo(actual.getDirectReports().size());
    }
}
