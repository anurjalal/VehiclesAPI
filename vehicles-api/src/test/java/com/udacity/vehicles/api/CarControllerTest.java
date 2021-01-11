package com.udacity.vehicles.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.service.CarService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Implements testing of the CarController class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CarControllerTest {

    private String price = "5000";
    private Location loc = new Location(40.730610, -73.935242, "8 Avenue", "NYC", "NY", "20238");

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Car> json;

    @MockBean
    private CarService carService;

    @MockBean
    private PriceClient priceClient;

    @MockBean
    private MapsClient mapsClient;

    /**
     * Creates pre-requisites for testing, such as an example car.
     */
    @Before
    public void setup() {
        Car car = getCar();
        car.setId(1L);

        Car carWithClientData = getCar();
        carWithClientData.setId(1L);
        carWithClientData.setPrice(price);
        carWithClientData.setLocation(loc);

        given(carService.save(any())).willReturn(car);
        given(carService.findById(any())).willReturn(carWithClientData);
        given(carService.list()).willReturn(Collections.singletonList(car));
    }

    /**
     * Tests for successful creation of new car in the system
     *
     * @throws Exception when car creation fails in the system
     */
    @Test
    public void createCar() throws Exception {
        Car car = getCar();
        mvc.perform(
                post(new URI("/cars"))
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated());
    }

    /**
     * Tests if the read operation appropriately returns a list of vehicles.
     *
     * @throws Exception if the read operation of the vehicle list fails
     */
    @Test
    public void listCars() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get(new URI("/cars"))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk()).andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResult = mapper.readTree(result);
        ObjectNode obj = (ObjectNode) jsonResult;
        obj.remove("_links");
        assertEquals(1, obj.size());
    }

    /**
     * Tests the read operation for a single car by ID.
     *
     * @throws Exception if the read operation for a single car fails
     */
    @Test
    @Order(3)
    public void findCar() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get(new URI("/cars/1"))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk()).andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResult = mapper.readTree(result);

        Integer idResult = jsonResult.get("id").asInt();
        Double latResult = jsonResult.get("location").get("lat").asDouble();
        Double lonResult = jsonResult.get("location").get("lon").asDouble();
        String addressResult = jsonResult.get("location").get("address").asText();
        String cityResult = jsonResult.get("location").get("city").asText();
        String stateResult = jsonResult.get("location").get("state").asText();
        String zipResult = jsonResult.get("location").get("zip").asText();
        String priceResult = jsonResult.get("price").asText();

        assertEquals((Integer) 1, idResult);
        assertEquals(loc.getLat(), latResult);
        assertEquals(loc.getLon(), lonResult);
        assertEquals(loc.getAddress(), addressResult);
        assertEquals(loc.getCity(), cityResult);
        assertEquals(loc.getState(), stateResult);
        assertEquals(loc.getZip(), zipResult);
        assertEquals(price, priceResult);

    }

    /**
     * Tests the deletion of a single car by ID.
     *
     * @throws Exception if the delete operation of a vehicle fails
     */
    @Test
    @Order(4)
    public void deleteCar() throws Exception {
        mvc.perform(
                delete(new URI("/cars/1"))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isNoContent());
    }

    /**
     * Creates an example Car object for use in testing.
     *
     * @return an example Car object
     */
    private Car getCar() {
        Car car = new Car();
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.USED);
        return car;
    }
}