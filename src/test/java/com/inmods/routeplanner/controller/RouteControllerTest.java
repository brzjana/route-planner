package com.inmods.routeplanner.controller;

import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import com.inmods.routeplanner.controller.RouteController;
import com.inmods.routeplanner.model.Link;
import com.inmods.routeplanner.model.Link;
import com.inmods.routeplanner.model.Link.TransportMode;
import com.inmods.routeplanner.service.RouteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@ActiveProfiles("mocktest")
class RouteControllerTest {

    @Autowired
    RouteController routeController;

    @Autowired
    MockMvc mvc;

    @MockBean
    RouteService routeService;

    @Test
    void findShortestRoute() throws Exception {
        Link fromto = new Link("From", "To", 1.0, TransportMode.TRAIN);
        given(routeService.findShortestPath("From", "To")).willReturn(
                Collections.singletonList(fromto));
        mvc.perform(get("/route/From/To"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].from", is(fromto.getFrom())));
    }
}