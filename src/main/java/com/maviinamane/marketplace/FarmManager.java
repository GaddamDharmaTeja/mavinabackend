package com.maviinamane.marketplace;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document("farm_managers") public class FarmManager { @Id private String id; private String farmId,name,email,passwordHash,role="FARM_MANAGER"; public String getId(){return id;} public void setId(String v){id=v;} public String getFarmId(){return farmId;} public void setFarmId(String v){farmId=v;} public String getName(){return name;} public void setName(String v){name=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){passwordHash=v;} public String getRole(){return role;} public void setRole(String v){role=v;} }
