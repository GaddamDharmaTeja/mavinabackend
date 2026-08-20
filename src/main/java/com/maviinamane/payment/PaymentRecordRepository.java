package com.maviinamane.payment;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRecordRepository extends MongoRepository<PaymentRecord, String> {
  List<PaymentRecord> findByOrderNumberOrderByCreatedAtDesc(String orderNumber);
  List<PaymentRecord> findAllByOrderByCreatedAtDesc();
}
