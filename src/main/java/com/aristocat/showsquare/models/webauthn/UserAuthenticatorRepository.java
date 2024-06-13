package com.aristocat.showsquare.models.webauthn;

import com.aristocat.showsquare.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserAuthenticatorRepository extends CrudRepository<UserAuthenticator, String> {

	List<UserAuthenticator> findUserAuthenticatorByUser(User user);

	long deleteByIdAndUser(String id, User user);

}
