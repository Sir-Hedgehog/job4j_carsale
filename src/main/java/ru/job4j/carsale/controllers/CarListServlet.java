package ru.job4j.carsale.controllers;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.carsale.models.Car;
import ru.job4j.carsale.models.ValidateService;
import ru.job4j.carsale.models.Validation;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sir-Hedgehog (mailto:quaresma_08@mail.ru)
 * @version 3.0
 * @since 13.05.2020
 */

public class CarListServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(CarListServlet.class);
    private final Validation validationDatabase = ValidateService.getInstance();

    /**
     * Метод выводит список объявлений из БД в зависимости от указанных фильтров
     * @param request - запрос серверу
     * @param response - ответ сервера
     */

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Set-Cookie", "HttpOnly; Secure; SameSite = Strict");
        List<List<String>> result = new ArrayList<>();
        String firm = request.getParameter("firm");
        String photo = request.getParameter("photo");
        String time = request.getParameter("time");
        List<Car> cars  = validationDatabase.validateGetData(firm, photo, time);
        for (Car car : cars) {
            List<String> current = new ArrayList<>();
            current.add(car.getImage());
            current.add(car.getSeller().getName());
            current.add(car.getSeller().getNumber());
            current.add(car.getModel());
            current.add(car.getBodyType());
            current.add(car.getYearOfRelease());
            current.add(car.getPower());
            current.add(car.getVolume());
            current.add(car.getPrice());
            current.add(car.getMileage());
            current.add(car.getStatus());
            current.add(String.valueOf(car.getSeller().getId()));
            current.add(car.getCreateDateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")));
            result.add(current);
        }
        String json = new Gson().toJson(result);
        response.getWriter().write(json);
    }
}
