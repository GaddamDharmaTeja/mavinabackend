package com.maviinamane.marketplace;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
@Service public class DeliveryService { private final DeliveryZoneRepository zones; public DeliveryService(DeliveryZoneRepository zones){this.zones=zones;} public Quote quote(String pincode,BigDecimal subtotal){DeliveryZone zone=zones.findFirstByPincodesContainingAndActiveTrue(pincode).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"We do not deliver to this pincode yet"));return new Quote(zone.getName(),subtotal.compareTo(zone.getMinimumOrder())>=0?BigDecimal.ZERO:zone.getFee(),zone.getEstimatedDays());} public record Quote(String zone,BigDecimal fee,int days){} }
