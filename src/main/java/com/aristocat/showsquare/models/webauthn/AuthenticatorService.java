package com.aristocat.showsquare.models.webauthn;

import com.aristocat.showsquare.models.User;
import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.authenticator.AuthenticatorImpl;
import com.webauthn4j.converter.AttestationObjectConverter;
import com.webauthn4j.converter.exception.DataConversionException;
import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.data.AuthenticationParameters;
import com.webauthn4j.data.AuthenticationRequest;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.validator.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Base64;

@Component
public class AuthenticatorService {

	@Value("${app.host}")
	private String host;

	private final UserAuthenticatorRepository repository;

	private final WebAuthnManager webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager();


	private final AttestationObjectConverter attestationConverter = new AttestationObjectConverter(
			new ObjectConverter());

	public AuthenticatorService(UserAuthenticatorRepository repository) {
		this.repository = repository;
	}

	public void saveCredentials(CredentialsRegistration registration, User user) {
		var attestationObject = Base64.getUrlDecoder()
			.decode(registration.credentials().response().attestationObject());
		var serializedAuthenticator = new UserAuthenticator(registration.credentials().id(), user, registration.name(),
				attestationObject);
		repository.save(serializedAuthenticator);
	}

	public User authenticate(CredentialsVerification verification, String challenge) throws AuthenticationException {
		var userAuthenticator = repository.findById(verification.id())
			.orElseThrow(() -> new BadCredentialsException("Unknown credentials"));
		var attestation = attestationConverter.convert(userAuthenticator.attestationObject());

		var authenticator = new AuthenticatorImpl(attestation.getAuthenticatorData().getAttestedCredentialData(),
				attestation.getAttestationStatement(), attestation.getAuthenticatorData().getSignCount());

		var clientDataJSON = Base64.getUrlDecoder().decode(verification.response().clientDataJSON());
		var authenticatorData = Base64.getUrlDecoder().decode(verification.response().authenticatorData());
		var signature = Base64.getUrlDecoder().decode(verification.response().signature());
		var b64Challenge = Base64.getUrlEncoder().encodeToString(challenge.getBytes());

		var serverProperty = new ServerProperty(getOrigin(), getRpId(), new DefaultChallenge(b64Challenge), null);

		var authenticationRequest = new AuthenticationRequest(verification.id().getBytes(), authenticatorData,
				clientDataJSON, signature);
		var authenticationParameters = new AuthenticationParameters(serverProperty, authenticator, false);

		try {
			webAuthnManager.validate(authenticationRequest, authenticationParameters);

			// TODO: required per spec, to allow for authenticator clone detection, see:
			// https://www.w3.org/TR/webauthn-3/#sctn-sign-counter
			// userAuthenticator.setCounter(authenticationData.getAuthenticatorData().getSignCount());
		}
		catch (DataConversionException e) {
			// If you would like to handle WebAuthn data structure parse error, please
			// catch DataConversionException
			throw e;
		}
		catch (ValidationException e) {
			// If you would like to handle WebAuthn data validation error, please catch
			// ValidationException
			throw e;
		}
		return userAuthenticator.getUser();
	}

	@Transactional
	public boolean deleteCredential(User user, String credentialId) {
		return repository.deleteByIdAndUser(credentialId, user) > 0;
	}

	private String getRpId() {
		return UriComponentsBuilder.fromHttpUrl(host).build().getHost();
	}

	private Origin getOrigin() {
		var origin = UriComponentsBuilder.fromHttpUrl(host)
			.replacePath(null)
			.toUriString();
		return new Origin(origin);
	}

}
