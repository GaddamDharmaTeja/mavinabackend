package com.maviinamane.category;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends MongoRepository<Category, String> {

    List<Category> findByActiveTrue();
}