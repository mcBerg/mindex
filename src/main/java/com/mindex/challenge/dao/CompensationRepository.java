package com.mindex.challenge.dao;

import com.mindex.challenge.data.Compensation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompensationRepository extends MongoRepository<Compensation, String> {

    //Ordering was not asked, but in a real world scenario I would expect date order to be the most useful
    List<Compensation> findCompensationsByEmployeeIdOrderByEffectiveDate(String employeeId);
}
