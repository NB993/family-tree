package io.jhchoe.familytree.common.auth.domain;

import io.jhchoe.familytree.common.exception.CommonExceptionCode;
import io.jhchoe.familytree.common.exception.FTException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.util.ObjectUtils;

@Converter(autoApply = true)
public enum AuthenticationType implements AttributeConverter<AuthenticationType, String> {

    FORM_LOGIN(true),
    OAUTH2(true),
    JWT(true),
    NONE(false);

    private final boolean loginable;

    AuthenticationType(boolean loginable) {
        this.loginable = loginable;
    }

    /**
     * 해당 인증 타입으로 로그인이 가능한지 여부를 반환합니다.
     *
     * @return 로그인 가능 여부
     */
    public boolean isLoginable() {
        return this.loginable;
    }

    @Override
    public String convertToDatabaseColumn(AuthenticationType authenticationType) {
        if (ObjectUtils.isEmpty(authenticationType)) {
            throw new FTException(CommonExceptionCode.ENUM_CONVERT);
        }
        return authenticationType.name();
    }

    @Override
    public AuthenticationType convertToEntityAttribute(String dbData) {
        if (ObjectUtils.isEmpty(dbData)) {
            throw new FTException(CommonExceptionCode.ENUM_CONVERT);
        }
        try {
            return AuthenticationType.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            throw new FTException(CommonExceptionCode.ENUM_CONVERT);
        }
    }
}
