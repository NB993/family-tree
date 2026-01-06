/**
 * 음력/양력 변환 유틸리티
 * PRD-005: 멤버 목록 생일/나이 표시 기능
 *
 * korean-lunar-calendar 라이브러리 사용
 */

import KoreanLunarCalendar from 'korean-lunar-calendar';
import { BirthdayType } from '../api/services/familyService';

const calendar = new KoreanLunarCalendar();

/**
 * 날짜를 YYYY.MM.DD 형식으로 포맷
 * @param date Date 객체 또는 ISO string
 * @returns 포맷된 날짜 문자열
 */
export const formatDate = (date: Date | string): string => {
  const d = typeof date === 'string' ? new Date(date) : date;
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${year}.${month}.${day}`;
};

/**
 * 월.일 형식으로 포맷 (연도 제외)
 * @param date Date 객체 또는 ISO string
 * @returns 포맷된 날짜 문자열 (MM.DD)
 */
export const formatMonthDay = (date: Date | string): string => {
  const d = typeof date === 'string' ? new Date(date) : date;
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${month}.${day}`;
};

/**
 * 양력 → 음력 변환
 * @param date 양력 날짜
 * @returns 음력 날짜 정보
 */
export const solarToLunar = (date: Date): { year: number; month: number; day: number; intercalation: boolean } => {
  calendar.setSolarDate(date.getFullYear(), date.getMonth() + 1, date.getDate());
  const lunar = calendar.getLunarCalendar();
  return {
    year: lunar.year,
    month: lunar.month,
    day: lunar.day,
    intercalation: lunar.intercalation
  };
};

/**
 * 음력 → 양력 변환
 * @param year 음력 연도
 * @param month 음력 월
 * @param day 음력 일
 * @param intercalation 윤달 여부
 * @returns 양력 Date 객체
 */
export const lunarToSolar = (year: number, month: number, day: number, intercalation: boolean = false): Date => {
  calendar.setLunarDate(year, month, day, intercalation);
  const solar = calendar.getSolarCalendar();
  return new Date(solar.year, solar.month - 1, solar.day);
};

/**
 * 생일 표시 문자열 생성
 * @param birthday 생년월일 (ISO string)
 * @param birthdayType 생일 유형 (SOLAR/LUNAR)
 * @param showConverted 변환된 날짜 표시 여부
 * @returns 포맷된 생일 문자열
 */
export const formatBirthday = (
  birthday: string,
  birthdayType: BirthdayType,
  showConverted: boolean = false
): string => {
  const date = new Date(birthday);

  // 기본 표시: 원본 날짜와 유형 라벨
  if (!showConverted) {
    const formattedDate = formatDate(date);
    if (birthdayType === 'LUNAR') {
      return `(음) ${formattedDate}`;
    }
    return formattedDate;
  }

  // 토글된 경우: 반대 유형으로 변환 표시
  if (birthdayType === 'LUNAR') {
    // 원본이 음력 -> 양력으로 변환
    // 생일 문자열을 음력으로 해석하여 양력으로 변환
    const solarDate = lunarToSolar(date.getFullYear(), date.getMonth() + 1, date.getDate());
    return formatDate(solarDate);
  }

  // 원본이 양력 -> 음력으로 변환
  const lunar = solarToLunar(date);
  const lunarStr = `${lunar.year}.${String(lunar.month).padStart(2, '0')}.${String(lunar.day).padStart(2, '0')}`;
  return `(음) ${lunarStr}`;
};

/**
 * 생일 표시용 간단한 포맷 (월.일만)
 * @param birthday 생년월일 (ISO string)
 * @param birthdayType 생일 유형
 * @returns 포맷된 생일 문자열 (MM.DD 또는 (음) MM.DD)
 */
export const formatBirthdayShort = (
  birthday: string,
  birthdayType: BirthdayType
): string => {
  const date = new Date(birthday);
  const formattedDate = formatMonthDay(date);

  if (birthdayType === 'LUNAR') {
    return `(음) ${formattedDate}`;
  }
  return formattedDate;
};
