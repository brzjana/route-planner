package com.inmods.routeplanner.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import com.inmods.routeplanner.model.City;
import com.inmods.routeplanner.model.Link;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class LinkTest {

    static List<City> cities;

    @BeforeAll
    public static void setup(){
        cities = new ArrayList<City>();
        City city = new City();
        city.setName("Basel");
        cities.add(city);
        city = new City();
        city.setName("Liestal");
        cities.add(city);
    }
    @Test
    public void cityIsIncluded(){
        Link l = new Link();
        l.setFrom("Basel");
        l.setTo("Bern");
        assertTrue(l.isIncludedIn(cities));
    }
}
