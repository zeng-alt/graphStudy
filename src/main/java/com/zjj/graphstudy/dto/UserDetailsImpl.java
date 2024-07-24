package com.zjj.graphstudy.dto;

import com.zjj.graphstudy.entity.Users;
import com.zjj.graphstudy.service.UserDetailsServiceImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月22日 22:26
 * @version 1.0
 */
public class UserDetailsImpl implements UserDetails {

    private String username;
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl() {}

    public UserDetailsImpl(Users users) {
        username = users.getUsername();
        password = users.getPassword();
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return username;
    }

    @Override
    public String getUsername() {
        return password;
    }
}
