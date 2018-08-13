package com.viridian.configServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger log = LogManager.getLogger();

    @Value("#{'${security.basic.username}'}") //property with default value
            String basicUser;

    @Value("#{'${security.basic.password}'}") //property with default value
            String basicPass;

    @Value("#{'${security.super.username}'}") //property with default value
            String superUser;

    @Value("#{'${security.super.password}'}") //property with default value
            String superPass;
    @Value("#{'${security.basic.enable}'}") //property with default value
    private Boolean securityIsEnable;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if(securityIsEnable) {
            // Note:
            // Use this to enable the tomcat basic authentication (tomcat popup rather than spring login page)
            // Note that the CSRf token is disabled for all requests
            log.info("Disabling CSRF, enabling basic authentication...");
            http
                    .authorizeRequests()
                    .antMatchers("/authorization/**").permitAll()
                    .antMatchers(HttpMethod.GET, "/**").authenticated() // These urls are allowed by any authenticated user
                    //.antMatchers("/encrypt/**").hasRole("USER")
                    .antMatchers("/encrypt/**").authenticated()
                    .antMatchers("/decrypt/**").hasRole("ADMIN")
                    .and()
                    .httpBasic();
            http.csrf().disable();
        }else {
            log.info("Disabling CSRF, permit all without authentication...");
            http
                    .authorizeRequests()
                    .antMatchers("/**").permitAll()
                    .and()
                    .httpBasic();
            http.csrf().disable();
        }
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Get the user credentials from the console (or any other source):
        String username = basicUser;
        String password = basicPass;
        String superusername = superUser;
        String superpassword = superPass;

        // Set the inMemoryAuthentication object with the given credentials:
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        String encodedPassword = passwordEncoder().encode(password);
        String encodedPassword2 = passwordEncoder().encode(superpassword);

        manager.createUser(User.withUsername(username).password(encodedPassword).roles("USER").build());
        manager.createUser(User.withUsername(superusername).password(encodedPassword2).roles("ADMIN","USER").build());
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
