package com.maviinamane.marketplace;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Browser keys must be restricted to the storefront origin in Google Cloud. */
@RestController
@RequestMapping("/api/maps")
@CrossOrigin(origins = "${app.cors-origin:http://localhost:3000}")
public class MapsController {
  private final String apiKey;

  public MapsController(@Value("${app.google-maps-api-key:}") String apiKey) { this.apiKey = apiKey; }

  @GetMapping("/config")
  public Config config() { return new Config(!apiKey.isBlank(), apiKey.isBlank() ? null : apiKey); }

  public record Config(boolean enabled, String browserKey) { }
}
