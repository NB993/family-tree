/**
 * 음력/양력 변환 유틸리티 테스트
 * PRD-005: 멤버 목록 생일/나이 표시 기능
 */

import { formatDate, formatMonthDay, getThisYearSolarDate, lunarToSolar, formatBirthday, formatThisYearSolarBirthday, formatBirthdayShort } from '../lunar';

describe('lunar utilities', () => {
  describe('formatDate', () => {
    it('Date 객체를 YYYY.MM.DD 형식으로 포맷', () => {
      const date = new Date('1990-12-25');
      expect(formatDate(date)).toBe('1990.12.25');
    });

    it('ISO 문자열을 YYYY.MM.DD 형식으로 포맷', () => {
      expect(formatDate('2000-06-15')).toBe('2000.06.15');
    });

    it('한 자리 월/일을 0으로 패딩', () => {
      const date = new Date('2000-01-05');
      expect(formatDate(date)).toBe('2000.01.05');
    });
  });

  describe('formatMonthDay', () => {
    it('Date 객체를 MM.DD 형식으로 포맷', () => {
      const date = new Date('1990-12-25');
      expect(formatMonthDay(date)).toBe('12.25');
    });

    it('ISO 문자열을 MM.DD 형식으로 포맷', () => {
      expect(formatMonthDay('2000-06-15')).toBe('06.15');
    });
  });

  describe('getThisYearSolarDate', () => {
    it('음력 1월 1일(설날)을 2024년 양력으로 변환', () => {
      const solar = getThisYearSolarDate(1, 1, 2024);

      expect(solar.getFullYear()).toBe(2024);
      expect(solar.getMonth()).toBe(1);  // 2월 (0-indexed)
      expect(solar.getDate()).toBe(10);  // 2024년 설날은 2월 10일
    });

    it('음력 1월 1일(설날)을 2025년 양력으로 변환', () => {
      const solar = getThisYearSolarDate(1, 1, 2025);

      expect(solar.getFullYear()).toBe(2025);
      expect(solar.getMonth()).toBe(0);  // 1월 (0-indexed)
      expect(solar.getDate()).toBe(29);  // 2025년 설날은 1월 29일
    });

    it('같은 음력 날짜도 매년 양력 날짜가 다름', () => {
      const solar2024 = getThisYearSolarDate(12, 25, 2024);
      const solar2025 = getThisYearSolarDate(12, 25, 2025);

      // 같은 음력 날짜여도 양력으로 변환하면 다른 날짜
      expect(solar2024.getTime()).not.toBe(solar2025.getTime());
    });
  });

  describe('lunarToSolar', () => {
    it('음력 1990년 11월 9일을 양력으로 변환', () => {
      const solar = lunarToSolar(1990, 11, 9);

      expect(solar.getFullYear()).toBe(1990);
      expect(solar.getMonth()).toBe(11); // 12월 (0-indexed)
      expect(solar.getDate()).toBe(25);
    });

    it('음력 2024년 1월 1일 (설날)을 양력으로 변환', () => {
      const solar = lunarToSolar(2024, 1, 1);

      expect(solar.getFullYear()).toBe(2024);
      expect(solar.getMonth()).toBe(1);  // 2월 (0-indexed)
      expect(solar.getDate()).toBe(10);
    });
  });

  describe('formatBirthday', () => {
    it('양력 생일은 날짜만 표시', () => {
      const result = formatBirthday('1990-12-25', 'SOLAR');
      expect(result).toBe('1990.12.25');
    });

    it('음력 생일은 (음) 라벨과 함께 표시 (공백 없음)', () => {
      const result = formatBirthday('1990-12-25', 'LUNAR');
      expect(result).toBe('(음)1990.12.25');
    });

    it('birthdayType이 null이면 양력으로 표시', () => {
      const result = formatBirthday('1990-12-25', null);
      expect(result).toBe('1990.12.25');
    });
  });

  describe('formatThisYearSolarBirthday', () => {
    it('음력 1월 1일 생일의 2025년 양력 날짜를 반환', () => {
      // 2025년 설날은 양력 1월 29일
      const result = formatThisYearSolarBirthday('1990-01-01', 2025);
      expect(result).toBe('01.29');
    });

    it('음력 1월 1일 생일의 2024년 양력 날짜를 반환', () => {
      // 2024년 설날은 양력 2월 10일
      const result = formatThisYearSolarBirthday('1990-01-01', 2024);
      expect(result).toBe('02.10');
    });

    it('같은 음력 생일도 매년 양력 날짜가 다름', () => {
      const result2024 = formatThisYearSolarBirthday('1990-01-01', 2024);
      const result2025 = formatThisYearSolarBirthday('1990-01-01', 2025);
      expect(result2024).not.toBe(result2025);
    });
  });

  describe('formatBirthdayShort', () => {
    it('양력 생일은 MM.DD 형식으로 표시', () => {
      const result = formatBirthdayShort('1990-12-25', 'SOLAR');
      expect(result).toBe('12.25');
    });

    it('음력 생일은 (음) MM.DD 형식으로 표시', () => {
      const result = formatBirthdayShort('1990-12-25', 'LUNAR');
      expect(result).toBe('(음) 12.25');
    });

    it('birthdayType이 null이면 양력으로 표시', () => {
      const result = formatBirthdayShort('1990-12-25', null);
      expect(result).toBe('12.25');
    });
  });
});
