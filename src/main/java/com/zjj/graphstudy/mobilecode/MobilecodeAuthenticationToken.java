package com.zjj.graphstudy.mobilecode;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.Serial;
import java.util.Collection;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月23日 22:20
 * @version 1.0
 */
@EqualsAndHashCode(of = {"phone", "mobileCode"}, callSuper = false)
public class MobilecodeAuthenticationToken extends AbstractAuthenticationToken implements MessageSourceAware {
    @Serial
    private static final long serialVersionUID = 530L;

    protected transient MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    @Getter
    private Object principal;
    @Getter
    private Object credentials;
    @Getter
    private String phone;
    @Getter
    private String mobileCode;


    public MobilecodeAuthenticationToken(String phone, String mobileCode) {
        super(null);
        this.phone = phone;
        this.mobileCode = mobileCode;
        this.setAuthenticated(false);
    }

    public MobilecodeAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);

    }

    public static MobilecodeAuthenticationToken unauthenticated(String phone, String mobileCode) {
        return new MobilecodeAuthenticationToken(phone, mobileCode);
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated,
                "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }

    @Override
    public void setMessageSource(@NonNull MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }
}
