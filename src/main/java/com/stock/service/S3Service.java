package com.stock.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

/*
https://javatutorial.net/java-s3-example
 */
@RestController
@RequestMapping(value = "/s3")
public class S3Service {

    private AmazonS3 s3Client;

    public S3Service() {
        AWSCredentials credentials = new ProfileCredentialsProvider().getCredentials();
        s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_SOUTH_1).build();
    }

    @GetMapping("/createBucket")
    public String createBucket(String bucket) {
        Bucket b = s3Client.createBucket(bucket);
        return "SUCCESS : " + b.getName();
    }

    @GetMapping("/deleteBucket")
    public String deleteBucket(String bucket) {
        s3Client.deleteBucket(bucket);
        return "SUCCESS";
    }

    @GetMapping("/listAll")
    public List<String> listBucket() {
        return s3Client.listBuckets().stream().map(Bucket::getName).collect(Collectors.toList());
    }

    @GetMapping("/read")
    public String readFile(String bucket, String fileName) {
        S3Object object = s3Client.getObject(bucket, fileName);
        InputStream objectData = object.getObjectContent();
        String data="";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(objectData));

            String line;
            while((line = reader.readLine())!=null){
                data+=line+"\n";
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return data;
    }

    public String upload(File file, String fileName, String bucket) {
        s3Client.putObject(bucket, fileName, file);
        return "Done";
    }
}
