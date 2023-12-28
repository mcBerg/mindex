package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompensationServiceImpl implements CompensationService {

    private final CompensationRepository compensationRepository;

    @Override
    public List<Compensation> read(String employeeId) {
        log.debug("Received read request for id [{}]", employeeId);
        return compensationRepository.findCompensationsByEmployeeIdOrderByEffectiveDate(employeeId);
    }

    @Override
    public Compensation create(Compensation newCompensation) {
        log.debug("Received create request for [{}]", newCompensation);
        return compensationRepository.insert(newCompensation);
    }
}
