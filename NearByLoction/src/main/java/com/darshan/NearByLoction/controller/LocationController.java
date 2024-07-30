package com.darshan.NearByLoction.controller;

import com.darshan.NearByLoction.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class LocationController {

    @Autowired
    private HotelService hotelService;


    @GetMapping("/nearbyHotels")
    public Mono<String> getNearbyHotels(@RequestParam double latitude, @RequestParam double longitude) {
        return hotelService.getNearbyHotels(latitude, longitude)
                .map(response -> {
                    // Ensure response has proper JSON Content-Type header
                    return response;
                });
    }



//
//    @GetMapping("/shops")
//    public Mono<String> getNearbyShops(@RequestParam double lat, @RequestParam double lng) {
//        return ShopService.getNearbyShops(lat, lng);
//    }
}
