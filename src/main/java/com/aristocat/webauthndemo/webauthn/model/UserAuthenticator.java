package com.aristocat.webauthndemo.webauthn.model;

import com.aristocat.webauthndemo.models.User;
import jakarta.persistence.*;

@Entity
@Table(name = "authenticator")
public class UserAuthenticator {

	@Id
	private String id;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	private User user;

	private String credentialsName;

	private byte[] attestationObject;

	public UserAuthenticator() {
	}

	public UserAuthenticator(String id, User user, String credentialsName, byte[] attestationObject) {
		this.id = id;
		this.user = user;
		this.credentialsName = credentialsName;
		this.attestationObject = attestationObject;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getCredentialsName() {
		return credentialsName;
	}

	public void setCredentialsName(String credentialsName) {
		this.credentialsName = credentialsName;
	}

	public byte[] attestationObject() {
		return attestationObject;
	}

	public void setAttestationObject(byte[] attestationStatement) {
		this.attestationObject = attestationStatement;
	}

}
