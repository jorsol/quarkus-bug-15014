
package org.acme;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.acme.s3.S3Manager;
import org.junit.jupiter.api.Test;

@QuarkusTest
class FileTest {

  @Inject
  S3Manager s3;

  @Test
  void test() throws IOException {
    final String fileContent = ""
        + "# -----------------------------   "
        + "# PostgreSQL configuration file   "
        + "# -----------------------------   "
        + "checkpoint_completion_target = 0.9";

    Path file = Files.createTempFile("bug", "15014");
    Files.writeString(file, fileContent);

    s3.uploadFile("demo/path", file.toFile());

    try (InputStream is = s3.downloadFile("demo/path")) {
      String fromS3 = new String(is.readAllBytes(), StandardCharsets.UTF_8);
      assertEquals(fileContent, fromS3);
    }

    Files.deleteIfExists(file);
  }

}
