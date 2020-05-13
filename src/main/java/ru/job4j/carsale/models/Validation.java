package ru.job4j.carsale.models;

import java.util.List;

/**
 * @author Sir-Hedgehog (mailto:quaresma_08@mail.ru)
 * @version 3.0
 * @since 13.05.2020
 */

public interface Validation {
    boolean validateAdd(Seller seller, Car car);
    String validateUpdate(String joinId);
    List<Car> validateGetData(String firm, String photo, String time);
}
