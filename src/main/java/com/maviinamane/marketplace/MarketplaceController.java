package com.maviinamane.marketplace;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController @RequestMapping("/api") @CrossOrigin(origins="${app.cors-origin:http://localhost:3000}")
public class MarketplaceController {
 private final FarmRepository farms; private final SellerApplicationRepository applications; private final DeliveryZoneRepository zones; private final NotificationRepository notifications; private final DeliveryService delivery;
 public MarketplaceController(FarmRepository farms,SellerApplicationRepository applications,DeliveryZoneRepository zones,NotificationRepository notifications,DeliveryService delivery){this.farms=farms;this.applications=applications;this.zones=zones;this.notifications=notifications;this.delivery=delivery;}
 @GetMapping("/farms") public List<Farm> farms(){return farms.findAll().stream().filter(f->"ACTIVE".equals(f.getStatus())).toList();}
 @PostMapping("/seller-applications") @ResponseStatus(HttpStatus.CREATED) public SellerApplication apply(@RequestBody SellerApplication item){item.setId(null);item.setStatus("PENDING");return applications.save(item);}
 @GetMapping("/delivery-zones/quote") public DeliveryQuote quote(@RequestParam String pincode,@RequestParam(required=false,defaultValue="0") java.math.BigDecimal subtotal){var quote=delivery.quote(pincode,subtotal);return new DeliveryQuote(quote.zone(),quote.fee(),quote.days());}
 @GetMapping("/admin/farms") public List<Farm> allFarms(){return farms.findAll();}
 @PostMapping("/admin/farms") public Farm createFarm(@RequestBody Farm item){return farms.save(item);}
 @PutMapping("/admin/farms/{id}") public Farm updateFarm(@PathVariable String id,@RequestBody Farm item){item.setId(id);return farms.save(item);}
 @GetMapping("/admin/seller-applications") public List<SellerApplication> applications(){return applications.findAll();}
 @PatchMapping("/admin/seller-applications/{id}") public SellerApplication review(@PathVariable String id,@RequestBody Review review){SellerApplication application=applications.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Application not found"));application.setStatus(review.status());application.setReviewNote(review.note());if("APPROVED".equals(review.status())){Farm farm=new Farm();farm.setName(application.getFarmName());farm.setManagerName(application.getApplicantName());farm.setManagerEmail(application.getEmail());farm.setPhone(application.getPhone());farm.setAddress(application.getAddress());farms.save(farm);}Notification notification=new Notification();notification.setRecipientEmail(application.getEmail());notification.setType("SELLER_APPLICATION");notification.setTitle("Farm application "+review.status().toLowerCase());notification.setMessage(review.note()==null||review.note().isBlank()?"Your application for "+application.getFarmName()+" was "+review.status().toLowerCase()+".":review.note());notifications.save(notification);return applications.save(application);}
 @GetMapping("/admin/delivery-zones") public List<DeliveryZone> zones(){return zones.findAll();}
 @PostMapping("/admin/delivery-zones") public DeliveryZone createZone(@RequestBody DeliveryZone item){return zones.save(item);}
 @PutMapping("/admin/delivery-zones/{id}") public DeliveryZone updateZone(@PathVariable String id,@RequestBody DeliveryZone item){item.setId(id);return zones.save(item);}
 @GetMapping("/notifications") public List<Notification> notifications(@RequestParam String email){return notifications.findByRecipientEmailOrderByCreatedAtDesc(email);}
 @PatchMapping("/notifications/{id}/read") public Notification read(@PathVariable String id){Notification item=notifications.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Notification not found"));item.setRead(true);return notifications.save(item);}
 public record Review(String status,String note){} public record DeliveryQuote(String zone,java.math.BigDecimal fee,int estimatedDays){}
}
