package com.maviinamane.product;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> findAll(String variety, BigDecimal maxPrice) {
        return repository.findByActiveTrue()
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
