package com.zjj.graphstudy.config;

import com.zjj.graphstudy.filter.CustomAuthenticationFilter;
import com.zjj.graphstudy.filter.JwtAuthenticationTokenFilter;
import com.zjj.graphstudy.filter.JwtRenewFilter;
import com.zjj.graphstudy.filter.TenantFilter;
import com.zjj.graphstudy.filter.login.failure.LoginFailureExcessiveFilter;
import com.zjj.graphstudy.handler.LoginAuthenticationHandler;
import com.zjj.graphstudy.filter.mobilecode.MobilecodeAuthenticationFilter;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;


/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年07月19日 20:02
 */
@Lazy
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
    @Resource
    MobilecodeDsl mobilecodeDsl;
    @Resource
    JwtRenewFilter jwtRenewFilter;
    @Resource
    LoginFailureExcessiveFilter loginFailureExcessiveFilter;


    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain filterChain(HttpSecurity http, CustomAuthenticationFilter customAuthenticationFilter, MobilecodeAuthenticationFilter mobilecodeAuthenticationFilter) throws Exception {

        return http
                .authorizeHttpRequests(
                    author ->
                        author
                                .requestMatchers(HttpMethod.POST, "/login/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/graphiql/**").permitAll()
                                .requestMatchers("/graphql/**").permitAll()
                        .anyRequest().authenticated()
                )
                .headers(
                        headers -> headers
                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .formLogin(
                        AbstractHttpConfigurer::disable // 禁用，前后端分离项目
//                         .loginPage("/login")
//                        .failureHandler(loginAuthenticationHandler)
//                        .successHandler(loginAuthenticationHandler)
//                        .permitAll()
//                        .loginPage("/login/mobilecode")
//                        .failureHandler(loginAuthenticationHandler)
//                        .successHandler(loginAuthenticationHandler)
//                        .permitAll()
                )
//                .oauth2Client(
////                        client -> client
////                                .init()
//                )
                .userDetailsService(userDetailsService)
                .cors(Customizer.withDefaults())  // 开启跨域
                .csrf(AbstractHttpConfigurer::disable)  // 关闭csrf 保护
                .sessionManagement(AbstractHttpConfigurer::disable)  // 每个账户最大的session数量
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .logout(logout -> logout.logoutUrl("logout").logoutSuccessHandler()) // 退出时，设置session无效
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtRenewFilter, JwtAuthenticationTokenFilter.class)
//                .addFilterBefore(mobilecodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(loginFailureExcessiveFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .addFilterBefore(tenantFilter, AuthorizationFilter.class)
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(loginAuthenticationHandler)
                        .authenticationEntryPoint(loginAuthenticationHandler)
                )
                .with(mobilecodeDsl, dsl -> {})
                .build();

        // SpringUtil.getBean(TyplmPartBomService.class).getPartForm(partBomViewList.get(0).getBomTreeNodeList().get(0).oid)
    }

    @Order(-1)
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
        return daoAuthenticationProvider;
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationManager.class)
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration, List<AuthenticationProvider> authenticationProviders, AuthenticationEventPublisher authenticationEventPublisher) throws Exception {
        ProviderManager providerManager = new ProviderManager(authenticationProviders, configuration.getAuthenticationManager());
        providerManager.setAuthenticationEventPublisher(authenticationEventPublisher);
        return providerManager;
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationEventPublisher.class)
    public AuthenticationEventPublisher defaultAuthenticationEventPublisher(ApplicationEventPublisher delegate) {
        return new DefaultAuthenticationEventPublisher(delegate);
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {

        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }



    /*
    无法注入 mobilecodeAuthenticationProvider
     */
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//        AuthenticationManager authenticationManager = configuration.getAuthenticationManager();
//        return authenticationManager;
//    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//        manager.createUser(User.withUsername("root1").password("{bcrypt}" + bCryptPasswordEncoder.encode("123456")).roles("ROOT").build());
//        return manager;
//    }
}
