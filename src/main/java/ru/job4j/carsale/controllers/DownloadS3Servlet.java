package ru.job4j.carsale.controllers;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author Sir-Hedgehog (mailto:quaresma_08@mail.ru)
 * @version 2.0
 * @since 26.09.2020
 */

public class DownloadS3Servlet extends HttpServlet {
    private static final String AMAZON_ACCESS_KEY = "AKIA37SVVXBHWIWJHZGF";
    private static final String AMAZON_SECRET_KEY = "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";
    private static final String REGION = "eu-west-1";
    private static final String S3_BUCKET_NAME = "cloud-cube-eu/t7qeqayxsytc/public/autos";
    private static final Logger LOG = LoggerFactory.getLogger(DownloadS3Servlet.class);

    /**
     * Метод отображает фото продаваемого автомобиля, полученное из хранилища aws s3 (amazon)
     * @param request  - запрос серверу
     * @param response - ответ сервера
     */

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        String name = request.getParameter("name");

        LOG.info("NAME OF FILE 1 : " + name);

        if (name.equals("Фото не выбрано")) {

            LOG.info("NAME OF FILE 2 : " + name);

            name = "no_image.jpg";
        }

        LOG.info("NAME OF FILE 3 : " + name);

        response.setContentType("name=" + name);
        response.setContentType("image/jpg");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(AMAZON_ACCESS_KEY, AMAZON_SECRET_KEY);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(REGION))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
        S3Object object = s3Client.getObject(new GetObjectRequest(S3_BUCKET_NAME, name));
        try (InputStream reader = object.getObjectContent()) {
            response.getOutputStream().write(reader.readAllBytes());
        }
    }
}
