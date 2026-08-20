package com.maviinamane.marketplace;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document("seller_applications") public class SellerApplication {
 @Id private String id; private String farmName,applicantName,email,phone,address,products,documentUrl,status="PENDING",reviewNote; private Instant createdAt=Instant.now();
 public String getId(){return id;} public void setId(String v){id=v;} public String getFarmName(){return farmName;} public void setFarmName(String v){farmName=v;} public String getApplicantName(){return applicantName;} public void setApplicantName(String v){applicantName=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public String getAddress(){return address;} public void setAddress(String v){address=v;} public String getProducts(){return products;} public void setProducts(String v){products=v;} public String getDocumentUrl(){return documentUrl;} public void setDocumentUrl(String v){documentUrl=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;} public String getReviewNote(){return reviewNote;} public void setReviewNote(String v){reviewNote=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
}
