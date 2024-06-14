package com.aristocat.webauthndemo.webauthn.model;

public record CredentialsRegistration(String name, AuthenticatorCredentials credentials) {

	public record AuthenticatorCredentials(String id, Response response) {
	}

	public record Response(String attestationObject, String clientDataJSON) {
	}
}
