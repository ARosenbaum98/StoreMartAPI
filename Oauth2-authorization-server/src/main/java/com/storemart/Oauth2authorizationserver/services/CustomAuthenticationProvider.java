package com.storemart.Oauth2authorizationserver.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private EmployeeDetailsService detailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails userDetails = detailsService.loadUserByUsername(username);

        return checkPassword(userDetails, password);
    }

    private Authentication checkPassword(UserDetails userDetails, String rawPassword) {
        if(passwordEncoder.matches(rawPassword, userDetails.getPassword())){
            return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        }
        String msg = "check password failed in CustomAuthenticationProvider: "+
                "username = '"+userDetails.getUsername()+"'"+"rawPassword = '"+rawPassword+"' encryptedPassword = '"+userDetails.getPassword()+"'";
        log.debug(msg);
        throw new BadCredentialsException(msg);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
