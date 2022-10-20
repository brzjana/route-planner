package com.inmods.routeplanner.controller;

import com.inmods.routeplanner.model.City;
import com.inmods.routeplanner.model.Link;
import com.inmods.routeplanner.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RouteController {

    @Autowired
    RouteService routeService;

    @GetMapping("/route/{from}/{to}")
    public List<Link> findShortestRoute(@PathVariable String from, @PathVariable String to) {
        return routeService.findShortestPath(from, to);
    }

    @GetMapping("/city/{name}")
    public City findCity(@PathVariable String name) {
        return routeService.findCity(name);
    }

    @GetMapping("/cities/{country}")
    public Iterable<String> getCities(@PathVariable String country) {
        return routeService.findCities(country);
    }

    @GetMapping("/countries")
    public Iterable<String> getCountries() {
        return routeService.findCountries();
    }
}