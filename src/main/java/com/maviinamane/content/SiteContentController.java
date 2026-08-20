package com.maviinamane.content;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestController 
@RequestMapping("/api/content")
 @CrossOrigin(origins="${app.cors-origin:http://localhost:3000}") 
 public class SiteContentController {
    private final SiteContentRepository repository;
    public SiteContentController(SiteContentRepository repository){
        
        this.repository=repository;
        
        }
    @GetMapping("/{key}")
    public SiteContent get(@PathVariable String key){
        return repository.findByKey(key).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Content not found"));
        }
        }
