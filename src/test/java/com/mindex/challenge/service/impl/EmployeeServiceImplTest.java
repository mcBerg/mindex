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
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;

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
    }

    //Split these up, each test should only test one thing.
    @Test
    public void testCreate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        assert createdEmployee != null;
        assertNotNull(createdEmployee.getEmployeeId());
        //Test the employee returned is equivalent to the employee sent
        assertEmployeeEquivalence(testEmployee, createdEmployee);

    }
    @Test
    public void testRead() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        assert createdEmployee != null;

        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assert readEmployee != null;

        //Test the employee retrieved from the DB is equivalent to the employee created.
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);
    }

    @Test
    public void testUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        assert createdEmployee != null;
        assertNotNull(createdEmployee.getEmployeeId());
        Employee createdEmployee2 = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        assert createdEmployee2 != null;
        assertNotNull(createdEmployee2.getEmployeeId());

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
        assert updatedEmployee != null;
        assertEmployeeEquivalence(createdEmployee, updatedEmployee);

        //Check the employee retrieved from the db
        Employee updatedWithReports =
                restTemplate.getForEntity(employeeIdUrl,
                        Employee.class,
                        updatedEmployee.getEmployeeId())
                        .getBody();

        assert updatedWithReports != null;
        assertFalse(updatedWithReports.getDirectReports().isEmpty());
        assertEmployeeEquivalence(updatedEmployee, updatedWithReports);
    }

    @Test
    public void testCountReports() {
        String ceoId = setupCountData();

        assertEquals(4, (int) employeeService.countReports(ceoId));

    }

    @Test
    public void testReportingStructure() {
        String ceoId = setupCountData();

        ReportingStructure result = employeeService.getReportingStructure(ceoId);

        assertEquals(ceoId, result.getEmployee().getEmployeeId());
        assertEquals(4, result.getNumberOfReports().intValue());

    }

    private String setupCountData() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee employee = new Employee();
        employee.setFirstName("John");
        employee.setLastName("Lennon");
        employee.setDepartment("Executive");
        employee.setPosition("CEO");

        Employee ceoEmployee = restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody();
        assert ceoEmployee != null;
        String ceoId = ceoEmployee.getEmployeeId();

        employee = new Employee();
        employee.setFirstName("Paul");
        employee.setLastName("McCartney");
        employee.setDepartment("Sound");
        employee.setPosition("testPosition");
        Employee paulEmployee = restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody();
        assert ceoId != paulEmployee.getEmployeeId();
        employee = new Employee();
        employee.setFirstName("Ringo");
        employee.setLastName("Starr");
        employee.setDepartment("StarrDepartment");
        employee.setPosition("testPosition");
        Employee ringoEmployee = restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody();

        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(paulEmployee);
        employeeList.add(ringoEmployee);
        ceoEmployee.setDirectReports(employeeList);
        restTemplate.exchange(employeeIdUrl,
                HttpMethod.PUT,
                new HttpEntity<Employee>(ceoEmployee, headers),
                Employee.class,
                ceoEmployee.getEmployeeId());

        employee = new Employee();
        employee.setFirstName("Pete");
        employee.setLastName("Best");
        employee.setDepartment("testDepartment");
        employee.setPosition("testBest");
        employeeList.clear();
        employeeList.add(restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody());

        employee = new Employee();
        employee.setFirstName("George");
        employee.setLastName("Harrison");
        employee.setDepartment("testDepartment");
        employee.setPosition("testPosition");
        employeeList.add(restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody());

        assert ringoEmployee != null;
        ringoEmployee.setDirectReports(employeeList);
        restTemplate.exchange(employeeIdUrl,
                HttpMethod.PUT,
                new HttpEntity<Employee>(ringoEmployee, headers),
                Employee.class,
                ringoEmployee.getEmployeeId());

        return ceoId;

    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}
