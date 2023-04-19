# PetShopRestAssured
PetShotRestAssured is a learning pet project.

The project aim is to learn main functionality of RestAssured.

The project using API: https://petstore.swagger.io/

Base URL: https://petstore.swagger.io/v2

## Functionality

### Features review

    1. Using RestAssured
    2. Using Jackson API for work with json
    3. Using POJO
    4. Using Allure Report for reporting

Repository contains Actions for CI flows:

1. Build and run tests

2. Allure reports on the [GitHub Pages](https://taras-kr.github.io/PetStoreRestAssured/)
    
## Installation

### Prerequisites

Before installing the whole project you have to install the following:

1. [Java SDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
2. [Maven](https://maven.apache.org/download.cgi)
3. [IntelliJ IDEA](https://www.jetbrains.com/idea/download/#section=windows)
4. [Allure Framework](https://docs.qameta.io/allure-report/#_get_started)

### Verify you installed the correct version

In the IDEA terminal type:
<li>
<code> java -version</code> to get installed java version
</li>
<li>
<code> mvn -version</code>  to get installed maven version
</li>
<li>
<code> allure --version</code>  to get installed Allure Framework version
</li>

### Installation
1. Copy code from GitHub repository
2. In the IntelliJ IDEA create New project from Version Control

## Running tests

1. Run a single test class in the IDEA terminal type:

<code>mvn test -Dtest=ClassNameTest</code>

2. Run single test method:

<code>mvn test -Dtest=ClassNameTest#methodName</code>

3. Run test using XML suite file
<li>Run default suite file

<code>mvn test</code></li>
<li>Run specified suite file</li>
<code> mvn test -DsuiteXml="test_suite_name.xml"</code>

## View reports
In the IDEA terminal type:

<code> mvn allure:serve</code>

