package com.maviinamane.order;

import com.maviinamane.product.Product;
import com.maviinamane.product.ProductRepository;
import com.maviinamane.marketplace.DeliveryService;
import com.maviinamane.marketplace.NotificationService;
import com.maviinamane.delivery.DeliveryChargeService;

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
    private final DeliveryChargeService deliveryCharges;

    public OrderController(
            OrderRepository repository,
            ProductRepository products,
            DeliveryService delivery,
            NotificationService notifications,
            DeliveryChargeService deliveryCharges) {

        this.repository = repository;
        this.products = products;
        this.delivery = delivery;
        this.notifications = notifications;
        this.deliveryCharges = deliveryCharges;
    }


    /* =========================================================
       GET SINGLE ORDER
       ========================================================= */

    @GetMapping("/{orderNumber}")
    public Order one(
            @PathVariable String orderNumber) {

        String value = orderNumber == null
                ? ""
                : orderNumber.trim();

        if (value.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order number is required");
        }

        return repository
                .findByOrderNumberIgnoreCase(value)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Order not found"));
    }


    /* =========================================================
       GET MY ORDERS
       ========================================================= */

    @GetMapping("/mine")
    public List<Order> mine(
            Authentication authentication) {

        if (authentication == null ||
                authentication.getName() == null ||
                authentication.getName().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required");
        }

        return repository
                .findByEmailIgnoreCaseOrderByCreatedAtDesc(
                        authentication.getName());
    }


    /* =========================================================
       CREATE ORDER
       ========================================================= */

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public synchronized Order create(
            @RequestBody Order order) {

        if (order == null ||
                order.getItems() == null ||
                order.getItems().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order must contain items");
        }

        BigDecimal total = BigDecimal.ZERO;


        /* -----------------------------------------------------
           PRODUCTS + STOCK
           ----------------------------------------------------- */

        for (Order.OrderItem item :
                order.getItems()) {

            if (item.getProductId() == null) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Product is required");
            }

            Product product =
                    products.findById(
                            item.getProductId())
                            .filter(Product::isActive)
                            .orElseThrow(() ->
                                    new ResponseStatusException(
                                            HttpStatus.BAD_REQUEST,
                                            "Product is unavailable"));


            if (item.getQuantity() < 1 ||
                    product.getStockQuantity()
                            < item.getQuantity()) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Insufficient stock for "
                                + product.getName());
            }


            product.setStockQuantity(
                    product.getStockQuantity()
                            - item.getQuantity());

            products.save(product);


            item.setName(
                    product.getName());

            item.setPrice(
                    product.getPrice());


            total = total.add(
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            item.getQuantity())));
        }


        /* -----------------------------------------------------
           PAYMENT
           ----------------------------------------------------- */

        if (order.getPaymentMethod() != null &&
                !"COD".equalsIgnoreCase(
                        order.getPaymentMethod())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only Cash on Delivery is supported");
        }


        /* -----------------------------------------------------
           DISTANCE
           ----------------------------------------------------- */

        BigDecimal distanceKm =
                order.getDistanceKm();

        if (distanceKm == null) {
            distanceKm = BigDecimal.ZERO;
        }


        /* -----------------------------------------------------
           WEIGHT
           ----------------------------------------------------- */

        BigDecimal weightKg =
                BigDecimal.ZERO;


        for (Order.OrderItem item :
                order.getItems()) {

            Product product =
                    products.findById(
                            item.getProductId())
                            .orElseThrow(() ->
                                    new ResponseStatusException(
                                            HttpStatus.BAD_REQUEST,
                                            "Product not found"));


            BigDecimal productWeight =
                    parseWeightKg(
                            product.getWeight());


            weightKg = weightKg.add(
                    productWeight.multiply(
                            BigDecimal.valueOf(
                                    item.getQuantity())));
        }


        /* -----------------------------------------------------
           DELIVERY CHARGES
           ----------------------------------------------------- */

        var charge =
                deliveryCharges.calculate(
                        distanceKm,
                        weightKg);


        order.setDistanceKm(
                distanceKm);

        order.setTotalWeightKg(
                weightKg);

        order.setDistanceCharge(
                charge.distanceCharge());

        order.setWeightCharge(
                charge.weightCharge());

        order.setDeliveryCharge(
                charge.deliveryCharge());

        order.setPaymentMethod(
                "COD");


        /* -----------------------------------------------------
           PINCODE DELIVERY QUOTE
           ----------------------------------------------------- */

        if (order.getPincode() != null &&
                !order.getPincode().isBlank()) {

            var quote =
                    delivery.quote(
                            order.getPincode(),
                            total);

            order.setDeliveryZone(
                    quote.zone());

            order.setDeliveryFee(
                    quote.fee());

            order.setEstimatedDeliveryDays(
                    quote.days());
        }


        /* -----------------------------------------------------
           TOTAL
           ----------------------------------------------------- */

        total = total.add(
                charge.deliveryCharge());

        order.setTotal(total);


        /* -----------------------------------------------------
           ORDER NUMBER
           ----------------------------------------------------- */

        order.setOrderNumber(
                "ORD"
                        + ThreadLocalRandom
                                .current()
                                .nextInt(
                                        100000,
                                        999999));


        /* -----------------------------------------------------
           STATUS
           ----------------------------------------------------- */

        order.setStatus(
                "PENDING");

        order.setPaymentStatus(
                "PENDING");

        order.setCreatedAt(
                Instant.now());


        /* -----------------------------------------------------
           TIMELINE
           ----------------------------------------------------- */

        Order.TimelineEvent event =
                new Order.TimelineEvent();

        event.setStatus(
                "PENDING");

        event.setNote(
                "Order placed");


        if (order.getTimeline() == null) {
            // Only keep this if your Order model allows
            // setTimeline(). Otherwise remove this block.
            order.setTimeline(
                    new java.util.ArrayList<>());
        }


        order.getTimeline()
                .add(event);


        /* -----------------------------------------------------
           SAVE
           ----------------------------------------------------- */

        Order saved =
                repository.save(order);


        /* -----------------------------------------------------
           NOTIFICATION
           ----------------------------------------------------- */

        notifications.send(
                saved.getEmail(),
                "ORDER",
                "Order placed",
                "Your order #"
                        + saved.getOrderNumber()
                        + " has been placed and will be prepared shortly.");


        return saved;
    }


    /* =========================================================
       WEIGHT PARSER
       ========================================================= */

    private BigDecimal parseWeightKg(
            String value) {

        if (value == null ||
                value.isBlank()) {

            return BigDecimal.ZERO;
        }

        try {

            String number =
                    value.trim()
                            .replace(",", ".")
                            .replaceAll(
                                    "[^0-9.]+",
                                    "");

            if (number.isBlank()) {
                return BigDecimal.ZERO;
            }

            BigDecimal parsed =
                    new BigDecimal(number);


            String lower =
                    value.toLowerCase();


            if (lower.contains("g") &&
                    !lower.contains("kg")) {

                return parsed.divide(
                        BigDecimal.valueOf(1000));
            }


            return parsed;

        } catch (NumberFormatException ex) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid product weight");
        }
    }


    /* =========================================================
       COMPLETE PAYMENT
       ========================================================= */

    @PostMapping("/{orderNumber}/payment")
    public Order completePayment(
            @PathVariable String orderNumber,
            @RequestBody PaymentRequest payment) {

        Order order =
                one(orderNumber);


        if (payment == null ||
                payment.method() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment method is required");
        }


        if (!"COD".equalsIgnoreCase(
                payment.method())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Use the secure payment verification endpoint for online payments");
        }


        order.setPaymentMethod(
                "COD");

        order.setPaymentStatus(
                "PENDING");

        order.setTransactionId(
                "COD");


        Order saved =
                repository.save(order);


        notifications.send(
                saved.getEmail(),
                "PAYMENT",
                "Cash on delivery selected",
                "You will pay for order #"
                        + saved.getOrderNumber()
                        + " when it is delivered.");


        return saved;
    }


    /* =========================================================
       PAYMENT REQUEST
       ========================================================= */

    public record PaymentRequest(
            String method) {
    }
}