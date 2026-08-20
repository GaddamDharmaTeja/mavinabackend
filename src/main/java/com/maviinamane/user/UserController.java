package com.maviinamane.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.maviinamane.auth.JwtService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins="${app.cors-origin:http://localhost:3000}")
public class UserController {
  private final UserDataRepository users; private final PasswordEncoder encoder; private final JwtService jwt;
  public UserController(UserDataRepository users,PasswordEncoder encoder,JwtService jwt){this.users=users;this.encoder=encoder;this.jwt=jwt;}
  @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
  public UserResponse register(@Valid @RequestBody Register request){
    String email=request.email().trim().toLowerCase();
    if(users.findByEmailIgnoreCase(email).isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT,"An account already exists for this email");
    UserData user=new UserData(); user.setName(request.name().trim()); user.setEmail(email); user.setPhone(request.phone().trim()); user.setPasswordHash(encoder.encode(request.password()));
    return UserResponse.from(users.save(user), jwt);
  }
  @PostMapping("/login")
  public UserResponse login(@Valid @RequestBody Login request){
    UserData user=users.findByEmailIgnoreCase(request.email().trim()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password"));
    if(!encoder.matches(request.password(),user.getPasswordHash())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password");
    return UserResponse.from(user, jwt);
  }
  public record Register(@NotBlank String name,@Email @NotBlank String email,@NotBlank String phone,@Size(min=6) String password){}
  public record Login(@Email @NotBlank String email,@NotBlank String password){}
  public record UserResponse(String id,String name,String email,String phone,String token){static UserResponse from(UserData u,JwtService jwt){return new UserResponse(u.getId(),u.getName(),u.getEmail(),u.getPhone(),jwt.create(u.getEmail(),"CUSTOMER"));}}
}
