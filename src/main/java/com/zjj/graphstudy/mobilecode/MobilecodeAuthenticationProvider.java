package com.zjj.graphstudy.mobilecode;

import com.zjj.graphstudy.dto.UserDetailsImpl;
import com.zjj.graphstudy.service.MobileDetailsService;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月23日 22:19
 * @version 1.0
 */
public class MobilecodeAuthenticationProvider implements AuthenticationProvider, InitializingBean, MessageSourceAware {

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    @Setter
    private MobileDetailsService mobileDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MobilecodeAuthenticationToken mobilecodeAuthenticationToken = (MobilecodeAuthenticationToken) authentication;
        String phone = mobilecodeAuthenticationToken.getPhone();
        String mobileCode = mobilecodeAuthenticationToken.getMobileCode();
        System.out.println("登陆手机号：" + phone);
        System.out.println("手机验证码：" + mobileCode);

        // 模拟从redis中读取手机号对应的验证码及其用户名
        Map<String, String> dataFromRedis = new HashMap<>();
        dataFromRedis.put("code", "6789");
        dataFromRedis.put("username", "admin");

        // 判断验证码是否一致
        if (!mobileCode.equals(dataFromRedis.get("code"))) {
            throw new BadCredentialsException("验证码错误");
        }

        // 如果验证码一致，从数据库中读取该手机号对应的用户信息
//        UserDetailsImpl loadedUser = (UserDetailsImpl) userDetailsService.loadUserByUsername(dataFromRedis.get("username"));
//        if (loadedUser == null) {
//            throw new UsernameNotFoundException("用户不存在");
//        }
//        return new MobilecodeAuthenticationToken(loadedUser, null, loadedUser.getAuthorities());
        return null;
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
