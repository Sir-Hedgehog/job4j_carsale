package ru.job4j.carsale.models;

import java.util.List;

/**
 * @author Sir-Hedgehog (mailto:quaresma_08@mail.ru)
 * @version 3.0
 * @since 13.05.2020
 */

public interface TemplateBase {
    boolean saveAd(Seller seller, Car car);
    String updateStatus(String joinId);
    List<Car> getData(String firm, String photo, String time);
}
