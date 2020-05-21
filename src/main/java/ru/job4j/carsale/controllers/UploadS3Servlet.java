package ru.job4j.carsale.controllers;

import com.amazonaws.AmazonServiceException;
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400");
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        String nameOfFile = "";
        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

        LOG.info("1");
        LOG.info("s3Client: " + s3Client);

        try {
            List<FileItem> items = upload.parseRequest(request);
            File folder = new File("t7qeqayxsytc/public");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            LOG.info("2");

            for (FileItem item : items) {
                if (!item.isFormField()) {
                    //ObjectMetadata om = new ObjectMetadata();
                    //om.setContentLength(item.getSize());
                    nameOfFile = item.getName().substring(item.getName().lastIndexOf("\\") + 1);
                    File file = new File(folder + File.separator + nameOfFile);
                    try {
                        s3Client.putObject(new PutObjectRequest(S3_BUCKET_NAME, nameOfFile, file));
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
