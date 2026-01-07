package io.jhchoe.familytree.core.family.application.port.out;

/**
 * 태그 삭제를 위한 아웃바운드 포트입니다.
 */
public interface DeleteFamilyMemberTagPort {

    /**
     * 태그를 삭제합니다.
     * CASCADE로 연결된 매핑도 함께 삭제됩니다.
     *
     * @param id 삭제할 태그 ID
     */
    void deleteById(Long id);
}
