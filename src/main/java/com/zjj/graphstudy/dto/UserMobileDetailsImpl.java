package com.zjj.graphstudy.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年07月24日 15:59
 */
public class UserMobileDetailsImpl extends User implements UserMobileDetails {

    private String phone;
    private String code;


    public UserMobileDetailsImpl(String username, String password, Collection<? extends GrantedAuthority> authorities, String phone, String code) {
        super(username, password, authorities);
        this.phone = phone;
        this.code = code;
    }


    public UserMobileDetailsImpl(String username, String password, String phone, String code, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.phone = phone;
        this.code = code;
    }

    @Override
    public String getPhone() {
        return null;
    }

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.code = null;
    }
}
