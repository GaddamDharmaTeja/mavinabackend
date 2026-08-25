package com.maviinamane.delivery;
import java.util.List; import org.springframework.data.mongodb.repository.MongoRepository;
public interface DeliveryChargeConfigRepository extends MongoRepository<DeliveryChargeConfig,String>{List<DeliveryChargeConfig> findByTypeOrderByMinValueAsc(String type); List<DeliveryChargeConfig> findByTypeAndActiveTrueOrderByMinValueAsc(String type);}
