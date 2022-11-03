package com.inmods.routeplanner.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.inmods.routeplanner.model.Link;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LinkRepositoryTest extends RedisContainer {

    @Autowired
    LinkRepository linkRepository;

    @Test
    public void findAll(){
        int num=0;
        for(Link l: linkRepository.findAll()){
            assertEquals("Basel", l.getFrom());
            assertEquals("Liestal", l.getTo());
            num++;
        }
        assertEquals(1, num);
    }
}
