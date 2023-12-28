package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        employee.setEmployeeId(UUID.randomUUID().toString());
        log.debug("Creating employee [{}]", employee);

        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        log.debug("Finding employee with id [{}]", id); //Shouldn't this be finding or reading instead of Creating?

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        log.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public ReportingStructure getReportingStructure(String employeeId) {
        return new ReportingStructure(employeeId, countReports(employeeId));
    }

    public Integer countReports(String id) {
        log.debug("Counting Reports for [{}]", id);

        Employee manager = employeeRepository.findByEmployeeId(id);
        if(manager==null) {
            log.debug("Manager not found. Returning zero reports.");
            return 0;
        }
        return recursiveReportCount(manager);
    }

    private Integer recursiveReportCount(Employee e) {
        //TODO: A check should be implemented to prevent possible infinite loops.
        e = employeeRepository.findByEmployeeId(e.getEmployeeId());
        log.debug("Report Count for [{}]", e);
        if(e.getDirectReports()==null || e.getDirectReports().isEmpty()) {return 0;}
        Integer reports = e.getDirectReports().size();
        for(Employee downline : e.getDirectReports()) {
            reports += recursiveReportCount(downline);
        }
        return reports;
    }

}
