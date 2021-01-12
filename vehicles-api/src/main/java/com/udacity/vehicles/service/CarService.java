package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import com.udacity.vehicles.exception.CarNotFoundException;
import com.udacity.vehicles.exception.MapsNotFoundException;
import com.udacity.vehicles.exception.PriceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    @Autowired
    CarRepository repository;

    @Autowired
    MapsClient mapsClient;

    @Autowired
    PriceClient priceClient;

    /**
     * Gathers a list of all vehicles
     *
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        return repository.findAll().stream()
                .peek(car -> {
                    car.setLocation(mapsClient.getAddress(car.getLocation()));
                    car.setPrice(priceClient.getPrice(car.getId()));
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     *
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {
        if (repository.findById(id).isPresent()) {
            Car car = repository.findById(id).get();

            /**
             * Note: The car class file uses @transient, meaning you will need to call
             *   the pricing service each time to get the price.
             */
            try {
                String price = priceClient.getPrice(id);
                car.setPrice(price);
            } catch (Exception e) {
                throw new PriceNotFoundException(id);
            }

            /**
             * Note: The Location class file also uses @transient for the address,
             * meaning the Maps service needs to be called each time for the address.
             */

            Location loc = car.getLocation();
            try {
                Location result = mapsClient.getAddress(loc);
                car.setLocation(result);
            } catch (Exception e) {
                throw new MapsNotFoundException();
            }
            return car;

        } else {
            throw new CarNotFoundException(id);
        }
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     *
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        car.setCreatedAt(LocalDateTime.now());
        return repository.save(car);
    }

    public Car update(Car car) {
        if (repository.findById(car.getId()).isPresent()) {
            car.setModifiedAt(LocalDateTime.now());
            return repository.save(car);
        } else {
            throw new CarNotFoundException(car.getId());
        }
    }


    /**
     * Deletes a given car by ID
     *
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new CarNotFoundException(id);
        } else {
            try {
                repository.deleteById(id);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }
}
