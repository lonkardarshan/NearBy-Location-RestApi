package com.darshan.NearByLoction.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class ShopService {

    private static final String OVERPASS_URL = "https://overpass-api.de/api/interpreter";
    private final WebClient webClient;

    public ShopService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(OVERPASS_URL).build();
    }

    public Mono<String> getNearbyShops(double latitude, double longitude) {
        String overpassQuery = String.format(
                "[out:json];node(around:5000,%f,%f)[shop=supermarket];out;",
                latitude, longitude
        );

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("data", overpassQuery).build())
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> parseResponse(response));
    }

    private String parseResponse(String response) {
        JSONObject json = new JSONObject(response);
        JSONArray elements = json.getJSONArray("elements");
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < elements.length(); i++) {
            JSONObject element = elements.getJSONObject(i);
            JSONObject tags = element.optJSONObject("tags");
            String name = tags != null ? tags.optString("name", "Unknown") : "Unknown";
            String city = tags != null ? tags.optString("addr:city", "Unknown City") : "Unknown City";
            String area = tags != null ? tags.optString("addr:suburb", "Unknown Area") : "Unknown Area";
            String state = tags != null ? tags.optString("addr:state", "Unknown State") : "Maharashtra";

            result.append(String.format("Name: %s, Area: %s, City: %s, State: %s\n",
                    name, area, city, state));
        }

        return result.toString();
    }
}
