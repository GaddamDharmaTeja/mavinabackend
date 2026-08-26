package com.maviinamane.config;

import com.maviinamane.product.Product;
import com.maviinamane.product.ProductRepository;
import com.mongodb.client.gridfs.model.GridFSFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

@Configuration
public class LegacyGridFsMigration {

  @Bean
  CommandLineRunner migrateLegacyUploads(
      GridFsTemplate gridFs,
      ProductRepository products,
      @Value("${app.upload-dir:uploads}") String uploadDir,
      @Value("${app.gridfs-migrate-legacy:false}") boolean enabled) {
    return args -> {
      if (!enabled) return;

      Path directory = Path.of(uploadDir).toAbsolutePath().normalize();
      if (!Files.isDirectory(directory)) return;

      try (var paths = Files.list(directory)) {
        paths.filter(Files::isRegularFile).forEach(path -> migrate(path, gridFs, products));
      }
    };
  }

  private void migrate(Path path, GridFsTemplate gridFs, ProductRepository products) {
    String legacyName = path.getFileName().toString();
    GridFSFile existing = gridFs.findOne(new Query(Criteria.where("metadata.legacyFilename").is(legacyName)));
    ObjectId id;
    if (existing == null) {
      try (var input = Files.newInputStream(path)) {
        String mediaType = Files.probeContentType(path);
        Document metadata = new Document("entityType", "LEGACY")
            .append("legacyFilename", legacyName)
            .append("migratedAt", Instant.now());
        id = gridFs.store(input, legacyName, mediaType == null ? "application/octet-stream" : mediaType, metadata);
      } catch (IOException error) {
        throw new IllegalStateException("Could not migrate " + legacyName, error);
      }
    } else {
      id = (ObjectId) existing.getObjectId();
    }

    String oldUrl = "/uploads/" + legacyName;
    String newUrl = "/api/uploads/" + id.toHexString();
    products.findAll().stream()
        .filter(product -> oldUrl.equals(product.getImageUrl()))
        .forEach(product -> {
          product.setImageUrl(newUrl);
          products.save(product);
        });
  }
}
