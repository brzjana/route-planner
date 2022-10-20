package com.inmods.routeplanner.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.GeoIndexed;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash(value = "city")
public class City {
    @Id
    String id;
    @Indexed
    String name;
    Integer population;
    @GeoIndexed
    Point location;
    String country;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    //Ob zwei Städte gleich sind?
    public boolean equals(Object obj){
        if(this == obj)
            return true;
        if((obj == null) || (obj.getClass() != this.getClass())) //Klasse, id und Name muss gleich sein
            return false;
        City c = (City)obj;
        if( this.id != null) {
            return this.id.equals(c.id);
        }
        return this.name.equals(c.name);
    }

    public int hashCode(){
        int hash = 7;
        hash = 31 * hash + id.hashCode();
        return hash;
    }
    public double distance(City to){
        if( this.location.getY() == to.location.getY() &&
                this.location.getX() == to.location.getX() )
            return 0.0;
        // convert coordinates into radian
        double k = Math.PI/180;
        double a = this.location.getY()*k; // Latitude
        double b = this.location.getX()*k; // Longitude
        double c = to.location.getY()*k;   // Latitude
        double d = to.location.getX()*k;   // Longitude
        double x = Math.sin(a)*Math.sin(c)+
                Math.cos(a)*Math.cos(c)*Math.cos(b-d);
        double radius = 6371.007176; // in km
        if (x > 1) x = 1.0;
        return radius*Math.acos(x);
    }

}