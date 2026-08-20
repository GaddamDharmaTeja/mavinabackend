package com.maviinamane.product;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ProductRepository extends MongoRepository<Product, String> {
     List<Product> findByActiveTrue(); 
     List<Product> findByFarmIdAndActiveTrue(String farmId);
     List<Product> findByVarietyIgnoreCaseAndActiveTrue(String variety);
      List<Product> findByPriceLessThanEqualAndActiveTrue(BigDecimal price);
     }
