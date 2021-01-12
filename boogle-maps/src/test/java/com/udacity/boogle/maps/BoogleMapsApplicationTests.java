package com.udacity.boogle.maps;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BoogleMapsApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Test
    public void contextLoads() {
    }

    @Test
    public void getPrice() throws Exception {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("lat", "123.30");
        map.add("lon", "123.30");
        mvc.perform(
                get(new URI("/maps"))
                        .params(map)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
