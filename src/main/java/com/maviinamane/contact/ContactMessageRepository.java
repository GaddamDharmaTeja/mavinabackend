package com.maviinamane.contact;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ContactMessageRepository extends MongoRepository<ContactMessage,String>{List<ContactMessage> findAllByOrderByCreatedAtDesc();}
