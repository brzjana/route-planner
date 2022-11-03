package com.inmods.routeplanner.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.inmods.routeplanner.model.City;
import com.inmods.routeplanner.model.Link;
import com.inmods.routeplanner.model.Link.TransportMode;
import com.inmods.routeplanner.repository.CityRepository;
import com.inmods.routeplanner.repository.LinkRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;

@SpringBootTest
class RouteServiceTest {
    @InjectMocks
    RouteService routeService;

    @Mock
    CityRepository cityRepository;
    @Mock
    LinkRepository linkRepository;

    /**
     * set up a dummy list of 3 cities "from", "between", "to"
     *
     * @return list of 3 cities
     */
    private List<City> createCities() {
        City from = new City();
        from.setName("from");
        from.setId("from");
        from.setLocation(new Point(0, 0));
        City between = new City();
        between.setName("between");
        between.setId("between");
        between.setLocation(new Point(0, 1));
        City to = new City();
        to.setName("to");
        to.setId("to");
        to.setLocation(new Point(1, 1));
        return Arrays.asList(from, between, to);
    }

    @Test
    public void findShortestPath() {
        List<City> cities = createCities();
        // add to links from - between and between - to
        Link frombetween = new Link(cities.get(0).getName(), cities.get(1).getName(), 1.0, TransportMode.TRAIN);
        Link betweenTo = new Link(cities.get(1).getName(), cities.get(2).getName(), 1.0, TransportMode.TRAIN);

        for (City c : cities) {
            when(cityRepository.findByName(c.getName())).thenReturn(Collections.singletonList(c));
        }
        when(cityRepository.findByLocationNear(any(Point.class), any(Distance.class))).thenReturn(
                cities);
        when(linkRepository.findAll()).thenReturn(Arrays.asList(frombetween, betweenTo));

        List<Link> itinerary = routeService.findShortestPath(cities.get(0).getName(), cities.get(2).getName());

        assertEquals(2, itinerary.size());
        // should contain "from - between", "between - to"
        assertEquals(itinerary.get(0).getFrom(), cities.get(0).getName());
        assertEquals(itinerary.get(1).getFrom(), cities.get(1).getName());
    }
}