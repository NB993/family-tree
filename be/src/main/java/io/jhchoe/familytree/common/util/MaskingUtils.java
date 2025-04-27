package io.jhchoe.familytree.common.util;

/**
 * 개인정보 마스킹을 위한 유틸리티 클래스
 * 각종 개인정보를 로깅, 표시 등에 사용할 때 마스킹 처리하는 메서드를 제공합니다.
 */
public class MaskingUtils {

    /**
     * 이메일 주소를 마스킹 처리합니다.
     * 예시:
     * - test@example.com -> t***@e***.com
     * - a@b.com -> a@b.com (짧은 이메일은 마스킹하지 않음)
     * - invalid-email -> i************ (이메일 형식이 아닌 경우 단순 마스킹)
     *
     * @param email 마스킹할 이메일 주소
     * @return 마스킹된 이메일 주소, null 또는 빈 문자열이 입력되면 빈 문자열 반환
     */
    public static String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "";
        }
        
        // '@' 기호가 없는 경우 문자열 자체를 마스킹
        int atIndex = email.indexOf('@');
        if (atIndex < 0) {
            // 첫 글자만 보이고 나머지는 '*'로 마스킹
            return email.length() > 1 
                ? email.substring(0, 1) + "*".repeat(email.length() - 1)
                : email; // 한 글자인 경우 그대로 반환
        }
        
        // 사용자 이름 부분 마스킹 (첫 글자만 보이고 나머지는 '*'로)
        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex + 1);
        
        // 사용자 이름이 2글자 이상인 경우만 마스킹
        String maskedUsername = username.length() > 2 
            ? username.charAt(0) + "*".repeat(username.length() - 1)
            : username;
        
        // 도메인 마스킹 처리
        String maskedDomain;
        int dotIndex = domain.indexOf('.');
        
        if (dotIndex > 0) {
            // 도메인 이름과 TLD로 분리
            String domainName = domain.substring(0, dotIndex);
            String tld = domain.substring(dotIndex); // '.com', '.co.kr' 등
            
            // 도메인 이름이 2글자 이상인 경우만 마스킹
            maskedDomain = domainName.length() > 2
                ? domainName.charAt(0) + "*".repeat(domainName.length() - 1) + tld
                : domainName + tld;
        } else {
            // '.'이 없는 경우 (비정상적인 도메인)
            maskedDomain = domain.length() > 1
                ? domain.charAt(0) + "*".repeat(domain.length() - 1)
                : domain;
        }
        
        return maskedUsername + "@" + maskedDomain;
    }
    
    /**
     * 개인정보의 일부를 마스킹 처리합니다.
     * 예: "홍길동" -> "홍*동", "김철수" -> "김*수"
     *
     * @param name 마스킹할 이름
     * @return 마스킹된 이름, null 또는 빈 문자열이 입력되면 빈 문자열 반환
     */
    public static String maskName(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        
        if (name.length() <= 1) {
            return name;
        }
        
        if (name.length() == 2) {
            return name.charAt(0) + "*";
        }
        
        // 3글자 이상인 경우 가운데 글자들을 '*'로 마스킹
        StringBuilder masked = new StringBuilder();
        masked.append(name.charAt(0));
        
        for (int i = 1; i < name.length() - 1; i++) {
            masked.append('*');
        }
        
        masked.append(name.charAt(name.length() - 1));
        
        return masked.toString();
    }
}
