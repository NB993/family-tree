package io.jhchoe.familytree.common.auth.domain;

import lombok.Getter;

@Getter
public enum UserRole {

    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    String value;

    UserRole(String value) {
        this.value = value;
    }
}
