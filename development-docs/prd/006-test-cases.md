# PRD-006 FamilyMember 태그 기능 TDD 계획

## 개요
- **PRD**: `development-docs/prd/006-family-member-tagging.md`
- **작성일**: 2026-01-07
- **범위**: Sprint 1-BE (태그 CRUD) + Sprint 2-BE (멤버-태그 연결)

---

## Sprint 1-BE: 태그 CRUD 테스트 케이스

### Core 계층 (core-tdd)

#### 1. FamilyMemberTag Domain 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `newTag_creates_tag_with_random_color` | 랜덤 색상으로 태그 생성 |
| P0 | `newTag_sets_created_timestamp` | 생성 시간 자동 설정 |
| P0 | `withId_restores_tag_with_all_fields` | 모든 필드로 태그 복원 |
| P0 | `rename_returns_new_tag_with_changed_name` | 이름 변경 시 새 객체 반환 |
| P0 | `changeColor_returns_new_tag_with_changed_color` | 색상 변경 시 새 객체 반환 |
| P1 | `newTag_throws_when_name_is_null` | 이름 null 시 예외 |
| P1 | `newTag_throws_when_name_is_empty` | 이름 빈문자열 시 예외 |
| P1 | `newTag_throws_when_name_exceeds_10_chars` | 이름 10자 초과 시 예외 |
| P1 | `newTag_throws_when_name_has_invalid_chars` | 허용되지 않은 문자 포함 시 예외 |
| P1 | `changeColor_throws_when_color_not_in_palette` | 팔레트에 없는 색상 시 예외 |
| P2 | `color_palette_contains_9_colors` | 색상 팔레트 9개 검증 |

#### 2. SaveFamilyMemberTagCommand 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `create_command_with_valid_inputs` | 유효한 입력으로 Command 생성 |
| P1 | `throw_when_family_id_is_null` | familyId null 시 예외 |
| P1 | `throw_when_name_is_null` | name null 시 예외 |
| P1 | `throw_when_name_is_blank` | name 공백만 시 예외 |
| P1 | `throw_when_name_exceeds_10_chars` | name 10자 초과 시 예외 |
| P1 | `throw_when_name_has_invalid_pattern` | 특수문자 포함 시 예외 |

#### 3. ModifyFamilyMemberTagCommand 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `create_command_with_valid_inputs` | 유효한 입력으로 Command 생성 |
| P1 | `throw_when_tag_id_is_null` | tagId null 시 예외 |
| P1 | `throw_when_name_is_null` | name null 시 예외 |
| P1 | `throw_when_color_not_in_palette` | 색상 팔레트에 없을 시 예외 |

#### 4. SaveFamilyMemberTagService 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `save_returns_id_when_valid_request` | 정상 생성 시 ID 반환 |
| P0 | `save_throws_F003_when_not_owner` | OWNER 아닐 때 F003 예외 |
| P1 | `save_throws_T001_when_tag_count_exceeds_10` | 10개 초과 시 T001 예외 |
| P1 | `save_throws_T002_when_name_duplicated` | 이름 중복 시 T002 예외 |
| P1 | `save_throws_F001_when_not_family_member` | Family 구성원 아닐 때 F001 예외 |

#### 5. FindFamilyMemberTagService 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `findAll_returns_tags_with_member_count` | 태그 목록 + 멤버 수 반환 |
| P0 | `findAll_returns_empty_list_when_no_tags` | 태그 없으면 빈 목록 반환 |
| P0 | `findAll_returns_tags_sorted_by_name` | 가나다순 정렬 |
| P1 | `findAll_throws_F001_when_not_family_member` | Family 구성원 아닐 때 F001 예외 |

#### 6. ModifyFamilyMemberTagService 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `modify_updates_name_successfully` | 이름 수정 성공 |
| P0 | `modify_updates_color_successfully` | 색상 수정 성공 |
| P0 | `modify_throws_F003_when_not_owner` | OWNER 아닐 때 F003 예외 |
| P1 | `modify_throws_T005_when_tag_not_found` | 태그 없을 때 T005 예외 |
| P1 | `modify_throws_T006_when_tag_belongs_to_other_family` | 다른 Family 태그 시 T006 예외 |
| P1 | `modify_throws_T002_when_name_duplicated` | 이름 중복 시 T002 예외 (본인 제외) |
| P1 | `modify_throws_T009_when_invalid_color` | 잘못된 색상 시 T009 예외 |

#### 7. DeleteFamilyMemberTagService 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `delete_removes_tag_successfully` | 태그 삭제 성공 |
| P0 | `delete_throws_F003_when_not_owner` | OWNER 아닐 때 F003 예외 |
| P1 | `delete_throws_T005_when_tag_not_found` | 태그 없을 때 T005 예외 |
| P1 | `delete_throws_T006_when_tag_belongs_to_other_family` | 다른 Family 태그 시 T006 예외 |

---

### Infra 계층 (infra-tdd)

#### 1. FamilyMemberTagJpaEntity 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `from_creates_entity_from_domain` | 도메인 → 엔티티 변환 |
| P0 | `toFamilyMemberTag_creates_domain_from_entity` | 엔티티 → 도메인 변환 |
| P0 | `from_updates_existing_entity_when_id_present` | ID 있으면 수정용 엔티티 |

#### 2. FamilyMemberTagAdapter 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `save_returns_id_when_success` | 저장 성공 시 ID 반환 |
| P0 | `findById_returns_tag_when_exists` | 태그 조회 성공 |
| P0 | `findById_returns_empty_when_not_exists` | 태그 없으면 빈 Optional |
| P0 | `findAllByFamilyId_returns_tags` | Family별 태그 목록 조회 |
| P0 | `findByFamilyIdAndName_returns_tag_when_exists` | 이름으로 태그 조회 |
| P0 | `countByFamilyId_returns_tag_count` | Family별 태그 수 조회 |
| P0 | `deleteById_removes_tag` | 태그 삭제 |

---

### Presentation 계층 (presentation-tdd)

#### 1. FamilyMemberTagController 인수 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `get_tags_returns_200_when_success` | GET 태그 목록 성공 |
| P0 | `post_tag_returns_201_when_success` | POST 태그 생성 성공 |
| P0 | `put_tag_returns_200_when_success` | PUT 태그 수정 성공 |
| P0 | `delete_tag_returns_204_when_success` | DELETE 태그 삭제 성공 |
| P1 | `post_tag_returns_400_when_T001` | 태그 수 초과 시 400 |
| P1 | `post_tag_returns_400_when_T002` | 이름 중복 시 400 |
| P1 | `post_tag_returns_400_when_T003` | 이름 길이 초과 시 400 |
| P1 | `post_tag_returns_403_when_F003` | 권한 없을 때 403 |
| P1 | `put_tag_returns_404_when_T005` | 태그 없을 때 404 |
| P1 | `delete_tag_returns_404_when_T005` | 태그 없을 때 404 |

#### 2. API 문서 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P2 | `document_find_tags` | 태그 목록 조회 문서 |
| P2 | `document_save_tag` | 태그 생성 문서 |
| P2 | `document_modify_tag` | 태그 수정 문서 |
| P2 | `document_delete_tag` | 태그 삭제 문서 |

---

## Sprint 2-BE: 멤버-태그 연결 테스트 케이스

### Core 계층 (core-tdd)

#### 1. FamilyMemberTagMapping Domain 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `newMapping_creates_mapping_with_timestamp` | 매핑 생성 시 시간 자동 설정 |
| P0 | `withId_restores_mapping_with_all_fields` | 모든 필드로 매핑 복원 |

#### 2. ModifyMemberTagsCommand 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `create_command_with_valid_inputs` | 유효한 입력으로 Command 생성 |
| P0 | `create_command_with_empty_tagIds` | 빈 tagIds (모든 태그 해제) |
| P1 | `throw_when_family_id_is_null` | familyId null 시 예외 |
| P1 | `throw_when_member_id_is_null` | memberId null 시 예외 |

#### 3. ModifyMemberTagsService 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `modify_assigns_tags_to_member` | 멤버에 태그 할당 성공 |
| P0 | `modify_removes_all_tags_when_empty_list` | 빈 목록이면 모든 태그 해제 |
| P0 | `modify_replaces_all_tags` | 전체 교체 방식 동작 |
| P0 | `modify_throws_F003_when_not_owner` | OWNER 아닐 때 F003 예외 |
| P1 | `modify_throws_F009_when_member_not_found` | 멤버 없을 때 F009 예외 |
| P1 | `modify_throws_T007_when_member_belongs_to_other_family` | 다른 Family 멤버 시 T007 예외 |
| P1 | `modify_throws_T005_when_tag_not_found` | 존재하지 않는 태그 시 T005 예외 |
| P1 | `modify_throws_T006_when_tag_belongs_to_other_family` | 다른 Family 태그 시 T006 예외 |
| P1 | `modify_throws_T008_when_tag_count_exceeds_10` | 멤버당 10개 초과 시 T008 예외 |

---

### Infra 계층 (infra-tdd)

#### 1. FamilyMemberTagMappingJpaEntity 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `from_creates_entity_from_domain` | 도메인 → 엔티티 변환 |
| P0 | `toFamilyMemberTagMapping_creates_domain` | 엔티티 → 도메인 변환 |

#### 2. FamilyMemberTagMappingAdapter 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `saveAll_saves_mappings` | 매핑 일괄 저장 |
| P0 | `findAllByMemberId_returns_mappings` | 멤버별 매핑 조회 |
| P0 | `deleteAllByMemberId_removes_mappings` | 멤버 매핑 전체 삭제 |
| P0 | `countByTagId_returns_member_count` | 태그별 멤버 수 조회 |
| P1 | `cascade_delete_when_tag_deleted` | 태그 삭제 시 매핑 CASCADE 삭제 |

---

### Presentation 계층 (presentation-tdd)

#### 1. FamilyMemberTagController - 멤버 태그 할당 인수 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `put_member_tags_returns_200_when_success` | 멤버 태그 할당 성공 |
| P1 | `put_member_tags_returns_403_when_F003` | 권한 없을 때 403 |
| P1 | `put_member_tags_returns_404_when_F009` | 멤버 없을 때 404 |
| P1 | `put_member_tags_returns_400_when_T008` | 태그 수 초과 시 400 |

#### 2. 기존 멤버 조회 API 수정 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P0 | `get_members_includes_tags_field` | 멤버 조회 시 tags 필드 포함 |
| P0 | `get_members_returns_empty_tags_when_no_tags` | 태그 없으면 빈 배열 |

#### 3. API 문서 테스트

| 우선순위 | 메서드명 | 설명 |
|---------|---------|------|
| P2 | `document_modify_member_tags` | 멤버 태그 할당 문서 |

---

## 다음 단계

1. [ ] **core-tdd** 스킬로 Core 계층 TDD 시작
   - FamilyMemberTag 도메인
   - SaveFamilyMemberTagService
   - FindFamilyMemberTagService
   - ModifyFamilyMemberTagService
   - DeleteFamilyMemberTagService

2. [ ] **infra-tdd** 스킬로 Infra 계층 TDD 진행
   - FamilyMemberTagJpaEntity
   - FamilyMemberTagAdapter
   - DB 마이그레이션

3. [ ] **presentation-tdd** 스킬로 Presentation 계층 TDD 완료
   - FamilyMemberTagController
   - API 문서화

4. [ ] Sprint 2-BE로 멤버-태그 연결 기능 반복
