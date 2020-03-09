<p align="center">
  <a href="https://travis-ci.com/quickcougar/smart_fridge"><img src="https://api.travis-ci.com/quickcougar/smart_fridge.svg?branch=master"></a>
</p>

Smart Fridge
========

This is a sample microservice for tracking refrigerators.  It is an exercise to demonstrating use of [Ratpack](https://ratpack.io/) in a Java 11 application.

Development
-----------

Requirements:

* Java 11
* Gradle 6.2.1

`gradle build` will build and test the entire application

Known Issues / TODO
-----------

* Add a validator to prevent adding or updating a fridge with more than 12 cans.
* Add functional and unit tests and code coverage reports
* Activate and configure OAuth (ratpack-bearer-auth)
* When H2 connection pooling hits maximum, no scaling occurs resulting in failure
* Add health check and performance endpoints
