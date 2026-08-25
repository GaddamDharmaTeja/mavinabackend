package com.maviinamane.delivery;
import java.math.BigDecimal; import java.time.Instant;
import org.springframework.data.annotation.Id; import org.springframework.data.mongodb.core.mapping.Document;
@Document("delivery_charge_configs") public class DeliveryChargeConfig {
 @Id private String id; private String type; private BigDecimal minValue,maxValue,charge=BigDecimal.ZERO; private boolean active=true; private Instant createdAt=Instant.now(),updatedAt=Instant.now();
 public String getId(){return id;} public void setId(String v){id=v;} public String getType(){return type;} public void setType(String v){type=v;} public BigDecimal getMinValue(){return minValue;} public void setMinValue(BigDecimal v){minValue=v;} public BigDecimal getMaxValue(){return maxValue;} public void setMaxValue(BigDecimal v){maxValue=v;} public BigDecimal getCharge(){return charge;} public void setCharge(BigDecimal v){charge=v;} public boolean isActive(){return active;} public void setActive(boolean v){active=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;} public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
}
