package com.maviinamane.payment;

import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("payment_records")
public class PaymentRecord {
  @Id private String id;
  private String orderNumber, customerEmail, method, status, transactionReference, gatewayOrderId, gatewayState;
  private BigDecimal amount;
  private Instant createdAt = Instant.now();
  private Instant updatedAt = Instant.now();
  public String getId(){return id;} public void setId(String value){id=value;}
  public String getOrderNumber(){return orderNumber;} public void setOrderNumber(String value){orderNumber=value;}
  public String getCustomerEmail(){return customerEmail;} public void setCustomerEmail(String value){customerEmail=value;}
  public String getMethod(){return method;} public void setMethod(String value){method=value;}
  public String getStatus(){return status;} public void setStatus(String value){status=value;}
  public String getTransactionReference(){return transactionReference;} public void setTransactionReference(String value){transactionReference=value;}
  public String getGatewayOrderId(){return gatewayOrderId;} public void setGatewayOrderId(String value){gatewayOrderId=value;}
  public String getGatewayState(){return gatewayState;} public void setGatewayState(String value){gatewayState=value;}
  public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal value){amount=value;}
  public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant value){createdAt=value;}
  public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant value){updatedAt=value;}
}
