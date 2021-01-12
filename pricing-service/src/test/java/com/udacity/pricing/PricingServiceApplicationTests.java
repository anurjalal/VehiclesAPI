package com.udacity.pricing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PricingServiceApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Test
    public void contextLoads() {
    }

    @Test
    public void getPrice() throws Exception {
    	// LongStream.range(1,20) is stop in 19, so there is only 19 valid vehicleId
        mvc.perform(
                get(new URI("/services/price"))
                        .param("vehicleId","19")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

	@Test
	public void priceNotFoundTest() throws Exception {
		// LongStream.range(1,20) is stop in 19, so there is only 19 valid vehicleId
		mvc.perform(
				get(new URI("/services/price"))
						.param("vehicleId","20")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}



}
