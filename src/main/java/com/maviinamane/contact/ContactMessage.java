package com.maviinamane.contact;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document("contactMessages") public class ContactMessage { @Id private String id; private String name; private String email; private String message; private String status="NEW"; private Instant createdAt=Instant.now(); public String getId(){return id;} public void setId(String v){id=v;} public String getName(){return name;} public void setName(String v){name=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getMessage(){return message;} public void setMessage(String v){message=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;} }
