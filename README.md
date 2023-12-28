## What Was Added

### Lombok
#### @Builder
Makes creating objects easier, especially in testing.
#### @Constructors
The RequiredArgsConstructor does all the work to set up dependency injection and avoid the field injection that is frowned upon. 
#### @Getters/Setters/ToString/Data
https://projectlombok.org/features/Data
Reduces boilerplate, and includes some handy features like the ToString and Equals.
### AssertThat
AssertThat(x).isEqualTo(y); reads nicer imo, but more importantly includes some fancy comparisons as seen in the refactored 
version of the equivalent employee comparison. 
### Coverage
Test coverage was improved to cover all happy paths
### ToDos
Some ToDos were added for out of scope considerations like the potential for direct reports to create a loop.

## What Was Refactored
### AutoWired 
Field Injection
https://stackoverflow.com/questions/39890849/what-exactly-is-field-injection-and-how-to-avoid-it
### @Slf4j
Replaces the logging boilerplate
### Testing
Section 3.7 discusses breaking tests into specific scenarios.
Section 3.12 suggests 80% coverage or better
https://www.baeldung.com/java-unit-testing-best-practices
Test classes were split between Integration and Unit tests
Individual tests were split to test single methods

## Thanks!
Thank you for providing a challenge that runs out of the box. Making small changes and seeing if it still works is much better than having several changes to make before it works.

I appreciate your time and consideration.
# Coding Challenge
## What's Provided
A simple [Spring Boot](https://projects.spring.io/spring-boot/) web application has been created and bootstrapped 
with data. The application contains information about all employees at a company. On application start-up, an in-memory 
Mongo database is bootstrapped with a serialized snapshot of the database. While the application runs, the data may be
accessed and mutated in the database without impacting the snapshot.

### How to Run
The application may be executed by running `gradlew bootRun`.

### How to Use
The following endpoints are available to use:
```
* CREATE
    * HTTP Method: POST 
    * URL: localhost:8080/employee
    * PAYLOAD: Employee
    * RESPONSE: Employee
* READ
    * HTTP Method: GET 
    * URL: localhost:8080/employee/{id}
    * RESPONSE: Employee
* UPDATE
    * HTTP Method: PUT 
    * URL: localhost:8080/employee/{id}
    * PAYLOAD: Employee
    * RESPONSE: Employee
```
The Employee has a JSON schema of:
```json
{
  "type":"Employee",
  "properties": {
    "employeeId": {
      "type": "string"
    },
    "firstName": {
      "type": "string"
    },
    "lastName": {
          "type": "string"
    },
    "position": {
          "type": "string"
    },
    "department": {
          "type": "string"
    },
    "directReports": {
      "type": "array",
      "items" : "string"
    }
  }
}
```
For all endpoints that require an "id" in the URL, this is the "employeeId" field.

## What to Implement
Clone or download the repository, do not fork it.

### Task 1
Create a new type, ReportingStructure, that has two properties: employee and numberOfReports.

For the field "numberOfReports", this should equal the total number of reports under a given employee. The number of 
reports is determined to be the number of directReports for an employee and all of their distinct reports. For example, 
given the following employee structure:
```
                    John Lennon
                /               \
         Paul McCartney         Ringo Starr
                               /        \
                          Pete Best     George Harrison
```
The numberOfReports for employee John Lennon (employeeId: 16a596ae-edd3-4847-99fe-c4518e82c86f) would be equal to 4. 

This new type should have a new REST endpoint created for it. This new endpoint should accept an employeeId and return 
the fully filled out ReportingStructure for the specified employeeId. The values should be computed on the fly and will 
not be persisted.

### Task 2
Create a new type, Compensation. A Compensation has the following fields: employee, salary, and effectiveDate. Create 
two new Compensation REST endpoints. One to create and one to read by employeeId. These should persist and query the 
Compensation from the persistence layer.

## Delivery
Please upload your results to a publicly accessible Git repo. Free ones are provided by Github and Bitbucket.
