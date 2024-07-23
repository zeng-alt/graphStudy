//package com.zjj.graphstudy.service;
//
//import com.zjj.graphstudy.utils.JwtUtil;
//import jakarta.annotation.Resource;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolderStrategy;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
//import org.springframework.security.web.context.SecurityContextRepository;
//import org.springframework.security.core.context.ListeningSecurityContextHolderStrategy;
//import org.springframework.stereotype.Service;
//
//import java.util.Objects;
//import java.util.Optional;
//
///**
// * @author zengJiaJun
// * @crateTime 2024年07月20日 15:33
// * @version 1.0
// */
//@Service
//public class LoginServiceImpl {
//
//
//    public Optional<String> login(String username, String password, HttpServletRequest request, HttpServletResponse response) {
//        //进行用户认证
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(username, password);
//        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
//        //认证未通过，给出提示
//        if(Objects.isNull(authenticate)){
//            throw new RuntimeException("登陆失败！");
//        }
//        SecurityContextHolderStrategy securityContextHolderStrategy = new ListeningSecurityContextHolderStrategy();
//                Authentication authentication = authenticationManager.authenticate(authenticate);
//        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
//        context.setAuthentication(authentication);
//        securityContextHolderStrategy.setContext(context);
//        securityContextRepository.saveContext(context, request, response);
//        //通过了，生成jwt
//        UserDetails loginUser = (UserDetails) authenticate.getPrincipal();
//        String userUsername = loginUser.getUsername();
//        String jwt = JwtUtil.createJWT(userUsername);
//
//        //将用户信息存入redis
////        redisCache.setCacheObject("login"+id,loginUser);
//        return Optional.of(jwt);
//    }
//}
