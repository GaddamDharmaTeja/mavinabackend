package com.maviinamane.marketplace;

import com.maviinamane.auth.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.maviinamane.product.Product;
import com.maviinamane.product.ProductRepository;
import java.util.List;

@RestController
@RequestMapping("/api/farm-managers")
@CrossOrigin(origins = "${app.cors-origin:http://localhost:3000}")
public class FarmManagerController {
  private final FarmManagerRepository managers;
  private final FarmRepository farms;
  private final PasswordEncoder encoder;
  private final JwtService jwt;
  private final ProductRepository products;

  public FarmManagerController(FarmManagerRepository managers, FarmRepository farms, PasswordEncoder encoder, JwtService jwt, ProductRepository products) {
    this.managers = managers;
    this.farms = farms;
    this.encoder = encoder;
    this.jwt = jwt;
    this.products = products;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public Token register(@Valid @RequestBody Register request) {
    String email = request.email().trim().toLowerCase();
    if (managers.findByEmailIgnoreCase(email).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Farm manager account already exists");
    }
    Farm farm = farms.findAll().stream()
        .filter(value -> email.equalsIgnoreCase(value.getManagerEmail()) && "ACTIVE".equals(value.getStatus()))
        .findFirst()
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Your approved farm account was not found"));
    FarmManager manager = new FarmManager();
    manager.setFarmId(farm.getId());
    manager.setName(farm.getManagerName());
    manager.setEmail(email);
    manager.setPasswordHash(encoder.encode(request.password()));
    managers.save(manager);
    return token(manager);
  }

  @PostMapping("/login")
  public Token login(@Valid @RequestBody Login request) {
    FarmManager manager = managers.findByEmailIgnoreCase(request.email())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
    if (!encoder.matches(request.password(), manager.getPasswordHash())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }
    return token(manager);
  }

  @GetMapping("/me")
  public FarmManager me(Authentication authentication) {
    return managers.findByEmailIgnoreCase(authentication.getName())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm manager account not found"));
  }

  @GetMapping("/products")
  public List<Product> products(Authentication authentication) {
    return products.findByFarmIdAndActiveTrue(manager(authentication).getFarmId());
  }

  @PatchMapping("/products/{id}/stock")
  public Product updateStock(@org.springframework.web.bind.annotation.PathVariable String id,
                             @RequestBody StockUpdate update, Authentication authentication) {
    if (update.stockQuantity() < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock cannot be negative");
    }
    FarmManager manager = manager(authentication);
    Product product = products.findById(id)
        .filter(item -> manager.getFarmId().equals(item.getFarmId()))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found for this farm"));
    product.setStockQuantity(update.stockQuantity());
    if (update.available() != null) product.setAvailable(update.available());
    return products.save(product);
  }

  private FarmManager manager(Authentication authentication) {
    return managers.findByEmailIgnoreCase(authentication.getName())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm manager account not found"));
  }

  private Token token(FarmManager manager) {
    return new Token(jwt.create(manager.getEmail(), "FARM_MANAGER"), manager.getEmail(), manager.getFarmId(), "FARM_MANAGER");
  }

  public record Register(@Email @NotBlank String email, @NotBlank @Size(min = 8) String password) { }
  public record Login(@Email @NotBlank String email, @NotBlank String password) { }
  public record Token(String token, String email, String farmId, String role) { }
  public record StockUpdate(int stockQuantity, Boolean available) { }
}
