package com.maviinamane.product;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("products")
public class Product {
  @Id private String id;
  @NotBlank private String name;
  @NotBlank private String variety;
  @NotNull @Positive private BigDecimal price;
  private String weight = "1 Kg";
  private double rating;
  private int reviews;
  private String imageKey;
  private String description;
  private boolean featured;
  private boolean active = true;
  private int stockQuantity;
  private String imageUrl;
  private String farmId;
  private boolean available = true;
  public String getId(){return id;} public void setId(String id){this.id=id;}
  public String getName(){return name;} public void setName(String name){this.name=name;}
  public String getVariety(){return variety;} public void setVariety(String variety){this.variety=variety;}
  public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal price){this.price=price;}
  public String getWeight(){return weight;} public void setWeight(String weight){this.weight=weight;}
  public double getRating(){return rating;} public void setRating(double rating){this.rating=rating;}
  public int getReviews(){return reviews;} public void setReviews(int reviews){this.reviews=reviews;}
  public String getImageKey(){return imageKey;} public void setImageKey(String imageKey){this.imageKey=imageKey;}
  public String getDescription(){return description;} public void setDescription(String description){this.description=description;}
  public boolean isFeatured(){return featured;} public void setFeatured(boolean featured){this.featured=featured;}
  public boolean isActive(){return active;} public void setActive(boolean active){this.active=active;}
  public int getStockQuantity(){return stockQuantity;} public void setStockQuantity(int value){stockQuantity=value;}
  public String getImageUrl(){return imageUrl;} public void setImageUrl(String value){imageUrl=value;}
  public String getFarmId(){return farmId;} public void setFarmId(String value){farmId=value;}
  public boolean isAvailable(){return available;} public void setAvailable(boolean value){available=value;}
}
