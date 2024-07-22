package com.zjj.graphstudy.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年07月19日 20:02
 */

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .authorizeHttpRequests(
                    author ->
                        author.requestMatchers("/unauthenticated", "/oauth2/**", "/login/**", "/graphiql", "/graphql").permitAll()
                        .anyRequest().fullyAuthenticated()
                )
//                .formLogin(
//                    formLogin -> formLogin
//                        .loginPage("/login")
//                        .permitAll()
//                )
                .cors(Customizer.withDefaults())  // 开启跨域
                .csrf(Customizer.withDefaults())  // 开启csrf 保护
                .sessionManagement(sm->sm.maximumSessions(2))  // 每个账户最大的session数量
                .logout(logout -> logout.invalidateHttpSession(true)) // 退出时，设置session无效
                .build();
        // SpringUtil.getBean(TyplmPartBomService.class).getPartForm(partBomViewList.get(0).getBomTreeNodeList().get(0).oid)
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
