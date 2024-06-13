package com.aristocat.showsquare.security;

import com.aristocat.showsquare.models.User;
import com.aristocat.showsquare.models.UserAuthenticationToken;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.ui.DefaultLogoutPageGeneratingFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    private final HttpServletRequest request;

    private final HttpServletResponse response;



    public SecurityConfiguration(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public void login(User user) {
        var auth = new UserAuthenticationToken(user);
        var newContext = SecurityContextHolder.createEmptyContext();
        newContext.setAuthentication(auth);
        SecurityContextHolder.setContext(newContext);
        securityContextRepository.saveContext(newContext, request, response);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authorize -> {
                    authorize.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll();
                    authorize.requestMatchers("/").permitAll();
                    authorize.requestMatchers("/user/login").permitAll();
                    authorize.requestMatchers("/user/register").permitAll();
                    authorize.requestMatchers("/passkey/login").permitAll();
                    authorize.requestMatchers("/login-mail").permitAll(); // TODO: consistent
                    authorize.requestMatchers("/style.css").permitAll();
                    authorize.requestMatchers("/favicon.ico").permitAll();
                    authorize.anyRequest().authenticated();
                })
                .addFilter(new DefaultLogoutPageGeneratingFilter())
                .csrf(CsrfConfigurer::disable)
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/")))
                .build();
    }
}
