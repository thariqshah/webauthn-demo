package com.aristocat.showsquare.models;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {

	public Optional<User> findUserByEmail(String email);

	public boolean existsUserByEmail(String email);

}
