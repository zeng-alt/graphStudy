package com.zjj.graphstudy.dao;

import com.zjj.graphstudy.entity.PersistentLogins;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import java.util.Date;
import java.util.Optional;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月19日 21:58
 * @version 1.0
 */
public interface PersistentLoginsRepository extends PersistentTokenRepository, BaseRepository<PersistentLogins, Long> {

    default void createNewToken(PersistentRememberMeToken token) {
        PersistentLogins persistentLogins = new PersistentLogins();
        persistentLogins.setUsername(token.getUsername());
        persistentLogins.setSeries(token.getSeries());
        persistentLogins.setToken(token.getTokenValue());
        persistentLogins.setLastUsed(token.getDate());
        save(persistentLogins);
    }

    default void updateToken(String series, String tokenValue, Date lastUsed) {
        updateBySeries(series, tokenValue, lastUsed);
    }

    default PersistentRememberMeToken getTokenForSeries(String seriesId) {
        return findBySeries(seriesId)
                .map(u -> new PersistentRememberMeToken(u.getUsername(), u.getSeries(), u.getToken(), u.getLastUsed()))
                .orElse(null);
    }

    default void removeUserTokens(String username) {
        deleteByUsername(username);
    }

    @Modifying
    default void updateBySeries(String series, String tokenValue, Date lastUsed) {

        PersistentLogins persistentLogins = findBySeries(series).orElse(null);
        if (persistentLogins != null) {
            persistentLogins.setToken(tokenValue);
            persistentLogins.setLastUsed(lastUsed);
            save(persistentLogins);
        }
    }

    @Modifying
    void deleteByUsername(String username);

    Optional<PersistentLogins> findBySeries(String series);

}
