package com.maviinamane.marketplace;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface FarmManagerRepository extends MongoRepository<FarmManager,String>{Optional<FarmManager> findByEmailIgnoreCase(String email);}
