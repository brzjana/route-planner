package com.inmods.routeplanner.repository;

import com.inmods.routeplanner.model.City;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface CityRepository extends CrudRepository<City, String> {
    List<City> findByName(String name);
    List<City> findByLocationNear(Point point, Distance distance);
}
