package com.mindex.challenge.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document(collection="compensation")
@Builder
@Data
@AllArgsConstructor
public class Compensation {

    String employeeId;
    Double salary;
    LocalDate effectiveDate;

}
