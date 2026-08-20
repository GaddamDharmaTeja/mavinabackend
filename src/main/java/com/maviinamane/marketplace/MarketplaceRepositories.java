package com.maviinamane.marketplace;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
interface FarmRepository extends MongoRepository<Farm,String>{}
interface SellerApplicationRepository extends MongoRepository<SellerApplication,String>{}
interface DeliveryZoneRepository extends MongoRepository<DeliveryZone,String>{ Optional<DeliveryZone> findFirstByPincodesContainingAndActiveTrue(String pincode); }
interface NotificationRepository extends MongoRepository<Notification,String>{ java.util.List<Notification> findByRecipientEmailOrderByCreatedAtDesc(String email); }
