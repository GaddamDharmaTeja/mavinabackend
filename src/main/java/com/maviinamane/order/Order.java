package com.maviinamane.order;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("orders")
public class Order {

    @Id
    private String id;

    private String orderNumber;
    private String customerName;
    private String email;
    private String phone;
    private String address;
    private String pincode;
    private String deliveryZone;
    private BigDecimal deliveryFee = BigDecimal.ZERO;
    private int estimatedDeliveryDays;
    private Double latitude;
    private Double longitude;

    private String status = "PENDING";
    private String paymentStatus = "PENDING";
    private String paymentMethod;
    private String transactionId;

    private String courier;
    private String trackingNumber;
    private Instant statusUpdatedAt;

    private BigDecimal total;
    private Instant createdAt = Instant.now();
    private List<TimelineEvent> timeline = new java.util.ArrayList<>();

    private List<OrderItem> items;

    public static class OrderItem {

        private String productId;
        private String name;
        private int quantity;
        private BigDecimal price;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }
    public static class TimelineEvent { private String status; private String note; private Instant createdAt=Instant.now(); public String getStatus(){return status;} public void setStatus(String v){status=v;} public String getNote(){return note;} public void setNote(String v){note=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;} }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getPincode(){return pincode;} public void setPincode(String v){pincode=v;}
    public String getDeliveryZone(){return deliveryZone;} public void setDeliveryZone(String v){deliveryZone=v;}
    public BigDecimal getDeliveryFee(){return deliveryFee;} public void setDeliveryFee(BigDecimal v){deliveryFee=v;}
    public int getEstimatedDeliveryDays(){return estimatedDeliveryDays;} public void setEstimatedDeliveryDays(int v){estimatedDeliveryDays=v;}
    public Double getLatitude(){return latitude;} public void setLatitude(Double v){latitude=v;}
    public Double getLongitude(){return longitude;} public void setLongitude(Double v){longitude=v;}
    public List<TimelineEvent> getTimeline(){return timeline;} public void setTimeline(List<TimelineEvent> v){timeline=v;}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCourier() {
        return courier;
    }

    public void setCourier(String courier) {
        this.courier = courier;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Instant getStatusUpdatedAt() {
        return statusUpdatedAt;
    }

    public void setStatusUpdatedAt(Instant statusUpdatedAt) {
        this.statusUpdatedAt = statusUpdatedAt;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
