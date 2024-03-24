package com.store.management.tool.config;

import com.store.management.tool.domain.User;
import com.store.management.tool.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

@Configuration
public class UserInfoDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var optionalUser = userRepository.findByEmail(username);
        return optionalUser.map(UserInfoDetailsService::userDetailsInfo).orElseThrow(() -> new UsernameNotFoundException("User doesn't exist"));
    }

    private static UserInfoDetails userDetailsInfo(User user) {
        var userInfoDetails = new UserInfoDetails();
        userInfoDetails.setEmail(user.getEmail());
        userInfoDetails.setPassword(user.getPassword());
        var role = new SimpleGrantedAuthority(user.getUserRole().name());
        userInfoDetails.setRoles(List.of(role));
        return userInfoDetails;
    }
}
