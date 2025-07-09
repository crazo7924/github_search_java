# GitHub Repository Search Service

This is a Spring Boot application that provides a RESTful API to search for repositories on GitHub. It fetches the results using the official GitHub API and persists them into a PostgreSQL database.

## Features

-   **Search Repositories**: Search for GitHub repositories by a query string.
-   **Filter and Sort**: Filter search results by programming language and sort them by criteria like stars, forks, etc.
-   **Database Persistence**: Automatically saves the results of every successful search to a PostgreSQL database.
-   **Retrieve Saved Results**: An endpoint to view all repositories that have been saved to the database.
-   **Robust and Testable**: Built with a clean service layer and includes a comprehensive suite of unit tests.

## Technologies Used

-   **Framework**: Spring Boot 3
-   **Language**: Java 17
-   **Web**: Spring Web (for REST APIs)
-   **Database**: Spring Data JPA (Hibernate) with PostgreSQL
-   **Build Tool**: Gradle
-   **Testing**: JUnit 5 & Mockito

## Prerequisites

Before you begin, ensure you have the following installed:
-   JDK 17 or newer
-   Gradle 7.x or newer
-   A running instance of PostgreSQL

## Setup and Installation

Follow these steps to get the application running on your local machine.

### 1. Clone the Repository
