package com.maviinamane.order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface OrderRepository extends MongoRepository<Order,String>{
    
    Optional<Order> findByOrderNumberIgnoreCase(String orderNumber);
    List<Order> findAllByOrderByCreatedAtDesc();
    List<Order> findByEmailIgnoreCaseOrderByCreatedAtDesc(String email);
}
