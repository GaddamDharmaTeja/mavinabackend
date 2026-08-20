package com.maviinamane.user;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserDataRepository extends MongoRepository<UserData,String>{
  Optional<UserData> findByEmailIgnoreCase(String email);
}
