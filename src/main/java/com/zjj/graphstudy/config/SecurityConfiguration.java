package com.zjj.graphstudy.config;

import com.zjj.graphstudy.filter.JwtAuthenticationTokenFilter;
import com.zjj.graphstudy.filter.TenantFilter;
import com.zjj.graphstudy.handler.LoginAuthenticationHandler;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年07月19日 20:02
 */

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfiguration {

    @Resource
    private LoginAuthenticationHandler loginAuthenticationHandler;
    @Resource JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Resource TenantFilter tenantFilter;
    @Resource
    UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .authorizeHttpRequests(
                    author ->
                        author
                                .requestMatchers("/unauthenticated", "/oauth2/**", "/login/userPassword").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(
                    formLogin -> formLogin
                            .disable()
//                        .failureHandler(loginAuthenticationHandler)
//                        .successHandler(loginAuthenticationHandler)
//                        .permitAll()
                )
                .userDetailsService(userDetailsService)
                .cors(Customizer.withDefaults())  // 开启跨域
                .csrf(AbstractHttpConfigurer::disable)  // 开启csrf 保护
                .sessionManagement(AbstractHttpConfigurer::disable)  // 每个账户最大的session数量
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .logout(logout -> logout.logoutUrl("logout").logoutSuccessHandler()) // 退出时，设置session无效
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(tenantFilter, AuthorizationFilter.class)
//                .apply()
                .build();
        // SpringUtil.getBean(TyplmPartBomService.class).getPartForm(partBomViewList.get(0).getBomTreeNodeList().get(0).oid)
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
        return daoAuthenticationProvider;
    }

//    @Bean
//    public AuthenticationManager authenticationManager() {
//        List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
////        authenticationProviders.add(mobilecodeAuthenticationProvider());
//        authenticationProviders.add(daoAuthenticationProvider());
//        return new ProviderManager(authenticationProviders);
//
//    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//        manager.createUser(User.withUsername("root1").password("{bcrypt}" + bCryptPasswordEncoder.encode("123456")).roles("ROOT").build());
//        return manager;
//    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationEventPublisher.class)
    DefaultAuthenticationEventPublisher defaultAuthenticationEventPublisher(ApplicationEventPublisher delegate) {

        PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        delegatingPasswordEncoder.encode("111");
        return new DefaultAuthenticationEventPublisher(delegate);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

}
