package ru.job4j.carsale.controllers;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author Sir-Hedgehog (mailto:quaresma_08@mail.ru)
 * @version 1.0
 * @since 28.04.2020
 */

public class DownloadS3Servlet extends HttpServlet {

    private static final String S3_BUCKET_NAME = "cloud-cube-eu/t7qeqayxsytc/public/autos";

    /**
     * Метод отображает фото продаваемого автомобиля
     * @param request  - запрос серверу
     * @param response - ответ сервера
     */

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
        String name = request.getParameter("name");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("name=" + name);
        response.setContentType("image/jpg");

        /*response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400");*/

        response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
        S3Object object = s3Client.getObject(new GetObjectRequest(S3_BUCKET_NAME, name));
        try (InputStream reader = object.getObjectContent()) {
            response.getOutputStream().write(reader.readAllBytes());
        }
    }
}
