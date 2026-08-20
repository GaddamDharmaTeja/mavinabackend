package com.maviinamane.marketplace;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("farms")
public class Farm {
  @Id private String id;
  private String name, description, managerName, managerEmail, phone, address, placeId, imageUrl, status="ACTIVE";
  private double acreage, latitude, longitude;
  public String getId(){return id;} public void setId(String v){id=v;} public String getName(){return name;} public void setName(String v){name=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;} public String getManagerName(){return managerName;} public void setManagerName(String v){managerName=v;} public String getManagerEmail(){return managerEmail;} public void setManagerEmail(String v){managerEmail=v;} public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public String getAddress(){return address;} public void setAddress(String v){address=v;} public String getPlaceId(){return placeId;} public void setPlaceId(String v){placeId=v;} public String getImageUrl(){return imageUrl;} public void setImageUrl(String v){imageUrl=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;} public double getAcreage(){return acreage;} public void setAcreage(double v){acreage=v;} public double getLatitude(){return latitude;} public void setLatitude(double v){latitude=v;} public double getLongitude(){return longitude;} public void setLongitude(double v){longitude=v;}
}
