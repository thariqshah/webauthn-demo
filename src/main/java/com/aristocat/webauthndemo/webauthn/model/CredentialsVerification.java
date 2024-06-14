package com.aristocat.webauthndemo.webauthn.model;

public record CredentialsVerification(String id, Response response) {
	public record Response(String authenticatorData, String clientDataJSON, String signature) {
	}
}
