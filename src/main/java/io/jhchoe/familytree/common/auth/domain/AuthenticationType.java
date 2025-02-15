package io.jhchoe.familytree.common.auth.domain;

import io.jhchoe.familytree.common.exception.CommonException;
import io.jhchoe.familytree.common.exception.FTException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.util.ObjectUtils;

@Converter(autoApply = true)
public enum AuthenticationType implements AttributeConverter<AuthenticationType, String> {

    FORM_LOGIN,
    OAUTH2;

    @Override
    public String convertToDatabaseColumn(AuthenticationType authenticationType) {
        if (ObjectUtils.isEmpty(authenticationType)) {
            throw new FTException(CommonException.ENUM_CONVERT);
        }
        return authenticationType.name();
    }

    @Override
    public AuthenticationType convertToEntityAttribute(String dbData) {
        if (ObjectUtils.isEmpty(dbData)) {
            throw new FTException(CommonException.ENUM_CONVERT);
        }
        try {
            return AuthenticationType.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            throw new FTException(CommonException.ENUM_CONVERT);
        }
    }
}
