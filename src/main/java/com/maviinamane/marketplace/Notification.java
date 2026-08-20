package com.maviinamane.marketplace;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document("notifications") public class Notification { @Id private String id; private String recipientEmail,title,message,type; private boolean read; private Instant createdAt=Instant.now(); public String getId(){return id;} public void setId(String v){id=v;} public String getRecipientEmail(){return recipientEmail;} public void setRecipientEmail(String v){recipientEmail=v;} public String getTitle(){return title;} public void setTitle(String v){title=v;} public String getMessage(){return message;} public void setMessage(String v){message=v;} public String getType(){return type;} public void setType(String v){type=v;} public boolean isRead(){return read;} public void setRead(boolean v){read=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;} }
