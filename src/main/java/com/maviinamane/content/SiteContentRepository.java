package com.maviinamane.content;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SiteContentRepository extends MongoRepository<SiteContent, String> {

    Optional<SiteContent> findByKey(String key);

}