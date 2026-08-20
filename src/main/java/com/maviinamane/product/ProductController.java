package com.maviinamane.product;
import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
@RestController
 @RequestMapping("/api/products")
  @CrossOrigin(origins="${app.cors-origin:http://localhost:3000}")

public class ProductController { 

  private final ProductService service;
  
   public ProductController(ProductService service){
    
    this.service=service;
    
    }
  @GetMapping public List<Product> all(@RequestParam(required=false) String variety,@RequestParam(required=false) BigDecimal maxPrice){
    
    return service.findAll(variety,maxPrice);
    
    }
  @GetMapping("/{id}") public Product one(@PathVariable String id){
    
    return service.findById(id);
    
    }
}
