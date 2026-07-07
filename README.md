# External APIs Demo App

A Spring Boot application that demonstrates using **RestClient** and **WebClient** to communicate with an external Book API.

## What I implemented

### RestClient
- Fetches a single book
- Fetches all books
- Maps `BookApiResponse` to `BookDto`
- Handles client, server and connection errors using `ClientException`

### WebClient
- Fetches a single book asynchronously
- Fetches all books asynchronously
- Fetches two books in parallel using `Mono.zip()`
- Handles errors using `onErrorMap()`

## Testing

I created IntelliJ HTTP Client files for testing the application:

- `restclient-tests.http`
- `webclient-tests.http`
- `parallel-tests.http`

The tests include:
- Successful requests
- Error scenarios
- Chaos book IDs (991–999)

## Running the application

Run `ExternalApisBootcampDemoApplication`.

The application starts on:

```
http://localhost:8080
```

The external Book API is already configured in `application.yaml`, so no additional setup is required.

## Technologies

- Java 21
- Spring Boot
- RestClient
- WebClient
- Reactor (`Mono`, `Flux`)
- Maven
