package com.maviinamane.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("userData")
public class UserData {
  @Id private String id;
  private String name;
  private String email;
  private String phone;
  private String passwordHash;
  public String getId(){return id;} public void setId(String v){id=v;}
  public String getName(){return name;} public void setName(String v){name=v;}
  public String getEmail(){return email;} public void setEmail(String v){email=v;}
  public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
  public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){passwordHash=v;}
}
