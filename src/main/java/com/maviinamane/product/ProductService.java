package com.maviinamane.product;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);


    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

   public List<Product> findAll(String variety, BigDecimal maxPrice) {

    List<Product> products = repository.findByActiveTrue()
            .stream()
            .filter(Product::isAvailable)
            .filter(product ->
                    variety == null
                            || variety.isBlank()
                            || product.getVariety().equalsIgnoreCase(variety))
            .filter(product ->
                    maxPrice == null
                            || product.getPrice().compareTo(maxPrice) <= 0)
            .toList();

    log.info("Found {} products matching criteria: variety={}, maxPrice={}",
            products.size(), variety, maxPrice);

    return products;
}

    public Product findById(String id) {
        return repository.findById(id)
                .filter(Product::isActive)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found"));
    }

    public Product create(Product product) {
        return repository.save(product);
    }

    public Product update(String id, Product product) {
        product.setId(id);
        findById(id);
        return repository.save(product);
    }

    public void archive(String id) {
        Product product = findById(id);
        product.setActive(false);
        repository.save(product);
    }
}
