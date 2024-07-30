package com.darshan.NearByLoction.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class HotelService {

    private static final String OVERPASS_URL = "https://overpass-api.de/api/interpreter";
    private final WebClient webClient;

    public HotelService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(OVERPASS_URL).build();
    }

    public Mono<String> getNearbyHotels(double latitude, double longitude) {
        String overpassQuery = String.format(
            "[out:json];node(around:5000,%f,%f)[tourism=hotel];out;", latitude, longitude
        );

        return webClient.get()
            .uri(uriBuilder -> uriBuilder.queryParam("data", overpassQuery).build())
            .retrieve()
            .bodyToMono(String.class)
            .doOnNext(response -> System.out.println("Raw Response: " + response))  // Logging raw response
            .map(this::parseResponse);
    }

    private String parseResponse(String response) {
        JSONObject json = new JSONObject(response);
        JSONArray elements = json.getJSONArray("elements");
        JSONArray result = new JSONArray();

        for (int i = 0; i < elements.length(); i++) {
            JSONObject element = elements.getJSONObject(i);
            JSONObject tags = element.optJSONObject("tags");

            if (tags != null) {
                JSONObject hotel = new JSONObject();
                hotel.put("name", tags.optString("name", "Unknown"));
                hotel.put("city", tags.optString("addr:city", "Unknown City"));
                hotel.put("area", tags.optString("addr:suburb", "Unknown Area"));
                hotel.put("state", tags.optString("addr:state", "Maharashtra"));
                hotel.put("contact", tags.optString("contact:phone", "No Contact"));
                result.put(hotel);
            } else {
                JSONObject hotel = new JSONObject();
                hotel.put("name", "Unknown");
                hotel.put("city", "Unknown City");
                hotel.put("area", "Unknown Area");
                hotel.put("state", "Maharashtra");
                hotel.put("contact", "No Contact");
                result.put(hotel);
            }
        }

        return result.toString(2);
    }
}
