package com.maviinamane.contact;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/api/contact-messages") @CrossOrigin(origins="${app.cors-origin:http://localhost:3000}") public class ContactMessageController { private final ContactMessageRepository messages; public ContactMessageController(ContactMessageRepository messages){this.messages=messages;} @PostMapping @ResponseStatus(HttpStatus.CREATED) public ContactMessage create(@RequestBody ContactMessage message){if(message.getName()==null||message.getName().isBlank()||message.getEmail()==null||message.getEmail().isBlank()||message.getMessage()==null||message.getMessage().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Name, email and message are required"); message.setId(null); message.setStatus("NEW"); message.setCreatedAt(Instant.now()); return messages.save(message);} }
