# WebAuthn Demo

A Demo of WebAuthn using [webauthn4j](https://github.com/webauthn4j/webauthn4j)

Purely to understand webauthn flow and ceremonies. Not a reference for production environment.

## Issues

- Android devices cannot self-discover keys - it is an android problem.
-  with Android or Windows self key discovery can be fixed with fetching allowedCredentials for the user.
- Works fine with iCloud Keychain, Chrome Webauthn Virtualized env (test purpose only)

## Prerequisites

List any prerequisites or dependencies that users need to have installed before setting up the project.

- Java JDK 21+
- Maven 3.6+ 

## Run Locally

```bash
#!/bin/bash
  ./mvnw spring-boot:build-image &&
  docker run -d -p 8080:8080 webauthn-demo . &&
  if command -v start >/dev/null 2>&1; then
    start "http://localhost:8080"
  elif command -v xdg-open >/dev/null 2>&1; then
    xdg-open "http://localhost:8080"
  elif command -v open >/dev/null 2>&1; then
    open "http://localhost:8080"
```
## Usage

Explain how to use your WebAuthn demo, including any special instructions or setup required.

- Goto live demo
- Register with email and username
- login with email.
- A link will showup in page click the link
- Add passkeys using mac chrome or ios devices
- Logout
- Click on login with passkey!!

## Live

Hosted on fly.io

- [Live](https://webauthn-demo-tq.fly.dev/)

## Resources

- https://www.w3.org/TR/webauthn-2/#sctn-intro
- https://webauthn.me/
- https://github.com/webauthn4j/webauthn4j-spring-security