package com.maviinamane.marketplace;
import org.springframework.stereotype.Service;
@Service public class NotificationService { private final NotificationRepository repository; public NotificationService(NotificationRepository repository){this.repository=repository;} public void send(String email,String type,String title,String message){if(email==null||email.isBlank())return;Notification item=new Notification();item.setRecipientEmail(email);item.setType(type);item.setTitle(title);item.setMessage(message);repository.save(item);} }
