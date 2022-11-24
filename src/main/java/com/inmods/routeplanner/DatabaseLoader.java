package com.inmods.routeplanner;

import java.io.InputStream;
import java.util.Scanner;
import com.inmods.routeplanner.model.City;
import com.inmods.routeplanner.repository.CityRepository;
import com.inmods.routeplanner.repository.LinkRepository;
import com.inmods.routeplanner.model.Link;
import com.inmods.routeplanner.model.Link.TransportMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Component;

//wird einfach aufgerugen, wenn Spring startet (Frage: Muss der DataLoader auch beim Testen geladen werden? -nein)
@Component
@Profile("!mocktest") //Deshalb dies; nur starten, wenn nicht testprofile
public class DatabaseLoader implements CommandLineRunner {

    Logger logger = LoggerFactory.getLogger(DatabaseLoader.class);

    //Damit er es laden kann (Spring)
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private LinkRepository linkRepository;

    //Von einem File lesen (Property Datei)
    @Value("${routeplanner.cities}")
    String cityfile;
    @Value("${routeplanner.links}")
    String linkfile;

    //File zu lesen und in Datenbank einzufügen: Aufgabe von DatabaseLoader

    @Override
    public void run(String... strings) {
        long numCities = cityRepository.count();//Wie viele Cites sind drin? Anfrage an die Datenbank
        long numLinks = linkRepository.count();
        if (numCities == 0) { //Laden und sonst nicht
            numCities = loadCities();
            numLinks = loadLinks();
        }
        logger.info("Total {} cities {} links", numCities, numLinks); //Wie sinds und wie eviele Loinks geladen
    }

    private int loadCities() {
        int nrecs = 0;
        InputStream in = this.getClass().getResourceAsStream(cityfile);
        Scanner sc = new Scanner(in, "UTF-8");
        while (sc.hasNextLine()) {
            String[] recs = sc.nextLine().split("\t");
            City c = new City();
            c.setName(recs[0].strip());
            c.setCountry(recs[1].strip());
            c.setPopulation(Integer.valueOf(recs[2]));
            c.setLocation(new Point(Double.valueOf(recs[4]), Double.valueOf(recs[3])));
            cityRepository.save(c);
            nrecs++;
        }
        return nrecs;
    }

    private int loadLinks() {
        int nrecs = 0;
        InputStream in = this.getClass().getResourceAsStream(linkfile);
        Scanner sc = new Scanner(in, "UTF-8");
        while (sc.hasNextLine()) {
            String[] recs = sc.nextLine().split("\t");
            if (recs.length < 2) {
                logger.warn("illegal record at line {}", nrecs);
            } else {
                try {
                    City a = cityRepository.findByName(recs[0].strip()).get(0);
                    City b = cityRepository.findByName(recs[1].strip()).get(0);
                    Double dist = a.distance(b);
                    Link l = new Link(a.getName(), b.getName(), dist, TransportMode.TRAIN);
                    linkRepository.save(l);
                    nrecs++;
                } catch (IndexOutOfBoundsException e) {
                    logger.warn("City not found in {} {}", recs[0], recs[1]);
                }
            }
        }
        return nrecs;
    }

}