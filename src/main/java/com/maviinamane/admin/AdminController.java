package com.maviinamane.admin;

import com.maviinamane.category.*;
import com.maviinamane.contact.*;
import com.maviinamane.content.*;
import com.maviinamane.order.*;
import com.maviinamane.product.*;
import com.maviinamane.user.UserDataRepository;
import com.maviinamane.marketplace.NotificationService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController @RequestMapping("/api/admin") public class AdminController {
  private final ProductRepository products; private final CategoryRepository categories; private final OrderRepository orders; private final SiteContentRepository content; private final UserDataRepository users; private final NotificationService notifications; private final ContactMessageRepository contactMessages;
  public AdminController(ProductRepository products,CategoryRepository categories,OrderRepository orders,SiteContentRepository content,UserDataRepository users,NotificationService notifications,ContactMessageRepository contactMessages){this.products=products;this.categories=categories;this.orders=orders;this.content=content;this.users=users;this.notifications=notifications;this.contactMessages=contactMessages;}
  @GetMapping("/dashboard") public Dashboard dashboard(){List<Order> all=orders.findAllByOrderByCreatedAtDesc();return new Dashboard(products.count(),categories.count(),all.size(),all.stream().filter(o->"PENDING".equals(o.getStatus())||"PACKED".equals(o.getStatus())).count(),all.stream().map(Order::getTotal).filter(java.util.Objects::nonNull).reduce(java.math.BigDecimal.ZERO,java.math.BigDecimal::add),users.count());}
  @GetMapping("/analytics") public Analytics analytics(){List<Order> all=orders.findAllByOrderByCreatedAtDesc();java.util.Map<String,Long> statuses=all.stream().collect(java.util.stream.Collectors.groupingBy(o->o.getStatus()==null?"PENDING":o.getStatus(),java.util.stream.Collectors.counting()));java.util.Map<String,Long> sold=all.stream().flatMap(o->o.getItems()==null?java.util.stream.Stream.empty():o.getItems().stream()).collect(java.util.stream.Collectors.groupingBy(Order.OrderItem::getProductId,java.util.stream.Collectors.summingLong(Order.OrderItem::getQuantity)));return new Analytics(statuses,sold,all.stream().filter(o->"PAID".equals(o.getPaymentStatus())).map(Order::getTotal).filter(java.util.Objects::nonNull).reduce(java.math.BigDecimal.ZERO,java.math.BigDecimal::add));}
  @GetMapping("/customers") public List<Customer> customers(){return users.findAll().stream().map(user->new Customer(user.getId(),user.getName(),user.getEmail(),user.getPhone())).toList();}
  @GetMapping("/contact-messages") public List<ContactMessage> contactMessages(){return contactMessages.findAllByOrderByCreatedAtDesc();}
  @PatchMapping("/contact-messages/{id}") public ContactMessage updateContactMessage(@PathVariable String id,@RequestBody ContactMessageUpdate update){ContactMessage message=contactMessages.findById(id).orElseThrow(()->missing("Contact message"));if(update.status()!=null) message.setStatus(update.status());return contactMessages.save(message);}
  @GetMapping("/products") public List<Product> products(){return products.findAll();}
  @PostMapping("/products") @ResponseStatus(HttpStatus.CREATED) public Product createProduct(@Valid @RequestBody Product product){if(product.getStockQuantity()<0)bad("Stock cannot be negative");return products.save(product);}
  @PutMapping("/products/{id}") public Product updateProduct(@PathVariable String id,@Valid @RequestBody Product product){if(!products.existsById(id))throw missing("Product");if(product.getStockQuantity()<0)bad("Stock cannot be negative");product.setId(id);return products.save(product);}
  @DeleteMapping("/products/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void archiveProduct(@PathVariable String id){Product product=products.findById(id).orElseThrow(()->missing("Product"));product.setActive(false);products.save(product);}
  @GetMapping("/categories") public List<Category> categories(){return categories.findAll();}
  @PostMapping("/categories") @ResponseStatus(HttpStatus.CREATED) public Category createCategory(@RequestBody Category category){return categories.save(category);}
  @PutMapping("/categories/{id}") public Category updateCategory(@PathVariable String id,@RequestBody Category category){if(!categories.existsById(id))throw missing("Category");category.setId(id);return categories.save(category);}
  @DeleteMapping("/categories/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void archiveCategory(@PathVariable String id){Category category=categories.findById(id).orElseThrow(()->missing("Category"));category.setActive(false);categories.save(category);}
  @GetMapping("/orders") public List<Order> orders(@RequestParam(required=false) String q){return orders.findAllByOrderByCreatedAtDesc().stream().filter(order->q==null||q.isBlank()||order.getOrderNumber().toLowerCase().contains(q.toLowerCase())||(order.getCustomerName()!=null&&order.getCustomerName().toLowerCase().contains(q.toLowerCase()))).toList();}
  @PatchMapping("/orders/{number}") public Order updateOrder(@PathVariable String number,@RequestBody OrderUpdate update){Order order=orders.findByOrderNumberIgnoreCase(number).orElseThrow(()->missing("Order"));if(update.status()!=null){if(!List.of("PENDING","PACKED","SHIPPED","DELIVERED","CANCELLED").contains(update.status()))bad("Invalid order status");order.setStatus(update.status());order.setStatusUpdatedAt(Instant.now());Order.TimelineEvent event=new Order.TimelineEvent();event.setStatus(update.status());event.setNote("Order status updated");order.getTimeline().add(event);notifications.send(order.getEmail(),"ORDER_STATUS","Order "+update.status().toLowerCase(),"Your order #"+order.getOrderNumber()+" is now "+update.status().toLowerCase()+".");}if(update.courier()!=null)order.setCourier(update.courier());if(update.trackingNumber()!=null)order.setTrackingNumber(update.trackingNumber());return orders.save(order);}
  @GetMapping("/content/{key}") public SiteContent getContent(@PathVariable String key){return content.findByKey(key).orElseGet(()->{SiteContent item=new SiteContent();item.setKey(key);return item;});}
  @PutMapping("/content/{key}") public SiteContent saveContent(@PathVariable String key,@RequestBody SiteContent item){item.setKey(key);return content.save(item);}
  private ResponseStatusException missing(String item){return new ResponseStatusException(HttpStatus.NOT_FOUND,item+" not found");} private void bad(String message){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,message);}
  public record Dashboard(long products,long categories,long orders,long openOrders,java.math.BigDecimal revenue,long customers){} public record Analytics(java.util.Map<String,Long> statusCounts,java.util.Map<String,Long> productQuantities,java.math.BigDecimal paidRevenue){} public record Customer(String id,String name,String email,String phone){} public record OrderUpdate(String status,String courier,String trackingNumber){} public record ContactMessageUpdate(String status){}
}
