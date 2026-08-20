package com.maviinamane.admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HexFormat;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {
  private static final Map<String, String> EXTENSIONS = Map.of(
      "image/jpeg", ".jpg", "image/png", ".png", "image/webp", ".webp", "image/gif", ".gif",
      "image/avif", ".avif", "image/heic", ".heic", "image/heif", ".heif");
  private final Path directory;
  private final long maxBytes;

  public UploadController(@Value("${app.upload-dir}") String uploadDir,
                          @Value("${app.upload-max-bytes:10485760}") long maxBytes) {
    directory = Paths.get(uploadDir).toAbsolutePath().normalize();
    this.maxBytes = maxBytes;
  }

  @PostMapping
  public Upload upload(@RequestParam("file") MultipartFile file) {
    if (file.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose an image to upload");
    if (file.getSize() > maxBytes) throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Image is larger than the configured upload limit");
    String mediaType = inspect(file);
    try {
      Files.createDirectories(directory);
      String name = java.util.UUID.randomUUID() + EXTENSIONS.get(mediaType);
      Path saved = directory.resolve(name).normalize();
      if (!saved.startsWith(directory)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid upload destination");
      Files.copy(file.getInputStream(), saved, StandardCopyOption.REPLACE_EXISTING);
      return new Upload("/uploads/" + name, mediaType, file.getSize(), file.getOriginalFilename());
    } catch (IOException error) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store image", error);
    }
  }

  private String inspect(MultipartFile file) {
    try (InputStream input = file.getInputStream()) {
      byte[] bytes = input.readNBytes(32);
      String hex = HexFormat.of().formatHex(bytes);
      if (hex.startsWith("ffd8ff")) return "image/jpeg";
      if (hex.startsWith("89504e470d0a1a0a")) return "image/png";
      if (hex.startsWith("474946383761") || hex.startsWith("474946383961")) return "image/gif";
      if (bytes.length >= 12 && new String(bytes, 0, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("RIFF")
          && new String(bytes, 8, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("WEBP")) return "image/webp";
      if (bytes.length >= 12 && new String(bytes, 4, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("ftyp")) {
        String brand = new String(bytes, 8, Math.min(12, bytes.length - 8), java.nio.charset.StandardCharsets.US_ASCII).toLowerCase();
        if (brand.contains("avif") || brand.contains("avis")) return "image/avif";
        if (brand.contains("heic") || brand.contains("heix") || brand.contains("heif") || brand.contains("mif1")) return "image/heic";
      }
    } catch (IOException error) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not inspect image", error);
    }
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upload a JPEG, PNG, WebP, GIF, AVIF, HEIC, or HEIF image");
  }

  public record Upload(String url, String mediaType, long size, String originalName) { }
}
