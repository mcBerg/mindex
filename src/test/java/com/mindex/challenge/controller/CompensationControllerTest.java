package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompensationControllerTest {

    @Mock
    private CompensationService mockCompensationService;

    @InjectMocks
    private CompensationController compensationController;

    @Before
    public void setUp() {

    }

    @Test
    public void read() {
        List<Compensation> compensationList = new ArrayList<>();
        compensationList.add(new Compensation("testId", 20.00, LocalDate.of(2023, 1,1)));
        when(mockCompensationService.read(anyString())).thenReturn(compensationList);

        List<Compensation> results = compensationController.read("test");

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).getEmployeeId()).isEqualTo("testId");
    }

    @Test
    public void create() {
        Compensation newCompensation = new Compensation("testId", 20.00, LocalDate.now());
        when(mockCompensationService.create(newCompensation)).thenReturn(newCompensation);

        Compensation result = compensationController.create(newCompensation);

        assertThat(result.getEmployeeId()).isEqualTo("testId");
    }
}