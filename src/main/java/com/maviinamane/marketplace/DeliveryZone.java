package com.maviinamane.marketplace;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document("delivery_zones") public class DeliveryZone { @Id private String id; private String name; private List<String> pincodes; private BigDecimal fee=BigDecimal.ZERO, minimumOrder=BigDecimal.ZERO; private int estimatedDays=3; private boolean active=true; public String getId(){return id;} public void setId(String v){id=v;} public String getName(){return name;} public void setName(String v){name=v;} public List<String> getPincodes(){return pincodes;} public void setPincodes(List<String> v){pincodes=v;} public BigDecimal getFee(){return fee;} public void setFee(BigDecimal v){fee=v;} public BigDecimal getMinimumOrder(){return minimumOrder;} public void setMinimumOrder(BigDecimal v){minimumOrder=v;} public int getEstimatedDays(){return estimatedDays;} public void setEstimatedDays(int v){estimatedDays=v;} public boolean isActive(){return active;} public void setActive(boolean v){active=v;} }
