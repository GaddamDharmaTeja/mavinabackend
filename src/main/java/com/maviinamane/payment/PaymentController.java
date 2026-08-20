package com.maviinamane.payment;

import com.maviinamane.order.OrderRepository;
import com.maviinamane.marketplace.NotificationService;
import com.razorpay.RazorpayClient;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "${app.cors-origin:http://localhost:3000}")
public class PaymentController {
  private final OrderRepository orders;
  private final String keyId;
  private final String keySecret;
  private final PaymentRecordRepository records;
  private final NotificationService notifications;

  public PaymentController(OrderRepository orders, PaymentRecordRepository records, NotificationService notifications, @Value("${razorpay.key-id}") String keyId, @Value("${razorpay.key-secret}") String keySecret) { this.orders = orders; this.records = records; this.notifications = notifications; this.keyId = keyId; this.keySecret = keySecret; }

  @GetMapping("/config") public Config config() { return new Config(keyId); }

  @PostMapping("/create-order") public GatewayOrder createOrder(@RequestBody CreateRequest request) {
    requireConfigured();
    if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) badRequest("Invalid payment amount");
    try {
      JSONObject data = new JSONObject();
      data.put("amount", request.amount().multiply(BigDecimal.valueOf(100)).longValueExact());
      data.put("currency", "INR");
      data.put("receipt", request.orderNumber() == null ? "checkout" : request.orderNumber());
      com.razorpay.Order created = new RazorpayClient(keyId, keySecret).orders.create(data);
      PaymentRecord record = new PaymentRecord();
      record.setOrderNumber(request.orderNumber()); record.setMethod("RAZORPAY"); record.setStatus("CREATED");
      record.setGatewayOrderId(created.get("id").toString()); record.setGatewayState("CREATED"); record.setAmount(request.amount());
      orders.findByOrderNumberIgnoreCase(request.orderNumber()).ifPresent(order -> { record.setCustomerEmail(order.getEmail()); });
      records.save(record);
      return new GatewayOrder(created.get("id").toString(), created.get("amount"), created.get("currency"));
    } catch (Exception exception) { throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not start payment", exception); }
  }

  @PostMapping("/verify") public com.maviinamane.order.Order verify(@RequestBody VerifyRequest request) {
    requireConfigured();
    if (request.razorpayOrderId() == null || request.razorpayPaymentId() == null || request.razorpaySignature() == null) badRequest("Incomplete payment response");
    if (!signatureFor(request.razorpayOrderId() + "|" + request.razorpayPaymentId()).equals(request.razorpaySignature())) badRequest("Payment signature could not be verified");
    com.maviinamane.order.Order order = orders.findByOrderNumberIgnoreCase(request.orderNumber()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    order.setPaymentMethod("RAZORPAY"); order.setPaymentStatus("PAID"); order.setTransactionId(request.razorpayPaymentId());
    order = orders.save(order);
    PaymentRecord record = new PaymentRecord();
    record.setOrderNumber(order.getOrderNumber()); record.setCustomerEmail(order.getEmail()); record.setMethod("RAZORPAY"); record.setStatus("PAID");
    record.setGatewayOrderId(request.razorpayOrderId()); record.setTransactionReference(request.razorpayPaymentId()); record.setGatewayState("CAPTURED"); record.setAmount(order.getTotal());
    records.save(record);
    notifications.send(order.getEmail(), "PAYMENT", "Payment received", "Payment for order #" + order.getOrderNumber() + " was completed.");
    return order;
  }

  @GetMapping("/records/{orderNumber}") public java.util.List<PaymentRecord> history(@PathVariable String orderNumber) { return records.findByOrderNumberOrderByCreatedAtDesc(orderNumber); }

  private void requireConfigured() { if (keyId.isBlank() || keySecret.isBlank()) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Online payments are not configured"); }
  private void badRequest(String message) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message); }
  private String signatureFor(String payload) { try { Mac mac = Mac.getInstance("HmacSHA256"); mac.init(new SecretKeySpec(keySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256")); return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8))); } catch (Exception exception) { throw new IllegalStateException("Unable to verify payment", exception); } }

  public record Config(String keyId) { }
  public record CreateRequest(BigDecimal amount, String orderNumber) { }
  public record GatewayOrder(String id, Object amount, Object currency) { }
  public record VerifyRequest(String orderNumber, String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) { }
}
