package com.storemart.Oauth2authorizationserver.services;

import com.storemart.Oauth2authorizationserver.exceptions.LoginUserNotFound;
import com.storemart.Oauth2authorizationserver.models.employee.EmployeeLogin;
import com.storemart.Oauth2authorizationserver.models.employee.PermissionsGroup;
import com.storemart.Oauth2authorizationserver.repositories.EmployeeRepository;
import com.storemart.Oauth2authorizationserver.repositories.LoginRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
@Slf4j
public class EmployeeDetailsService implements UserDetailsService {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(11);
    }

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LoginRepository loginRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        EmployeeLogin login = loginRepository.findByUsernameIgnoreCase(username);

        if(login == null) {
            String msg = "Error in OAuth service - User with username "+username+"not found";
            log.error(msg);
            throw new LoginUserNotFound(msg);
        }

        return new User(
                login.getUsername(),
                login.getPassword(),
                true,
                true,
                true,
                true,
                getAuthority(login.getGroup())
        );

    }

    private Collection<? extends GrantedAuthority> getAuthority(PermissionsGroup group) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(group.getId()));
        return authorities;
    }
}
