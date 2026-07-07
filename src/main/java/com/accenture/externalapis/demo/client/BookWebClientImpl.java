package com.accenture.externalapis.demo.client;

import com.accenture.externalapis.demo.config.ExternalServiceProperties;
import com.accenture.externalapis.demo.dto.BookApiResponse;
import com.accenture.externalapis.demo.dto.BookDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Component
public class BookWebClientImpl implements BookWebClient {

    private final WebClient webClient;

    public BookWebClientImpl(WebClient.Builder builder, ExternalServiceProperties properties) {
        this.webClient = builder
                .baseUrl(properties.baseUrl())
                .build();
        // Optional/bonus: this service doesn't require auth, but in a real API you would
        // often also add builder.defaultHeader("Authorization", "Bearer " + token) here.
    }

    private BookDto mapToDto(BookApiResponse response) {
        return new BookDto(
                response.title(),
                response.author(),
                response.genre(),
                response.price()
        );
    }

    @Override
    public Mono<BookDto> getBookAsync(Long id) {

        return webClient.get()
                .uri("/books/{id}", id)
                .retrieve()
                .bodyToMono(BookApiResponse.class)
                .map(this::mapToDto)
                .onErrorMap(WebClientResponseException.class,
                        ex ->
                                new ClientException(
                                        "External book service returned error: " + ex.getStatusCode(), ex))
                .onErrorMap(WebClientRequestException.class,
                        ex -> new ClientException(
                                "Could not connect to external book service.", ex));

    }

    @Override
    public Flux<BookDto> getAllBooksAsync() {
        return webClient.get()
                .uri("/books")
                .retrieve()
                .bodyToFlux(BookApiResponse.class)
                .map(this::mapToDto)
                .onErrorMap(WebClientResponseException.class,
                        ex ->
                                new ClientException(
                                        "External book service returned error: " + ex.getStatusCode(), ex))
                .onErrorMap(WebClientRequestException.class,
                        ex -> new ClientException(
                                "Could not connect to external book service.", ex));
    }

    @Override
    public Mono<List<BookDto>> getBooksInParallel(Long id1, Long id2) {
        Mono<BookDto> book1 = getBookAsync(id1);
        Mono<BookDto> book2 = getBookAsync(id2);
        return Mono.zip(book1, book2)
                .map(tuple -> List.of(
                        tuple.getT1(),
                        tuple.getT2()
                ));
    }

}
