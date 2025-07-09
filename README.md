# GitHub Repository Search Service

This is a Spring Boot application that provides a RESTful API to search for repositories on GitHub. It fetches the
results using the official GitHub API and persists them into a PostgreSQL database.

## Features

- **Search Repositories**: Search for GitHub repositories by a query string.
- **Filter and Sort**: Filter search results by programming language and sort them by criteria like stars, forks, etc.
- **Database Persistence**: Automatically saves the results of every successful search to a PostgreSQL database.
- **Retrieve Saved Results**: An endpoint to view all repositories that have been saved to the database.
- **Robust and Testable**: Built with a clean service layer and includes a comprehensive suite of unit tests.

## Technologies Used

- **Framework**: Spring Boot 3
- **Language**: Java 17
- **Web**: Spring Web (for REST APIs)
- **Database**: Spring Data JPA (Hibernate) with PostgreSQL
- **Build Tool**: Gradle
- **Testing**: JUnit 5 & Mockito

## Prerequisites

Before you begin, ensure you have the following installed:

- JDK 17 or newer
- Gradle 7.x or newer
- A running instance of PostgreSQL

## Setup and Installation

Follow these steps to get the application running on your local machine.

### 1. Clone the Repository

This is pretty obvious.

### 2. Configure the Database

You need to create a database and a user for the application.
The application uses the credentials found in src/main/resources/application.properties.
If your database setup is different, please update it accordingly.

### 3. Configure GitHub API Token (Optional but Recommended)

The application can run without an API token, but you will be subject to a very strict rate limit from GitHub. It is
highly recommended to generate a Personal Access Token and add it to the `application.properties`

## Running the Application

You can run the application using the Gradle wrapper included in the project.
```shell
./gradlew :bootRun
````
The server will start on http://localhost:8080.

## API Endpoints

The application exposes the following REST endpoints.

### 1. Search for Repositories

- **URL**: `/api/github/search`
- **Method**: `POST`
- **Description**: Searches for repositories on GitHub based on the provided criteria and saves the results to the
  database.
- **Request Body**:

| Field      | Type   | Description                                                               | Required |
|:-----------|:-------|:--------------------------------------------------------------------------|:---------|
| `query`    | String | The search keyword(s).                                                    | Yes      |
| `language` | String | The programming language to filter by.                                    | No       |
| `sort`     | String | The sort field (e.g., `stars`, `forks`, `help-wanted-issues`, `updated`). | No       |

- **Example `curl` Request**:
  ```shell
  curl http://localhost:8080/api/github/search --json '{"query": "spring", "language": "java", "sort": "stars"}'
  ```

### 2. Get All Saved Repositories

- **URL**: `/api/search/saved`
- **Method**: `GET`
- **Description**: Retrieves all repository records that have been saved to the local PostgreSQL database from previous
  searches.
- **Example `curl` Request**:
  ```shell
  curl http://localhost:8080/api/github/saved
  ```

## Running the Tests

To run the comprehensive suite of unit tests, use the following Gradle command:
```shell
./gradlew :test
```
This will execute all tests in the src/test directory and generate a report in build/reports/tests/test/index.html.