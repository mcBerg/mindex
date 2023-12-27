package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        employee.setEmployeeId(UUID.randomUUID().toString());
        LOG.debug("Creating employee [{}]", employee);

        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Finding employee with id [{}]", id); //Shouldn't this be finding or reading instead of Creating?

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public ReportingStructure getReportingStructure(String employeeId) {
        ReportingStructure reportingStructure = new ReportingStructure();

        reportingStructure.setEmployee(employeeRepository.findByEmployeeId(employeeId));
        reportingStructure.setNumberOfReports(countReports(employeeId));

        return reportingStructure;
    }

    public Integer countReports(String id) {
        LOG.debug("Counting Reports for [{}]", id);

        Employee manager = employeeRepository.findByEmployeeId(id);
        if(manager==null) {
            LOG.debug("Manager not found. Returning zero reports.");
            return 0;
        }
        return recursiveReportCount(manager);
    }

    private Integer recursiveReportCount(Employee e) {
        //TODO: A check should be implemented to prevent possible infinite loops.
        e = employeeRepository.findByEmployeeId(e.getEmployeeId());
        LOG.debug("Report Count for [{}]", e);
        if(e.getDirectReports()==null || e.getDirectReports().isEmpty()) {return 0;}
        Integer reports = e.getDirectReports().size();
        for(Employee downline : e.getDirectReports()) {
            reports += recursiveReportCount(downline);
        }
        return reports;
    }

}
