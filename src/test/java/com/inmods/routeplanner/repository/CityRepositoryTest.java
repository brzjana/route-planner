package com.inmods.routeplanner.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import com.inmods.routeplanner.model.City;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;

@SpringBootTest
class CityRepositoryTest extends RedisContainer {
    @Autowired
    CityRepository cityRepository;

    @Test
    void findByName() {
        List<City> cities = cityRepository.findByName("Basel");
        assertEquals("Basel", cities.get(0).getName());
    }

    @Test
    void findByLocationNear() {
        Distance dist = new Distance(10.0, Metrics.KILOMETERS);
        List<City> cities = cityRepository.findByLocationNear(new Point( 7.5, 47.5), dist);
        assertEquals("Basel", cities.get(0).getName());
    }
}

//repository: hat die Aufgabe mit der Datenbank zu kommunizieren