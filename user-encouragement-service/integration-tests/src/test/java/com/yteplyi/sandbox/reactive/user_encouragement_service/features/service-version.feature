Feature: UES Test

  Background:
    * url host

  Scenario:
    Given path '/api/rest/v1/version'
    When method GET
    * print response
    Then status 200
    * match $.data == { version:'0.1.0' }