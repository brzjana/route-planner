package com.inmods.routeplanner.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import com.inmods.routeplanner.error.CityNotFoundException;
import com.inmods.routeplanner.repository.CityRepository;
import com.inmods.routeplanner.model.City;
import com.inmods.routeplanner.model.Link;
import com.inmods.routeplanner.repository.LinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

/**
 * @author Ronald Tanner
 */
@Service
public class RouteService {

    Logger logger = LoggerFactory.getLogger((RouteService.class));
    @Autowired
    CityRepository cityRepository;
    @Autowired
    LinkRepository linkRepository;

    /**
     * find all links completely included within an array of cities
     *
     * @param cities
     * @return routes
     */
    List<Link> findAllLinks(List<City> cities) {
        return StreamSupport.stream(linkRepository.findAll().spliterator(), false).filter(
                l -> l.isIncludedIn(cities)).collect(Collectors.toList());
    }

    /**
     * find shortest path from source to target city See {@linktourl http://en.wikipedia.org/wiki/Dijkstra%27s_algorithm}
     *
     * @return itinerary
     */
    public List<Link> findShortestPath(String fromName, String toName) {
        List<City> cities = findCitiesBetween(fromName.strip(), toName.strip());
        List<Link> links = findAllLinks(cities);
        logger.info("have {} cities, {} links between {} and {}",
                cities.size(), links.size(), fromName, toName);
        if (links == null || links.size() == 0) {
            return new ArrayList<Link>();
        }
        City source = findCity(fromName);
        City target = findCity(toName);
        List<City> Q = new ArrayList<City>();
        Map<City, Double> dist = new HashMap<City, Double>();
        Map<City, City> previous = new HashMap<City, City>();
        for (City v : cities) {
            dist.put(v, Double.MAX_VALUE);
            previous.put(v, null);
            Q.add(v);
        }
        dist.put(source, 0.);
        while (!Q.isEmpty()) {
            City u = null;
            int i = 0;
            int iu = 0;
            double minDist = Double.MAX_VALUE;
            // find city u with shortest dist
            for (City c : Q) {
                if (dist.get(c) < minDist) {
                    iu = i;
                    u = c;
                    minDist = dist.get(c);
                }
                i++;
            }
            if (u != null) {
                Q.remove(iu);
                for (City n : findNeighbors(links, u)) {
                    double alt = dist.get(u) + getDist(links, u, n);
                    if (!dist.containsKey(n) || alt < dist.get(n)) {
                        dist.put(n, alt);
                        previous.put(n, u);
                    }
                }
            } else {
                break;
            }
        }
        LinkedList<City> citiesOnPath = new LinkedList<City>();
        City u = target;
        while (previous.get(u) != null) {
            citiesOnPath.addFirst(u);
            u = previous.get(u);
        }
        citiesOnPath.addFirst(source);
        // create path from source to target
        City from = null;
        City to = null;
        List<Link> itinerary = new ArrayList<Link>();
        for (City c : citiesOnPath) {
            from = to;
            to = c;
            if (from != null) {
                itinerary.add(findRoute(from, to, links));
            }
        }
        // set link id:
        int i = 0;
        for (Link l : itinerary) {
            l.setId(Integer.toString(i));
            i++;
        }
        return itinerary;
    }

    private Double getDist(List<Link> links, City a, City b) {
        for (Link r : links) {
            if ((a.getName().equals(r.getFrom()) && b.getName().equals(r.getTo()))
                    || (b.getName().equals(r.getFrom()) && a.getName().equals(r.getTo()))) {
                return r.getWeight();
            }
        }
        return Double.MAX_VALUE;
    }

    private Link findRoute(City from, City to, List<Link> links) {
        for (Link r : links) {
            if ((from.getName().equals(r.getFrom()) && to.getName().equals(r.getTo())) ||
                    (to.getName().equals(r.getFrom()) && from.getName().equals(r.getTo()))) {
                return new Link(from.getName(), to.getName(), r.getWeight(), r.getTransportMode());
            }
        }
        return null;
    }

    private List<City> findNeighbors(List<Link> links, City c) {
        List<City> neighbors = new ArrayList<City>();
        for (Link r : links) {
            if (c.getName().equals(r.getFrom())) {
                neighbors.add(findCity(r.getTo()));
            } else if (c.getName().equals(r.getTo())) {
                neighbors.add(findCity(r.getFrom()));
            }
        }
        return neighbors;
    }

    /**
     * find city by name
     *
     * @param name of city
     * @return
     */
    public City findCity(String name) {
        try {
            return cityRepository.findByName(name).get(0);
        }catch(IndexOutOfBoundsException ex){
            throw new CityNotFoundException(name);
        }
    }

    /**
     * find all cities located within the distance of 2 cities
     *
     * @param fromName name of starting city
     * @param toName   name of destination city
     * @return array of cities between from city and to city
     */
    List<City> findCitiesBetween(String fromName, String toName) {
        if (fromName == null || toName == null) {
            return new ArrayList<City>();
        }
        City from = findCity(fromName);
        City to = findCity(toName);
        Distance dist = new Distance(from.distance(to), Metrics.KILOMETERS);
        Point center = new Point((to.getLocation().getX() + from.getLocation().getX()) / 2,
                (to.getLocation().getY() + from.getLocation().getY()) / 2);
        return cityRepository.findByLocationNear(center, dist);
    }

    /**
     * find linked cities of a country
     *
     * @param country
     * @return array of linked cities in country
     */
    public Iterable<String> findCities(String country) {
        Set<String> cities = new HashSet<String>();
        for (Link l : linkRepository.findAll()) {
            for (City c : List.of(findCity(l.getFrom()), findCity(l.getTo()))) {
                if (c.getCountry().equals(country)) {
                    cities.add(c.getName());
                }
            }
        }
        return cities;
    }

    /**
     * find all cities of country
     * @param country
     * @return cities
     */
    public Iterable<City> findAllCities(String country){
        return StreamSupport.stream(cityRepository.findAll().spliterator(), true)
                .filter(c -> country.equals(c.getCountry())).collect(Collectors.toList());
    }

    /**
     * find all countries with linked cities
     *
     * @return array of names of all included country
     */
    public Iterable<String> findCountries() {
        Set<String> countries = new HashSet<String>();
        for (Link l : linkRepository.findAll()) {
            for (City c : List.of(findCity(l.getFrom()), findCity(l.getTo()))) {
                countries.add(c.getCountry());
            }
        }
        return countries;
    }
}