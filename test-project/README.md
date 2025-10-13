# Test Release Project

This is a sample Maven project used for testing the maven-release-action.

## Features

* Simple Calculator class with basic arithmetic operations
* Comprehensive JUnit 5 tests
* JaCoCo code coverage reporting
* Maven Site documentation generation
* Ready for Maven Central deployment

## Building

```bash
# Compile
mvn clean compile

# Run tests
mvn clean test

# Run tests with coverage
mvn clean verify

# Build without tests
mvn clean install -DskipTests

# Generate site documentation
mvn site
```

## Test Coverage

The project includes JaCoCo for code coverage. After running `mvn verify`, you can find the coverage report at:

```
target/jacoco-results/index.html
```

## Project Structure

```
test-project/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── example/
    │               └── Calculator.java
    └── test/
        └── java/
            └── com/
                └── example/
                    └── CalculatorTest.java
```

## Usage

This project is meant to be used with the maven-release-action. See the main README for more information.

