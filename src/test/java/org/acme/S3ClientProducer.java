
package org.acme;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import io.quarkus.test.Mock;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Mock
@ApplicationScoped
public class S3ClientProducer {

  private S3Client client;

  // Just example keys
  private final String ACCESS_KEY = "AKIAIOSFODNN7EXAMPLE";
  private final String SECRET_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";

  public final GenericContainer<?> minio =
      new GenericContainer<>(DockerImageName.parse("minio/minio:latest"))
          .withExposedPorts(9000)
          .withEnv("MINIO_ACCESS_KEY", ACCESS_KEY)
          .withEnv("MINIO_SECRET_KEY", SECRET_KEY)
          .withCommand("server /data")
          .waitingFor(Wait.forListeningPort());

  @ConfigProperty(name = "mybucket.name")
  String bucketName;

  /**
   * Build a client.
   *
   * @return S3Client
   */
  @Produces
  @ApplicationScoped
  public S3Client client() {
    minio.start();
    String endpoint = "http://" + minio.getContainerIpAddress() + ":" + minio.getMappedPort(9000);

    try {
      URI uriEndpoint = new URI(endpoint);
      client = S3Client.builder()
          .endpointOverride(uriEndpoint)
          .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
              ACCESS_KEY, SECRET_KEY)))
          .region(Region.of("us-east-1"))
          .build();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

    client.createBucket(b -> b.bucket(bucketName));
    return client;
  }

  /**
   * Close client connection.
   */
  @PreDestroy
  public void destroy() {
    if (client != null) {
      client.close();
    }
    if (minio != null) {
      minio.stop();
    }
  }

}
