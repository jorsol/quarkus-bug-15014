
package org.acme.s3;

import java.io.File;
import java.io.InputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ApplicationScoped
public class S3Manager {

  private static final Logger log = LoggerFactory.getLogger(S3Manager.class);

  @Inject
  S3Client s3;

  @ConfigProperty(name = "mybucket.name")
  String bucketName;

  public void uploadFile(String path, File file) {
    log.info("Upload file {} in path {}", file.getPath(), path);
    s3.putObject(buildPutRequest(path),
        RequestBody.fromFile(file));
  }

  public InputStream downloadFile(String path) {
    log.info("Download file from path {}", path);
    return s3.getObject(buildGetRequest(path));
  }


  private PutObjectRequest buildPutRequest(String objectKey) {
    return PutObjectRequest.builder()
        .bucket(bucketName)
        .key(objectKey)
        .build();
  }

  private GetObjectRequest buildGetRequest(String objectKey) {
    return GetObjectRequest.builder()
        .bucket(bucketName)
        .key(objectKey)
        .build();
  }

}
