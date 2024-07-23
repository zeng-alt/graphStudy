package com.zjj.graphstudy.controller;

import com.zjj.graphstudy.dto.Result;
import jakarta.annotation.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.rmi.ServerException;
import java.util.UUID;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月23日 20:41
 * @version 1.0
 */
@RequestMapping("/login")
@RestController
public class LoginController {

    @Resource
    private AuthenticationManager authenticationManager;

//    @PostMapping("/userPassword")
//    public Result login(@RequestParam("username") String username, @RequestParam("password") String password) throws ServerException {
//        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginBody.getUsername(), loginBody.getPassword());
//        try {
//            authenticationManager.authenticate(null);
//        } catch (BadCredentialsException | UsernameNotFoundException e) {
//            throw new ServerException(e.getMessage());
//        }
//        String token = UUID.randomUUID().toString().replace("-", "");
//        return Result.ok(token);
//    }
}
