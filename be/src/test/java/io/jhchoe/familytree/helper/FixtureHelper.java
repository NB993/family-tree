package io.jhchoe.familytree.helper;

import io.jhchoe.familytree.common.auth.domain.FTUser;
import io.jhchoe.familytree.common.auth.domain.GoogleUserInfo;
import io.jhchoe.familytree.common.auth.domain.OAuth2Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class FixtureHelper {

    public static FTUser createGoogleFTUser() {
        return createGoogleFTUser(null, null, null);
    }

    public static FTUser createGoogleFTUser(Long id, String name, String email) {
        id = Objects.requireNonNullElse(id, 1L);
        name = Objects.requireNonNullElse(name, "Test User");
        email = Objects.requireNonNullElse(email, "testuser@example.com");

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", UUID.randomUUID().toString());
        attributes.put("name", name);
        attributes.put("email", email);
        attributes.put("picture", "https://example.com/profile.jpg");

        GoogleUserInfo googleUserInfo = new GoogleUserInfo(attributes);

        return FTUser.ofOAuth2User(id, googleUserInfo, OAuth2Provider.GOOGLE, attributes);
    }
}
