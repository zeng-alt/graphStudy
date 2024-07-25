package com.zjj.graphstudy.mobilecode;

import com.zjj.graphstudy.dto.UserDetailsImpl;
import com.zjj.graphstudy.service.MobileDetailsService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月23日 22:19
 * @version 1.0
 */
@Slf4j
@Component
public class MobilecodeAuthenticationProvider implements AuthenticationProvider, InitializingBean, MessageSourceAware {

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    @Setter
    private MobileDetailsService mobileDetailsService;

    @Autowired
    public MobilecodeAuthenticationProvider(MobileDetailsService mobileDetailsService) {
        this.mobileDetailsService = mobileDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(MobilecodeAuthenticationToken.class, authentication,
                () -> this.messages.getMessage("MobilecodeAuthenticationProvider.onlySupports",
                        "Only MobilecodeAuthenticationToken is supported"));

        MobilecodeAuthenticationToken mobilecodeAuthenticationToken = (MobilecodeAuthenticationToken) authentication;
        String phone = mobilecodeAuthenticationToken.getPrincipal().toString();
        String mobileCode = mobilecodeAuthenticationToken.getCredentials();

        // 判断验证码是否一致
        if (!mobileCode.equals("2024")) {
            throw new BadCredentialsException("验证码错误");
        }


        // 如果验证码一致，从数据库中读取该手机号对应的用户信息
        UserDetails user = retrieveUser(phone, mobilecodeAuthenticationToken);
        if (user == null) {
            log.debug("Failed to find user '" + phone + "'");
            throw new UsernameNotFoundException("用户不存在");
        }
        return createSuccessAuthentication(user, mobilecodeAuthenticationToken, user);
    }

    protected Authentication createSuccessAuthentication(Object principal, MobilecodeAuthenticationToken authentication,
                                                         UserDetails user) {
        MobilecodeAuthenticationToken result = MobilecodeAuthenticationToken.authenticated(principal,
                authentication.getCredentials(), user.getAuthorities());
        result.setDetails(authentication.getDetails());
        log.debug("Authenticated user");
        return result;
    }

    protected UserDetails retrieveUser(String phone, MobilecodeAuthenticationToken authentication)
            throws AuthenticationException {

        try {
            UserDetails userDetails = this.mobileDetailsService.loadUserByPhone(phone);
            if (userDetails == null) {
                throw new InternalAuthenticationServiceException(
                        "UserDetailsService returned null, which is an interface contract violation"
                );
            }
            return userDetails;
        } catch (UsernameNotFoundException | InternalAuthenticationServiceException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return MobilecodeAuthenticationToken.class.isAssignableFrom(authentication);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.mobileDetailsService, "A UserDetailsService must be set");
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }
}
