package com.aristocat.showsquare.models;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface LoginCodeRepository extends CrudRepository<LoginCode, UUID> {

}
