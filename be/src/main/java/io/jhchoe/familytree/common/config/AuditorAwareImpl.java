package io.jhchoe.familytree.common.config;

import io.jhchoe.familytree.common.auth.domain.FTUser;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditorAwareImpl implements AuditorAware<Long> {

    private static final Long SYSTEM_USER_ID = 0L;

    @Override
    public Optional<Long> getCurrentAuditor() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.of(SYSTEM_USER_ID);
        }

        final FTUser userDetails = (FTUser) authentication.getPrincipal();
        return Optional.of(userDetails.getId());
    }
}
