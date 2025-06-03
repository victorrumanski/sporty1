# F1 Betting Backend Application

This is a Spring Boot application that provides a REST API for a Formula 1 betting service, as per the requirements of the Home Assignment.

## Non-AI Notes


*   Uses the controller --> service --> repository pattern for data access
*   Decouples the F1 API behind a @Service so future data providers can be added
*   Uses @Async processing for closing the bets as WON or LOST
*   OpenAPI documentation
*   Global Exception Handler
*   DRY on event fetching logic
*   Uses parallel fetching for drivers market
*   Ctrl+F  "NON-AI" for more comments written by me.

## Requirements

*   Java 17 or higher
*   Maven 3.6.x or higher

## How to Run

1.  **Clone the repository:**
    ```bash
    # git clone <repository-url>
    # cd f1-betting-app
    ```

2.  **Build the project using Maven:**
    ```bash
    mvn clean install
    ```

3.  **Run the application:**
    ```bash
    java -jar target/f1-betting-app-0.0.1-SNAPSHOT.jar
    ```
    Alternatively, you can run it from your IDE by executing the `main` method in `F1BettingApplication.java`.

4.  The application will start on port `8080` (by default).

## API Documentation (OpenAPI / Swagger UI)

Once the application is running, you can access the Swagger UI to view and interact with the API documentation:

*   **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
*   **OpenAPI Spec (JSON):** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
 