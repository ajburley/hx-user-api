## User API

A user API provided as a submission to the technical assessment as part of the Holiday Extras recruitment process.

### Quick start

* You will need JDK 11 or above, and a recent version of Apache Maven (version 3.6.3 was used for testing)
* As per the assessment brief, the data must be held for the lifetime of the assessment; due to this, the data is held on disk - this file is stored within the user's home directory where the application should usually have write permissions, but depending on your OS configuration you may need to set this up
* The application can be build with standard maven commands (e.g. within your IDE of choice, or using the `mvn` command line tool)
* To run the application after compiling, run the main class `com.holidayextras.techtest.ajb.main.Application`. The application will start on port 8080.
* Once the application has started, navigate to *http://localhost:8080/test-harness/* to execute test requests (note that you will need to click one of the page's refresh buttons after any database change to see the new data)
  * (You could also use a tool such as Postman; responses are in JSON format)
* Execute test class `UserTestsIT` to run 37 integration tests and verify that the API is working correctly. The server must already be running on port 8080 for the test to connect to

### Notes

* The application stack uses **Spring Boot** as a web framework and for dependency injection, **MyBatis** as a database framework, **SQLite** for the database itself, **JUnit** and **Hamcrest** for testing, **JSON-lib** for JSON verification in tests, and **jQuery** and **Bootstrap** in the test harness
* The application is structured around two primary layers - controller and database. Typically, an application like this would also have a business layer which sits between the controller and database layers, but there were no requirements within the assessment brief which needed business logic, so in this API the controller calls directly through to the database layer
* The provided test harness is intended for testing purposes only and is not meant to be a full client. In particular, while the API does support internationalization of error messages by returning an error code together with an erroneous value (if applicable), the test harness does not implement an error message lookup and instead displays the returned (sanitised) error message, together with the error code and erroneous value if present.
* Validation is performed for input data, for example email validation
* Usually, an application should have a balance between unit testing and integration testing as per the so-called "test pyramid". However, in the case of this application, almost all the code is specific to the configuration of the database, or to the mechanism of returning data to the client. Therefore, in lieu of specific unit tests, a comprehensive integration test suite has been provided.
