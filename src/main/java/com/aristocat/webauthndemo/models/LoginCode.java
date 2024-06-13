package com.aristocat.webauthndemo.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
public class LoginCode {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	private User user;

	private LocalDateTime expirationTime;

	public LoginCode() {
	}

	public LoginCode(User user) {
		this.user = user;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(LocalDateTime expirationTime) {
		this.expirationTime = expirationTime;
	}

}
