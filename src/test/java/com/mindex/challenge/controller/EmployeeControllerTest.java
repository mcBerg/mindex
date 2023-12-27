package com.mindex.challenge.controller;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeControllerTest {
    //Units test for controller, basic tests to prove the method calls the service.

    @Mock
    private EmployeeService mockEmployeeService = Mockito.mock(EmployeeService.class);

    @InjectMocks
    private EmployeeController employeeController;

    @Before
    public void setup() {
    }

    @Test
    public void testCreate() {
        when(mockEmployeeService.create(any(Employee.class))).thenReturn(new Employee());
        Employee result = employeeController.create(new Employee());
        verify(mockEmployeeService).create(any(Employee.class));

        assertThat(result).isNotNull();
    }

    @Test
    public void testReport() {
        ReportingStructure resultReport = new ReportingStructure();
        resultReport.setNumberOfReports(11);
        when(mockEmployeeService.getReportingStructure(anyString())).thenReturn(resultReport);
        ReportingStructure result = employeeController.getReportingStructure("testId");
        verify(mockEmployeeService, times(1)).getReportingStructure(anyString());

        assertThat(result.getNumberOfReports()).isEqualTo(11);
    }
}