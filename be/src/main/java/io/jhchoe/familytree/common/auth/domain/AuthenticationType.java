package io.jhchoe.familytree.common.auth.domain;

import io.jhchoe.familytree.common.exception.CommonExceptionCode;
import io.jhchoe.familytree.common.exception.FTException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.util.ObjectUtils;

@Converter(autoApply = true)
public enum AuthenticationType implements AttributeConverter<AuthenticationType, String> {

    FORM_LOGIN,
    OAUTH2,
    JWT;

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
