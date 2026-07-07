package com.accenture.externalapis.demo.client;

import com.accenture.externalapis.demo.config.ExternalServiceProperties;
import com.accenture.externalapis.demo.dto.BookApiResponse;
import com.accenture.externalapis.demo.dto.BookDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Component
public class BookRestClientImpl implements BookRestClient {

    private final RestClient restClient;

    public BookRestClientImpl(RestClient.Builder builder, ExternalServiceProperties properties) {
        this.restClient = builder
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
    public BookDto getBook(Long id) {
        try {
            BookApiResponse response = restClient.get()
                    .uri("/books/{id}", id)
                    .retrieve()
                    .body(BookApiResponse.class);
            if (response == null) {
                throw new ClientException("External service returned an empty response.");
            }

            return mapToDto(response);
        } catch (HttpClientErrorException ex) {
            throw new ClientException("External book service returned client error: " + ex.getStatusCode(), ex);
        } catch (HttpServerErrorException ex) {
            throw new ClientException("External book service returned server error: " + ex.getStatusCode(), ex);
        } catch (ResourceAccessException ex) {
            throw new ClientException("Could not connect to external book service.", ex);
        }
    }

    @Override
    public List<BookDto> getAllBooks() {
        try {
            BookApiResponse[] responses = restClient.get()
                    .uri("/books")
                    .retrieve()
                    .body(BookApiResponse[].class);
            if (responses == null) {
                throw new ClientException("External service returned an empty response.");
            }
            return Arrays.stream(responses)
                    .map(this::mapToDto)
                    .toList();
        } catch (HttpClientErrorException ex) {
            throw new ClientException("External book service returned client error: " + ex.getStatusCode(), ex);
        } catch (HttpServerErrorException ex) {
            throw new ClientException("External book service returned server error: " + ex.getStatusCode(), ex);
        } catch (ResourceAccessException ex) {
            throw new ClientException("Could not connect to external book service.", ex);
        }

    }


}
