package com.aristocat.webauthndemo.controller;


import com.aristocat.webauthndemo.models.LoginCode;
import com.aristocat.webauthndemo.models.LoginCodeRepository;
import com.aristocat.webauthndemo.models.User;
import com.aristocat.webauthndemo.models.UserRepository;
import com.aristocat.webauthndemo.models.webauthn.*;
import com.aristocat.webauthndemo.security.SecurityConfiguration;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;
import java.util.stream.Collectors;


@SessionAttributes("challenge")
@Slf4j
@Controller
public class WebController {

    private final UserRepository userRepository;

    private final LoginCodeRepository loginCodeRepository;

    private final SecurityConfiguration securityConfiguration;

    private final AuthenticatorService authenticatorService;

    private final UserAuthenticatorRepository authenticatorRepository;

    @Value("${app.host}")
    private String host;

    public WebController(UserRepository userRepository, LoginCodeRepository loginCodeRepository, SecurityConfiguration securityConfiguration, AuthenticatorService authenticatorService, UserAuthenticatorRepository authenticatorRepository) {
        this.userRepository = userRepository;
        this.loginCodeRepository = loginCodeRepository;
        this.securityConfiguration = securityConfiguration;
        this.authenticatorService = authenticatorService;
        this.authenticatorRepository = authenticatorRepository;
    }

    @GetMapping
    public String landingPage(Model model) {
        model.addAttribute("challenge", UUID.randomUUID().toString());
        return "index";
    }


    @PostMapping("/user/register")
    public String registerAction(@Validated User user, RedirectAttributes redirectAttributes)  {
        var savedUser = userRepository.save(user);
        redirectAttributes.addFlashAttribute("alert", """
        Hello %s,
        You have been registered. Login with email and add passkeys""".formatted(user.getUsername())
        );
        return "redirect:/";
    }

    @PostMapping("/user/login")
    public String requestLoginEmail(String email, RedirectAttributes redirectAttributes,Model model) {
        var user = userRepository.findUserByEmail(email);
        if (user.isPresent()) {
            var newCode = loginCodeRepository.save(new LoginCode(user.get()));
            model.addAttribute("magicLink", host+"/user/login?code="+newCode.getId());
        }
        else {
            redirectAttributes.addFlashAttribute("alert",
                    "You have requested a login code for [%s]. User does not exist, please register first!"
                            .formatted(email));
        }
        return "index";
    }

    @GetMapping("/user/login")
    @Transactional
    public String login(@RequestParam String code, RedirectAttributes redirectAttributes, HttpServletRequest request,
                        HttpServletResponse response) {
        var loginCode = loginCodeRepository.findById(UUID.fromString(code));
        if (loginCode.isPresent()) {
            var user = loginCode.get().getUser();
            securityConfiguration.login(user);
            loginCodeRepository.delete(loginCode.get());
            return "redirect:/account";
        }
        else {
            redirectAttributes.addFlashAttribute("alert", "Wrong code, try again!");
            return "redirect:/";
        }
    }

    @PostMapping("/passkey/register")
    public String register(@RequestBody CredentialsRegistration credentials, @AuthenticationPrincipal User user,
                           RedirectAttributes redirectAttributes,Model model) {
        authenticatorService.saveCredentials(credentials, user);
        redirectAttributes.addFlashAttribute("alert", "You have registered a new passkey!");
        model.addAttribute("host", UUID.randomUUID().toString());
        return "redirect:/account";
    }

    @PostMapping("/passkey/login")
    public String login(@RequestBody CredentialsVerification verification, SessionStatus sessionStatus,
                        @SessionAttribute("challenge") String challenge, HttpServletRequest request, HttpServletResponse response) {
        var user = authenticatorService.authenticate(verification, challenge);
        securityConfiguration.login(user);
        sessionStatus.setComplete();
        return "redirect:/account";
    }

    @PostMapping("/passkey/delete")
    public String login( @RequestParam("credential-id") String credentialId, @AuthenticationPrincipal User user,
                        RedirectAttributes redirectAttributes) {
        System.out.println("DELETING " + credentialId);
        if (authenticatorService.deleteCredential(user, credentialId)) {
            redirectAttributes.addFlashAttribute("alert", "Passkey deleted.");
        }
        return "redirect:/account";
    }

    @GetMapping("/account")
    public String accountPage(@AuthenticationPrincipal User user, Model model) {
        var authenticators = authenticatorRepository.findUserAuthenticatorByUser(user);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("authenticators", authenticators);
        model.addAttribute("passkeyList", authenticators.stream().map(UserAuthenticator::getCredentialsName).collect(Collectors.toSet()));
        return "account";
    }


}
