package com.maviinamane.admin;

import com.mongodb.client.gridfs.model.GridFSFile;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.HexFormat;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {
  private final GridFsTemplate gridFs;
  private final long maxBytes;

  public UploadController(GridFsTemplate gridFs, @Value("${app.upload-max-bytes:10485760}") long maxBytes) {
    this.gridFs = gridFs;
    this.maxBytes = maxBytes;
  }

  @PostMapping
  public Upload upload(@RequestParam("file") MultipartFile file,
                       @RequestParam(defaultValue = "GENERAL") String entityType,
                       @RequestParam(required = false) String entityId) {
    if (file.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose an image to upload");
    if (file.getSize() > maxBytes) throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Image exceeds the upload limit");
    String mediaType = inspect(file);
    Document metadata = new Document("entityType", entityType.trim().toUpperCase())
        .append("entityId", entityId == null || entityId.isBlank() ? null : entityId.trim())
        .append("originalName", file.getOriginalFilename())
        .append("uploadedAt", Instant.now());
    try (InputStream input = file.getInputStream()) {
      ObjectId id = gridFs.store(input, filename(file.getOriginalFilename()), mediaType, metadata);
      return new Upload(id.toHexString(), "/api/uploads/" + id.toHexString(), mediaType, file.getSize(),
          file.getOriginalFilename(), entityType.trim().toUpperCase(), entityId);
    } catch (IOException error) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store image in MongoDB", error);
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<InputStreamResource> download(@PathVariable String id) throws IOException {
    if (!ObjectId.isValid(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
    GridFSFile file = gridFs.findOne(new Query(Criteria.where("_id").is(new ObjectId(id))));
    if (file == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
    GridFsResource resource = gridFs.getResource(file);
    String contentType = resource.getContentType() == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : resource.getContentType();
    return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).contentLength(file.getLength())
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename(file.getFilename()) + "\"")
        .body(new InputStreamResource(resource.getInputStream()));
  }

  private String filename(String value) { return (value == null || value.isBlank() ? "image" : value).replaceAll("[\\\\/:*?\"<>|]", "_"); }

  private String inspect(MultipartFile file) {
    try (InputStream input = file.getInputStream()) {
      byte[] bytes = input.readNBytes(32); String hex = HexFormat.of().formatHex(bytes);
      if (hex.startsWith("ffd8ff")) return "image/jpeg";
      if (hex.startsWith("89504e470d0a1a0a")) return "image/png";
      if (hex.startsWith("474946383761") || hex.startsWith("474946383961")) return "image/gif";
      if (bytes.length >= 12 && new String(bytes, 0, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("RIFF") && new String(bytes, 8, 4, java.nio.charset.StandardCharsets.US_ASCII).equals("WEBP")) return "image/webp";
    } catch (IOException error) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not inspect image", error); }
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upload a JPEG, PNG, WebP, or GIF image");
  }

  public record Upload(String id, String url, String mediaType, long size, String originalName, String entityType, String entityId) { }
}
