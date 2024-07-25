package com.zjj.graphstudy.service;

import com.zjj.graphstudy.dao.UserRepository;
import com.zjj.graphstudy.dto.UserMobileDetails;
import com.zjj.graphstudy.dto.UserMobileDetailsImpl;
import com.zjj.graphstudy.exception.MobilecodeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月19日 21:35
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService, MobileDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository
                .findByUsername(username)
                .map(u -> User.withUsername(username).password(u.getPassword()).build())
                .orElseThrow(() -> new UsernameNotFoundException(username + " 用户不存在"));
    }

    @Override
    public UserMobileDetails loadUserByPhone(String phone) throws MobilecodeNotFoundException {
        return userRepository
                .findByTelephoneNumber(phone)
                .map(u -> new UserMobileDetailsImpl(u.getUsername(), u.getPassword(), new ArrayList<>(), u.getTelephoneNumber(), "2024"))
                .orElseThrow(() -> new MobilecodeNotFoundException(phone + " 手机号不存在"));
    }


}
