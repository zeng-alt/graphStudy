package com.zjj.graphstudy.filter.mobilecode;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.util.Assert;

import java.io.Serial;
import java.util.Collection;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月23日 22:20
 * @version 1.0
 */
@EqualsAndHashCode(of = {"principal", "credentials"}, callSuper = false)
public class MobilecodeAuthenticationToken extends AbstractAuthenticationToken implements MessageSourceAware {
    @Serial
    private static final long serialVersionUID = 530L;

    protected transient MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    @Getter
    private Object principal;
    @Getter
    private String credentials;



    public MobilecodeAuthenticationToken(String phone, String mobileCode) {
        super(null);
        this.principal = phone;
        this.credentials = mobileCode;
        this.setAuthenticated(false);
    }

    public MobilecodeAuthenticationToken(Object principal, String mobileCode, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = mobileCode;
        super.setAuthenticated(true);

    }

    public static MobilecodeAuthenticationToken unauthenticated(String phone, String mobileCode) {
        return new MobilecodeAuthenticationToken(phone, mobileCode);
    }

    public static MobilecodeAuthenticationToken authenticated(Object principal, String mobileCode, Collection<? extends GrantedAuthority> authorities) {
        return new MobilecodeAuthenticationToken(principal, mobileCode, authorities);
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
