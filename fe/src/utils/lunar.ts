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
 * 음력 월/일을 받아서 특정 연도의 양력 날짜를 반환
 * 어르신들의 음력 생일이 올해 양력으로 며칠인지 계산할 때 사용
 * @param lunarMonth 음력 월
 * @param lunarDay 음력 일
 * @param targetYear 대상 연도 (기본: 올해)
 * @returns 양력 Date 객체
 */
export const getThisYearSolarDate = (
  lunarMonth: number,
  lunarDay: number,
  targetYear: number = new Date().getFullYear()
): Date => {
  calendar.setLunarDate(targetYear, lunarMonth, lunarDay, false);
  const solar = calendar.getSolarCalendar();
  return new Date(solar.year, solar.month - 1, solar.day);
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
 * @returns 포맷된 생일 문자열
 */
export const formatBirthday = (
  birthday: string,
  birthdayType: BirthdayType
): string => {
  const date = new Date(birthday);
  const formattedDate = formatDate(date);

  if (birthdayType === 'LUNAR') {
    return `(음)${formattedDate}`;
  }
  return formattedDate;
};

/**
 * 음력 생일의 올해 양력 날짜를 M.DD 형식으로 반환
 * birthdayType이 LUNAR인 경우에만 사용
 * @param birthday 생년월일 (ISO string)
 * @returns 올해 양력 날짜 (M.DD 형식) 또는 null
 */
export const formatThisYearSolarBirthday = (
  birthday: string,
  targetYear: number = new Date().getFullYear()
): string => {
  const date = new Date(birthday);
  const lunarMonth = date.getMonth() + 1;
  const lunarDay = date.getDate();

  const thisYearSolar = getThisYearSolarDate(lunarMonth, lunarDay, targetYear);
  return formatMonthDay(thisYearSolar);
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
