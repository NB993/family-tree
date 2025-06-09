package io.jhchoe.familytree.core.family.adapter.in;

import io.jhchoe.familytree.core.family.adapter.in.response.FamilyNameAvailabilityResponse;
import io.jhchoe.familytree.core.family.application.port.in.CheckFamilyNameDuplicationUseCase;
import io.jhchoe.familytree.core.family.application.port.in.FamilyNameAvailabilityResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 가족명 중복 확인 API 컨트롤러입니다.
 * 실시간으로 가족명 사용 가능 여부를 확인할 수 있는 API를 제공합니다.
 * 
 * @author Claude AI
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/families")
@RequiredArgsConstructor
public class CheckFamilyNameController {

    private final CheckFamilyNameDuplicationUseCase checkFamilyNameDuplicationUseCase;

    /**
     * 가족명 중복 여부를 확인합니다.
     * 
     * @param name 확인할 가족명
     * @return 가족명 사용 가능 여부 정보
     */
    @GetMapping("/check-name")
    public ResponseEntity<FamilyNameAvailabilityResponse> checkFamilyNameDuplication(
            @RequestParam("name") String name) {
        
        log.info("가족명 중복 확인 요청: name={}", name);
        
        try {
            FamilyNameAvailabilityResult result = checkFamilyNameDuplicationUseCase.checkDuplication(name);
            FamilyNameAvailabilityResponse response = FamilyNameAvailabilityResponse.from(result);
            
            log.info("가족명 중복 확인 완료: name={}, available={}", name, result.available());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("가족명 중복 확인 요청 오류: name={}, error={}", name, e.getMessage());
            
            // 유효성 검사 실패 시 사용 불가로 응답
            FamilyNameAvailabilityResponse errorResponse = new FamilyNameAvailabilityResponse(
                false, 
                e.getMessage()
            );
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
