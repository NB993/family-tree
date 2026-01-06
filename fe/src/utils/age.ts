/**
 * 나이 계산 유틸리티
 * PRD-005: 멤버 목록 생일/나이 표시 기능
 */

/**
 * 한국 나이 (세는 나이) 계산
 * 태어난 해를 1살로 시작, 매년 1월 1일에 1살씩 증가
 * @param birthday 생년월일 (Date 또는 ISO string)
 * @returns 한국 나이
 */
export const getKoreanAge = (birthday: Date | string): number => {
  const birthDate = typeof birthday === 'string' ? new Date(birthday) : birthday;
  const today = new Date();
  return today.getFullYear() - birthDate.getFullYear() + 1;
};

/**
 * 만 나이 계산
 * 생일이 지났는지에 따라 나이 계산
 * @param birthday 생년월일 (Date 또는 ISO string)
 * @returns 만 나이
 */
export const getWesternAge = (birthday: Date | string): number => {
  const birthDate = typeof birthday === 'string' ? new Date(birthday) : birthday;
  const today = new Date();

  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDiff = today.getMonth() - birthDate.getMonth();

  // 생일이 아직 안 지났으면 1살 빼기
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age -= 1;
  }

  return age;
};

/**
 * 나이 표시 문자열 생성
 * @param birthday 생년월일
 * @param mode 표시 모드 ('korean' | 'western')
 * @returns 포맷된 나이 문자열 (예: "(35)" 또는 "(만 34)")
 */
export const formatAge = (birthday: Date | string, mode: 'korean' | 'western'): string => {
  if (mode === 'korean') {
    return `(${getKoreanAge(birthday)})`;
  }
  return `(만 ${getWesternAge(birthday)})`;
};
