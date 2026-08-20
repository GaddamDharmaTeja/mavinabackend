package com.maviinamane.order;
import com.maviinamane.product.Product;
import com.maviinamane.product.ProductRepository;
import com.maviinamane.marketplace.DeliveryService;
import com.maviinamane.marketplace.NotificationService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "${app.cors-origin:http://localhost:3000}")
public class OrderController {

    private final OrderRepository repository;
    private final ProductRepository products;
    private final DeliveryService delivery;
    private final NotificationService notifications;

    public OrderController(
            OrderRepository repository,
            ProductRepository products, DeliveryService delivery, NotificationService notifications) {
        this.repository = repository;
        this.products = products;
        this.delivery = delivery;
        this.notifications = notifications;
    }

    @GetMapping("/{orderNumber}")
    public Order one(@PathVariable String orderNumber) {
        return repository.findByOrderNumberIgnoreCase(orderNumber)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found"));
    }

    @GetMapping("/mine")
    public List<Order> mine(Authentication authentication) {
        return repository.findByEmailIgnoreCaseOrderByCreatedAtDesc(authentication.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public synchronized Order create(@RequestBody Order order) {

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order must contain items");
        }

        BigDecimal total = BigDecimal.ZERO;

        for (Order.OrderItem item : order.getItems()) {

            if (item.getProductId() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Product is required");
            }

            Product product = products.findById(item.getProductId())
                    .filter(Product::isActive)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Product is unavailable"));

            if (item.getQuantity() < 1 ||
                    product.getStockQuantity() < item.getQuantity()) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Insufficient stock for " + product.getName());
            }

            product.setStockQuantity(
                    product.getStockQuantity() - item.getQuantity());

            products.save(product);

            item.setName(product.getName());
            item.setPrice(product.getPrice());

            total = total.add(
                    product.getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        order.setTotal(total);
        if (order.getPincode() != null && !order.getPincode().isBlank()) { var quote=delivery.quote(order.getPincode(),total); order.setDeliveryZone(quote.zone()); order.setDeliveryFee(quote.fee()); order.setEstimatedDeliveryDays(quote.days()); total=total.add(quote.fee()); }
        order.setTotal(total);
        order.setOrderNumber(
                "ORD" + ThreadLocalRandom.current().nextInt(100000, 999999));

        order.setStatus("PENDING");
        order.setPaymentStatus("PENDING");
        order.setCreatedAt(Instant.now());
        Order.TimelineEvent event=new Order.TimelineEvent(); event.setStatus("PENDING"); event.setNote("Order placed"); order.getTimeline().add(event);

        Order saved = repository.save(order);
        notifications.send(saved.getEmail(), "ORDER", "Order placed", "Your order #" + saved.getOrderNumber() + " has been placed and will be prepared shortly.");
        return saved;
    }

    @PostMapping("/{orderNumber}/payment")
    public Order completePayment(
            @PathVariable String orderNumber,
            @RequestBody PaymentRequest payment) {

        Order order = one(orderNumber);

        if (!"COD".equals(payment.method())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Use the secure payment verification endpoint for online payments");
        }

        order.setPaymentMethod("COD");
        order.setPaymentStatus("PENDING");
        order.setTransactionId("COD");

        Order saved = repository.save(order);
        notifications.send(saved.getEmail(), "PAYMENT", "Cash on delivery selected", "You will pay for order #" + saved.getOrderNumber() + " when it is delivered.");
        return saved;
    }

    public record PaymentRequest(String method) {
    }
}
