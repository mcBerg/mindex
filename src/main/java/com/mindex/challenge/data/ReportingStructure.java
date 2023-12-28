package com.mindex.challenge.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
@AllArgsConstructor
@Builder
public class ReportingStructure {

    String employeeId;
    Integer numberOfReports;


}
