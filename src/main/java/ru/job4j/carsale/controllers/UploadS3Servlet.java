package ru.job4j.carsale.controllers;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
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
    private static final String S3_BUCKET_NAME = "cloud-cube-eu";
    private static final Logger LOG = LoggerFactory.getLogger(UploadS3Servlet.class);
    private static final long serialVersionUID = -7720246048637220075L;
    private static final int THRESHOLD_SIZE = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 140; // 140MB
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 150; // 150MB
    //private static final String AMAZON_ACCESS_KEY = "AKIA37SVVXBHWIWJHZGF";
    //private static final String AMAZON_SECRET_KEY = "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";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        response.setHeader("Access-Control-Max-Age", "86400");
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(THRESHOLD_SIZE);

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setFileSizeMax(MAX_FILE_SIZE);
        upload.setSizeMax(MAX_REQUEST_SIZE);

        String nameOfFile = "";

        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

        //BasicAWSCredentials awsCredentials = new BasicAWSCredentials(AMAZON_ACCESS_KEY, AMAZON_SECRET_KEY);
        /*AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.EU_WEST_1)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();*/

        LOG.info("1");
        LOG.info("s3Client: " + s3Client);

        if (isMultipart) {
            try {
                List<FileItem> items = upload.parseRequest(request);
                File folder = new File("t7qeqayxsytc/public");
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                LOG.info("2");

                for (FileItem item : items) {
                    if (!item.isFormField()) {
                        ObjectMetadata om = new ObjectMetadata();
                        om.setContentLength(item.getSize());
                        nameOfFile = item.getName().substring(item.getName().lastIndexOf("\\") + 1);
                        try {
                            s3Client.putObject(new PutObjectRequest(S3_BUCKET_NAME, nameOfFile, item.getInputStream(), om));
                        } catch (AmazonServiceException ase) {
                            LOG.error("Error:" + ase.getMessage());
                            LOG.info("Caught an AmazonServiceException from PUT requests, rejected reasons:");
                            LOG.info("Error Message:    " + ase.getMessage());
                            LOG.info("HTTP Status Code: " + ase.getStatusCode());
                            LOG.info("AWS Error Code:   " + ase.getErrorCode());
                            LOG.info("Error Type:       " + ase.getErrorType());
                            LOG.info("Request ID:       " + ase.getRequestId());
                        }
                    }
                }

                LOG.info("3");

            } catch (Exception ex) {
                LOG.error("error: " + ex.getMessage());
            }
            response.getWriter().write(nameOfFile);
        }
    }

}
