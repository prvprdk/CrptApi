package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.List;

public class CrptApi {

    private final static String STR_REQUEST = "https://ismp.crpt.ru/api/v3/lk/documents/create";

    private RateLimiter rateLimiter;

    public CrptApi(int period, int requestLimit) {
        double perSecond = requestLimit / (period / 1000.0);
        rateLimiter = RateLimiter.create(perSecond);

    }


    public void create(Document document) throws IOException, InterruptedException, URISyntaxException {

        rateLimiter.acquire();

        String jsonDocument = serialization(document);

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI(STR_REQUEST))
                .header("Content-Type", "JSON/APPLICATION")
                .POST(HttpRequest.BodyPublishers.ofString(jsonDocument))
                .build();
        httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

    }

    private <T> String serialization(T t) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(t);
    }

    @AllArgsConstructor
    public class Document {

        private Description description;
        private String doc_id;
        private String doc_status;
        private final String doc_type = "LP_INTRODUCE_GOODS";
        private final Boolean importRequest = true;
        private String owner_inn;
        private String participant_inn;
        private String producer_inn;
        private Date production_date;
        private String production_type;
        private List<Product> products;
        private Date reg_date;
        private String reg_number;
    }

    @AllArgsConstructor
    public class Description {
        private String participantInn;
    }

    @AllArgsConstructor
    public class Product {
        private String certificate_document;
        private Date certificate_document_date;
        private String certificate_document_number;
        private String owner_inn;
        private String producer_inn;
        private Date production_date;
        private String tnved_code;
        private String uit_code;
        private String uitu_code;
    }

}


