package com.aristocat.webauthndemo.webauthn.model;

import com.aristocat.webauthndemo.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserAuthenticatorRepository extends CrudRepository<UserAuthenticator, String> {

	List<UserAuthenticator> findUserAuthenticatorByUser(User user);

	long deleteByIdAndUser(String id, User user);

}
