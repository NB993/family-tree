## todo-ai
- [x] 도메인JpaEntity에서 setter 제거
- [x] 테스트코드에서 도메인JpaEntity를 기본생성자로 생성한 뒤 setter로 데이터 넣어주는 방식에서 도메인 엔티티를 생성한 뒤 도메인JpaEntity from 정적메서드에 넘겨서 생성하는 방식으로 변경
- [x] JPQL @Query 사용한 쿼리를 메서드 이름 기반 쿼리로 변경
  - [x] 변경하면서 메서드 바로 위에 주석으로 왜 JPQL로 작성했는지 이유를 남길 것

## todo-me
- 개발 지침 수정
  - [ ] 도메인JpaEntity 작성 규칙 추가
  - [ ] JpaRepository 메서드 작성 규칙 추가
