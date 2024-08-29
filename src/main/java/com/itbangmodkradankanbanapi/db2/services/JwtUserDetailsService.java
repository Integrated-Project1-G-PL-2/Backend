package com.itbangmodkradankanbanapi.db2.services;

import com.itbangmodkradankanbanapi.db1.service.UserLocalService;
import com.itbangmodkradankanbanapi.db2.entities.AuthUser;
import com.itbangmodkradankanbanapi.db2.entities.User;
import com.itbangmodkradankanbanapi.db2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLocalService userLocalService;
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(userName);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username password incorrect" );
        }
        userLocalService.addLocalUser(user);
//        List<GrantedAuthority> roles = new ArrayList<>();
//        GrantedAuthority grantedAuthority = new GrantedAuthority() {
//            @Override
//            public String getAuthority() {
//                return user.getRole().toString();
//            }
//        };
//        roles.add(grantedAuthority);
        UserDetails userDetails = new AuthUser(userName, user.getPassword());
        return userDetails;
    }
}


