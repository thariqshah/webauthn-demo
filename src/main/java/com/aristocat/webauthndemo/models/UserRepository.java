package com.aristocat.webauthndemo.models;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
	Optional<User> findUserByEmail(String email);
	boolean existsUserByEmail(String email);
	boolean existsUserByUsername(String username);

}
