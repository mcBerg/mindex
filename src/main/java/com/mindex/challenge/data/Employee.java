package com.mindex.challenge.data;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection="employee")
@Builder
@Getter
@Setter
@AllArgsConstructor
public class Employee {
    @Id
    private String employeeId;
    private String firstName;
    private String lastName;
    private String position;
    private String department;
    @Builder.Default
    private List<Employee> directReports = new ArrayList<>();

    @Override
    public String toString() {
        return this.employeeId+": "+this.firstName+" "+this.lastName+" "+this.getDirectReports().size()+" direct reports.";
    }
}
