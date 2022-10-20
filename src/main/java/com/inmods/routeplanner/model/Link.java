package com.inmods.routeplanner.model;

import java.util.List;
import org.springframework.data.redis.core.RedisHash;

/**
 * represents a connection between 2 cities
 */
@RedisHash(value = "link")
public class Link {

    public Link(){}

    public Link(String from, String to, Double dist, TransportMode transportMode) {
        this.from = from;
        this.to = to;
        this.weight = dist;
        this.transportMode = transportMode;
    }

    public enum TransportMode {
        TRAIN, PLANE, BICYCLE, TRUCK, SHIP;
    }

    private String id;
    private String from;
    private String to;
    private Double weight; //Mass für Gewicht für die Verbindung, damit wird gesucht
    private TransportMode transportMode;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public TransportMode getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(TransportMode transportMode) {
        this.transportMode = transportMode;
    }

    /**
     * check if both ends of this link are included in the array of cities
     *
     * @param cities
     * @return true if this link is included in cities
     */
    public boolean isIncludedIn(List<City> cities) {
        for (City c : cities) {
            if (c.getName().equals(from) || c.getName().equals(to)) {
                return true;
            }
        }
        return false;
    }

}