package ru.job4j.carsale.controllers;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class UploadS3Servlet extends HttpServlet {
    private static final String AMAZON_ACCESS_KEY = "AKIA37SVVXBHQ22GGJXS";
    private static final String AMAZON_SECRET_KEY = "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";
    private static final String REGION = "eu-west-1";
    private static final String S3_BUCKET_NAME = "cloud-cube-eu";

    private static final Logger LOG = LoggerFactory.getLogger(UploadS3Servlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400");

        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        String nameOfFile = "";
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(AMAZON_ACCESS_KEY, AMAZON_SECRET_KEY);

        LOG.info("awsCredentials: " + awsCredentials);

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(REGION))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

        LOG.info("AU");
        LOG.info("s3Client: " + s3Client);

        try {
            List<FileItem> items = upload.parseRequest(request);
            File folder = new File("/public/temp/autos/");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            LOG.info("AU");
            LOG.info("s3Client: " + s3Client);

            for (FileItem item : items) {
                if (!item.isFormField()) {
                    nameOfFile = item.getName().substring(item.getName().lastIndexOf("\\") + 1);
                    File targetFile = new File(folder + nameOfFile);
                    try {
                        s3Client.putObject(new PutObjectRequest(S3_BUCKET_NAME, nameOfFile, targetFile));
                    } catch (AmazonServiceException ase) {
                        LOG.error("Error:" + ase.getMessage());
                    }
                }
            }

            LOG.info("AU");
            LOG.info("s3Client: " + s3Client);

        } catch (Exception ex) {
            LOG.error("error: " + ex.getMessage());
        }
        response.getWriter().write(nameOfFile);
    }
}
